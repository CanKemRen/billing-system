package de.personalmarkt.commands.excel;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import de.personalmarkt.commands.excel.poi.PoiItemReader;
import de.personalmarkt.commands.excel.support.RowMapper;
import de.personalmarkt.commands.CommandHelper;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 17.07.17
 */
@Component
public class ExcelHelper {

	// @Autowired
	// private ExcelRowMapper excelRowMapper;

	@Autowired
	private CommandHelper helper;

	/**
	 *
	 * @param filename
	 *            ("data/students.xlsx")
	 * @return
	 */
	public ItemReader<ExcelSheetDto> excelReader(String path, String filename) throws Exception {
		String name = path + "/" + filename;

		PoiItemReader<ExcelSheetDto> reader = new PoiItemReader<>();
		reader.setLinesToSkip(1);
		reader.setResource(new ClassPathResource(name));
		reader.setRowMapper(excelRowMapper());
		reader.setPath(path);
		reader.setFilename(filename);
		reader.doOpen();

		System.out.println(reader.getRowMapper());

		return reader;
	}

	private RowMapper<ExcelSheetDto> excelRowMapper() {
		// BeanWrapperRowMapper<ExcelSheetDto> rowMapper = new BeanWrapperRowMapper<>();
		// rowMapper.setTargetType(ExcelSheetDto.class);
		// rowMapper.mapRow(excelRowMapper.mapRow());
		// return rowMapper;
		return new ExcelRowMapper();
	}
}
