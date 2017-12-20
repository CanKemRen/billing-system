package de.personalmarkt.commands.occupation;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import de.personalmarkt.commands.BaseField;
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
public class OccupationImporterCommands implements CommandMarker {

	private static final Map<String, BaseTable> map = new HashMap<>();

	private boolean simpleCommandExecuted = false;

	@Autowired
	private ExcelHelper excelHelper;

	@Autowired
	private CommandHelper helper;

	static {
		setUp();

	}

	@CliAvailabilityIndicator({ "oi simple" })
	public boolean isSimpleAvailable() {
		// always available
		return true;
	}

	@CliAvailabilityIndicator({ "echo" })
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
	@CliCommand(value = "echo", help = "Echo a message")
	public String echo(
			@CliOption(key = { "", "msg" }, mandatory = true, help = "The message to echo") String msg) {
		return msg;
	}

	/**
	 * oi simple --pn jobrobor --pi 1234 --f test.csv --path /tmp
	 *
	 * or
	 *
	 * oi simple --pn jobrobor --pi 1234 --f test.xlsx --path /tmp
	 *
	 * @param partnerName
	 * @param partnerId
	 * @param file
	 * @param path
	 * @return
	 */
	@CliCommand(value = "oi simple", help = "importing occupation from csv")
	public String occupationImport(
			@CliOption(key = { "partnerOccupationName2", "pn" }, mandatory = true, help = "give please partner name") String partnerName,
			@CliOption(key = { "occupationId", "pi" }, mandatory = true, help = "give please partner id") String partnerId,
			@CliOption(key = { "file", "f" }, mandatory = true, help = "give please file name") String file,
			@CliOption(key = { "path", "p" }, mandatory = true, help = "give please path") String path) {
		StringBuilder builder = new StringBuilder();
		String filename = "test" + CommandHelper.CSV_FILE_ENDING;
		try {
			path = helper.getPath(path);
			filename = helper.getFilename(file, filename);

			builder = helper.createBuilder(partnerName, partnerId, path, filename);

			if (helper.checkFileEnding(filename)) {
				builder.append("\n--csv file... \n");
				executeDataFromCsv(partnerName, partnerId, path, builder, filename);
			}
			else {
				builder.append("\n--xlsx file... \n");
				executeDataFromXlsx(partnerName, partnerId, path, builder, filename);
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return builder.toString();

	}

	private void executeDataFromCsv(String partnerName, String partnerId, String path, StringBuilder builder, String filename) throws IOException {
		// read csv
		CsvMapper mapper = new CsvMapper();
		mapper.enable(JsonParser.Feature.ALLOW_MISSING_VALUES);
		mapper.enable(com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);

		CsvSchema schema = mapper
			.schemaFor(OccupationCsvLine.class)
			.withAllowComments(true)
			.withColumnSeparator(';');

		Stream<String> csvLines = new BufferedReader(new InputStreamReader(helper.getResourceAsStream(path, filename))).lines();
		javaslang.collection.List<OccupationCsvLine> csvList = javaslang.collection.List.ofAll(csvLines.collect(toList()))
			.filter(helper::isValidLine)
			.flatMap(s -> helper.toOccupationCsvLine(mapper, schema, s));
		csvList.sliding(2);

		builder.append(createSql(partnerId, csvList, 1000));

		File sqlFilename = helper.saveFile(partnerName, path, "berufe", builder.toString());

		helper.closeExecution(builder, sqlFilename);
	}

	private void executeDataFromXlsx(String partnerName, String partnerId, String path, StringBuilder builder, String filename) throws IOException {
		try {
			int counter = 1000;
			String insertMapSql = createMapBerufeTemplate();
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

			File sqlFilename = helper.saveFile(partnerName, path, "berufe", builder.toString());

			helper.closeExecution(builder, sqlFilename);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String createSql(String partnerId, javaslang.collection.List<OccupationCsvLine> list, int counter) {
		StringBuilder builder = new StringBuilder();

		String insertMapSql = createMapBerufeTemplate();
		for (OccupationCsvLine line : list) {
			try {
				String caption = "'" + line.getPartnerOccupationName2() + "'";
				String externeId = String.valueOf(StringUtils.isEmpty(line.getPartnerOccupationId()) ? counter : line.getPartnerOccupationId());
				List<String> occupationList = line.getOccupationId();

				builder.append(appendSql(partnerId, insertMapSql, caption, externeId, occupationList));
				counter++;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return builder.toString();
	}

	private String appendSql(String partnerId, String insertMapSql, String caption, String externeId, List<String> occupationList) {
		StringBuilder builder = new StringBuilder();
		Map map = new HashMap();
		map.put(CommandHelper.CAPTION, caption);
		map.put(CommandHelper.EXTERNE_ID, externeId);
		map.put(CommandHelper.FK_PARTNER_ID, partnerId);

		// System.out.println(occupationList + "\n");
		builder.append(MapFormat.format(CommandHelper.INSERT_EXT_OCCUPATION_, map));
		for (String occupationId : occupationList) {
			createMapInterneOccupation(insertMapSql, builder, map, occupationId);
		}
		builder.append("\n");

		return builder.toString();
	}

	private void createMapInterneOccupation(String insertMapSql, StringBuilder builder, Map map, String occupationId) {

		try {
			Map<String, Integer> occupationmap = new HashMap();
			map.put(CommandHelper.FK_BERUF_ID, helper.getIntegerValueFromString(occupationId));
			builder.append(MapFormat.format(insertMapSql, map));
			builder.append("\n");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String createSQL() {
		StringBuilder sb = new StringBuilder();

		BaseTable value = map.get(CommandHelper.TBL_ANZ_EXT_BERUFE);
		getSQL(sb, value, 0);
		// createMapBerufeTemplate(sb);

		return sb.toString();
	}

	private static String createMapBerufeTemplate() {
		StringBuilder sb = new StringBuilder();
		BaseTable value;
		value = map.get(CommandHelper.TBL_ANZ_MAP_BERUFE);
		getSQL(sb, value, value.getFieldList().size());

		return sb.toString();
	}

	private static void getSQL(StringBuilder sb, BaseTable value, int startIndex) {
		if (BaseTable.Operation.INSERT.equals(value.getOperation())) {

			sb.append(CommandHelper.INSERT_INTO);
			sb.append(value.getTableName());

			sb.append("(");
			String fieldList = "";
			int index = startIndex;
			String tmp = "";
			for (BaseField field : value.getFieldList()) {
				fieldList += field.getFieldName() + ",";

				if (BaseField.FieldType.SERIAL.equals(field.getFieldType())) {
					tmp += "nextval('" + field.getSequence() + "')";
				}
				else if (BaseField.FieldType.INTEGER.equals(field.getFieldType()) && !StringUtils.isEmpty(field.getSequence())) {
					tmp += "currval('" + field.getSequence() + "')";
				}
				else {
					tmp += "{" + field.getFieldName() + "}";
					index++;
				}
				tmp += ",";
			}
			sb.append(fieldList.substring(0, fieldList.length() - 1));

			sb.append(") VALUES(");

			sb.append(tmp.substring(0, tmp.length() - 1) + ");\n");

		}
	}

	private static void setUp() {
		// nextval('tbl_locale_page_id_seq')
		// currval('tbl_locale_page_id_seq')
		// config for tbl_anz_ext_berufe
		Set<BaseField> fieldSet = new HashSet<>();
		fieldSet.add(new BaseField().setFieldName("id").setFieldType(BaseField.FieldType.SERIAL).setSequence(CommandHelper.SEQUENCE_EXT));
		fieldSet.add(new BaseField().setFieldName(CommandHelper.FK_PARTNER_ID).setFieldType(BaseField.FieldType.INTEGER));
		fieldSet.add(new BaseField().setFieldName(CommandHelper.EXTERNE_ID).setFieldType(BaseField.FieldType.INTEGER));
		fieldSet.add(new BaseField().setFieldName(CommandHelper.CAPTION).setFieldType(BaseField.FieldType.STRING));

		map.put(CommandHelper.TBL_ANZ_EXT_BERUFE,
				new BaseTable().setOperation(BaseTable.Operation.INSERT).setTableName("tbl_anz_ext_berufe").setFieldList(fieldSet)
					.setSequence(CommandHelper.SEQUENCE_EXT));

		// config for tbl_anz_map_berufe
		fieldSet = new HashSet<>();
		fieldSet.add(new BaseField().setFieldName("id").setFieldType(BaseField.FieldType.SERIAL).setSequence(CommandHelper.SEQUENCE_MAP));
		fieldSet.add(new BaseField().setFieldName(CommandHelper.FK_BERUF_ID).setFieldType(BaseField.FieldType.INTEGER));
		fieldSet.add(new BaseField().setFieldName("fk_ext_berufe_id").setFieldType(BaseField.FieldType.INTEGER).setSequence(CommandHelper.SEQUENCE_EXT));

		map.put(CommandHelper.TBL_ANZ_MAP_BERUFE,
				new BaseTable().setOperation(BaseTable.Operation.INSERT).setTableName("tbl_anz_map_berufe").setFieldList(fieldSet)
					.setSequence(CommandHelper.SEQUENCE_MAP));

		CommandHelper.INSERT_EXT_OCCUPATION_ = createSQL();
	}

}
