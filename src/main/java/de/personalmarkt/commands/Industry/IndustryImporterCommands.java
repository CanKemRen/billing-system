package de.personalmarkt.commands.Industry;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.personalmarkt.commands.BaseTable;
import de.personalmarkt.commands.CommandHelper;
import de.personalmarkt.commands.excel.ExcelHelper;
import de.personalmarkt.commands.excel.ExcelSheetDto;
import de.personalmarkt.commands.format.MapFormat;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 05.07.17
 */
@Component
public class IndustryImporterCommands implements CommandMarker {

	private static final Map<String, BaseTable> map = new HashMap<>();

	public static final String JOB_AD_INDUSTRIES_ID = "job_ad_industries_id";
	public static final String PARTNER_ID = "partner_id";
	public static final String JOB_AD_INDUSTRIES_PARTNER_ID = "job_ad_industries_" + PARTNER_ID;
	public static final String JOB_AD_INDUSTRIES_PARTNER = "job_ad_industries_partner";
	public static final String JOB_AD_INDUSTRIES_PARTNER_CAPTION = JOB_AD_INDUSTRIES_PARTNER + "_caption";

	private static final String SQL_TEMPLATE_FOR_INDUSTRY_MAP = "INSERT INTO " + JOB_AD_INDUSTRIES_PARTNER + " " +
			"(" + JOB_AD_INDUSTRIES_ID + ", " + JOB_AD_INDUSTRIES_PARTNER_ID + ", " + JOB_AD_INDUSTRIES_PARTNER_CAPTION + ", " + PARTNER_ID + ") " +
			"VALUES ({" + JOB_AD_INDUSTRIES_ID + "}, {" + JOB_AD_INDUSTRIES_PARTNER_ID + "}, {" + JOB_AD_INDUSTRIES_PARTNER_CAPTION + "}, {" + PARTNER_ID
			+ "});";

	private boolean simpleCommandExecuted = false;

	@Autowired
	private ExcelHelper excelHelper;

	@Autowired
	private CommandHelper helper;

	@CliAvailabilityIndicator({ "ii simple" })
	public boolean isSimpleAvailable() {
		// always available
		return true;
	}

	@CliAvailabilityIndicator({ "echoI" })
	public boolean isComplexAvailable() {
		if (simpleCommandExecuted) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	@CliCommand(value = "echoI", help = "Echo a message")
	public String echo(
			@CliOption(key = { "", "msg" }, mandatory = true, help = "The message to echo") String msg) {
		return msg;
	}

	/**
	 * ii simple --pn jobrobot --pi 1234 --f jobrobot_ind.xlsx --path /Users/kemal/tmp
	 * 
	 * @param partnerName
	 * @param partnerId
	 * @param file
	 * @param path
	 * @return
	 */
	@CliCommand(value = "ii simple", help = "importing occupation from csv")
	public String occupationImport(
			@CliOption(key = { "industryPartnerName2", "pn" }, mandatory = true, help = "give please partner name") String partnerName,
			@CliOption(key = { "industryPartnerId", "pi" }, mandatory = true, help = "give please partner id") String partnerId,
			@CliOption(key = { "file", "f" }, mandatory = true, help = "give please file name") String file,
			@CliOption(key = { "path", "p" }, mandatory = true, help = "give please path") String path) {
		StringBuilder builder = new StringBuilder();
		String filename = "test.xslx";
		try {
			path = helper.getPath(path);
			filename = helper.getFilename(file, filename);

			builder = helper.createBuilder(partnerName, partnerId, path, filename);

			// read csv
			executeDataFromXlsx(partnerName, partnerId, path, builder, filename);

		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return builder.toString();

	}

	private void executeDataFromXlsx(String partnerName, String partnerId, String path, StringBuilder builder, String filename) throws IOException {
		try {
			int counter = 1000;
			String insertMapSql = SQL_TEMPLATE_FOR_INDUSTRY_MAP;
			ItemReader<ExcelSheetDto> itemReader = excelHelper.excelReader(path, filename);

			ExcelSheetDto row = null;
			do {
				try {
					row = itemReader.read();
					if (row != null && !StringUtils.isEmpty(row.getExterneName())) {

						String caption = "'" + row.getExterneName() + "'";
						String externeId = String.valueOf(StringUtils.isEmpty(row.getExterneId()) ? counter : row.getExterneId());
						List<String> occupationList = row.getInterneIdList();

						String appendSql = appendSql(partnerId, insertMapSql, caption, externeId, occupationList);
						builder.append(appendSql);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				counter++;

			}
			while (row != null);

			File sqlFilename = helper.saveFile(partnerName, path, "industry", builder.toString());

			helper.closeExecution(builder, sqlFilename);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String appendSql(String partnerId, String insertMapSql, String caption, String externeId, List<String> industryList) {
		StringBuilder builder = new StringBuilder();
		for (String interneId : industryList) {
			Map map = new HashMap();
			map.put(JOB_AD_INDUSTRIES_PARTNER_CAPTION, caption);
			map.put(JOB_AD_INDUSTRIES_PARTNER_ID, externeId);
			map.put(PARTNER_ID, partnerId);
			map.put(JOB_AD_INDUSTRIES_ID, helper.getIntegerValueFromString(interneId));

			// System.out.println(occupationList + "\n");
			String appendSql = MapFormat.format(insertMapSql, map);
			builder.append(appendSql);
		}

		builder.append("\n");

		return builder.toString();
	}

}
