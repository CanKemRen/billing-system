package de.personalmarkt.commands.excel;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.personalmarkt.commands.excel.support.RowMapper;
import de.personalmarkt.commands.excel.support.RowSet;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 17.07.17
 */
@Component
public class ExcelRowMapper implements RowMapper<ExcelSheetDto> {

	@Override
	public ExcelSheetDto mapRow(RowSet rowSet) throws Exception {
		ExcelSheetDto sheet = new ExcelSheetDto();

		try {
			if (rowSet.getCurrentRow() != null) {

				String tmp = rowSet.getColumnValue(2);

				sheet.setExterneName(rowSet.getColumnValue(1))
					.setExterneId(rowSet.getColumnValue(0))

					.setInterneIdList(StringUtils.isEmpty(tmp) ? new ArrayList<>() : Arrays.asList(tmp.split(",")));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return sheet;
	}
}
