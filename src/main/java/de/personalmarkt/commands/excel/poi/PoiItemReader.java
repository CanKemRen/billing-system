package de.personalmarkt.commands.excel.poi;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import de.personalmarkt.commands.excel.support.DefaultRowSetFactory;
import de.personalmarkt.commands.excel.support.ExcelFileParseException;
import de.personalmarkt.commands.excel.support.RowCallbackHandler;
import de.personalmarkt.commands.excel.support.RowMapper;
import de.personalmarkt.commands.excel.support.RowSet;
import de.personalmarkt.commands.excel.support.RowSetFactory;
import de.personalmarkt.commands.excel.support.Sheet;
import de.personalmarkt.commands.CommandHelper;

/**
 * {@link org.springframework.batch.item.ItemReader} implementation which uses apache POI to read an Excel file. It will read the file sheet for sheet and row
 * for row. It is based on the {@link org.springframework.batch.item.file.FlatFileItemReader}
 *
 * @param <T>
 *            the type
 * 
 */
public class PoiItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements
		ResourceAwareItemReaderItemStream<T>, InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());
	private Resource resource;
	private int linesToSkip = 0;
	private int currentSheet = 0;
	private RowMapper<T> rowMapper;
	private RowCallbackHandler skippedRowsCallback;
	private boolean noInput = false;
	private boolean strict = true;
	private RowSetFactory rowSetFactory = new DefaultRowSetFactory();
	private RowSet rs;

	private String path;
	private String filename;

	public PoiItemReader() {
		super();
		this.setName(ClassUtils.getShortName(this.getClass()));
	}

	private CommandHelper helper = new CommandHelper();

	private Workbook workbook;

	private InputStream workbookStream;

	public Sheet getSheet(final int sheet) {
		return new PoiSheet(this.workbook.getSheetAt(sheet));
	}

	protected int getNumberOfSheets() {
		return this.workbook.getNumberOfSheets();
	}

	@Override
	protected void doClose() throws Exception {
		// As of Apache POI 3.11 there is a close method on the Workbook, prior version
		// lack this method.
		if (workbook instanceof Closeable) {
			this.workbook.close();
		}

		if (workbookStream != null) {
			workbookStream.close();
		}
		this.workbook = null;
		this.workbookStream = null;
	}

	/**
	 * Open the underlying file using the {@code WorkbookFactory}. We keep track of the used {@code InputStream} so that it can be closed cleanly on the end of
	 * reading the file. This to be able to release the resources used by Apache POI.
	 *
	 * @param path
	 * @param filename
	 *            the {@code Resource} pointing to the Excel file.
	 * @throws Exception
	 *             is thrown for any errors.
	 */

	public void openExcelFile(String path, String filename) throws Exception {
		try {
			workbookStream = helper.getResourceAsStream(path, filename);

			this.workbook = WorkbookFactory.create(workbookStream);
			this.workbook.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);
			this.openSheet();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
		}
		catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return string corresponding to logical record according to {@link #setRowMapper(RowMapper)} (might span multiple rows in file).
	 */
	@Override
	public T doRead() throws Exception {
		if (this.noInput || this.rs == null) {
			return null;
		}

		if (rs.next()) {
			try {
				return this.rowMapper.mapRow(rs);
			}
			catch (final Exception e) {
				throw new ExcelFileParseException("Exception parsing Excel file.", e, this.resource.getDescription(),
						rs.getMetaData().getSheetName(), rs.getCurrentRowIndex(), rs.getCurrentRow());
			}
		}
		else {
			this.currentSheet++;
			if (this.currentSheet >= this.getNumberOfSheets()) {
				if (logger.isDebugEnabled()) {
					logger.debug("No more sheets in '" + this.resource.getDescription() + "'.");
				}
				return null;
			}
			else {
				this.openSheet();
				return this.doRead();
			}
		}
	}

	@Override
	public void doOpen() throws Exception {

		this.openExcelFile(this.path, this.filename);
		this.openSheet();
		this.noInput = false;

		logger.info("Opened workbook [" + this.resource.getFilename() + "] with " + this.getNumberOfSheets() + " sheets.");

	}

	public void openSheet() {
		final Sheet sheet = this.getSheet(this.currentSheet);
		this.rs = rowSetFactory.create(sheet);

		if (logger.isDebugEnabled()) {
			logger.debug("Opening sheet " + sheet.getName() + ".");
		}

		for (int i = 0; i < this.linesToSkip; i++) {
			if (rs.next() && this.skippedRowsCallback != null) {
				this.skippedRowsCallback.handleRow(rs);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Openend sheet " + sheet.getName() + ", with " + sheet.getNumberOfRows() + " rows.");
		}

	}

	/**
	 * Public setter for the input resource.
	 *
	 * @param resource
	 *            the {@code Resource} pointing to the Excelfile
	 */
	public void setResource(final Resource resource) {
		this.resource = resource;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.rowMapper, "RowMapper must be set");
	}

	/**
	 * Set the number of lines to skip. This number is applied to all worksheet in the excel file! default to 0
	 *
	 * @param linesToSkip
	 *            number of lines to skip
	 */
	public void setLinesToSkip(final int linesToSkip) {
		this.linesToSkip = linesToSkip;
	}

	/**
	 * In strict mode the reader will throw an exception on {@link #open(org.springframework.batch.item.ExecutionContext)} if the input resource does not exist.
	 *
	 * @param strict
	 *            true by default
	 */
	public void setStrict(final boolean strict) {
		this.strict = strict;
	}

	/**
	 * Public setter for the {@code rowMapper}. Used to map a row read from the underlying Excel workbook.
	 *
	 * @param rowMapper
	 *            the {@code RowMapper} to use.
	 */
	public void setRowMapper(final RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	/**
	 * Public setter for the <code>rowSetFactory</code>. Used to create a {@code RowSet} implemenation. By default the {@code DefaultRowSetFactory} is used.
	 *
	 * @param rowSetFactory
	 *            the {@code RowSetFactory} to use.
	 */
	public void setRowSetFactory(RowSetFactory rowSetFactory) {
		this.rowSetFactory = rowSetFactory;
	}

	/**
	 * @param skippedRowsCallback
	 *            will be called for each one of the initial skipped lines before any items are read.
	 */
	public void setSkippedRowsCallback(final RowCallbackHandler skippedRowsCallback) {
		this.skippedRowsCallback = skippedRowsCallback;
	}

	public Resource getResource() {
		return resource;
	}

	public int getLinesToSkip() {
		return linesToSkip;
	}

	public int getCurrentSheet() {
		return currentSheet;
	}

	public void setCurrentSheet(int currentSheet) {
		this.currentSheet = currentSheet;
	}

	public RowMapper<T> getRowMapper() {
		return rowMapper;
	}

	public RowCallbackHandler getSkippedRowsCallback() {
		return skippedRowsCallback;
	}

	public boolean isNoInput() {
		return noInput;
	}

	public void setNoInput(boolean noInput) {
		this.noInput = noInput;
	}

	public boolean isStrict() {
		return strict;
	}

	public RowSetFactory getRowSetFactory() {
		return rowSetFactory;
	}

	public RowSet getRs() {
		return rs;
	}

	public void setRs(RowSet rs) {
		this.rs = rs;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public CommandHelper getHelper() {
		return helper;
	}

	public void setHelper(CommandHelper helper) {
		this.helper = helper;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public InputStream getWorkbookStream() {
		return workbookStream;
	}

	public void setWorkbookStream(InputStream workbookStream) {
		this.workbookStream = workbookStream;
	}
}
