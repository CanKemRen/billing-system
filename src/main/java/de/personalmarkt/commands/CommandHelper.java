package de.personalmarkt.commands;

import static org.apache.commons.lang3.StringUtils.countMatches;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import de.personalmarkt.IndustryCsvLine;
import de.personalmarkt.commands.occupation.OccupationCsvLine;
import javaslang.control.Option;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 10.07.17
 */
@Component
public class CommandHelper {

	public static final String FK_PARTNER_ID = "fk_partner_id";
	public static final String EXTERNE_ID = "externe_id";
	public static final String CAPTION = "caption";
	public static final String FK_BERUF_ID = "fk_beruf_id";

	public static final String INSERT_INTO = "INSERT INTO public.";

	public static String INSERT_EXT_OCCUPATION_ = INSERT_INTO;

	public static final String TBL_ANZ_EXT_BERUFE = "tbl_anz_ext_berufe";
	public static final String TBL_ANZ_MAP_BERUFE = "tbl_anz_map_berufe";

	public static final String SEQUENCE_EXT = "tbl_anz_ext_berufe_seq";
	public static final String SEQUENCE_MAP = "tbl_anz_map_berufe_seq";
	public static final String CSV_FILE_ENDING = ".csv";

	public StringBuilder createBuilder(String partnerName, String partnerId, String path, String filename) {
		StringBuilder builder = new StringBuilder();
		builder.append("--input Data [partnerName=");
		builder.append(partnerName);
		builder.append(", partnerId=");
		builder.append(partnerId);
		builder.append(", filename=");
		builder.append(filename);
		builder.append(", path=");
		builder.append(path);
		builder.append("\n");

		return builder;
	}

	public Integer getIntegerValueFromString(String value) {

		value = value.trim();
		Double d = Double.parseDouble(value);

		Integer tmp = d.intValue();

		return tmp;
	}

	public void copyIfNotPresent(IndustryCsvLine b1, IndustryCsvLine b2) {
		if (b1.getPmId() != null && b2.getPmId() == null) {
			b2.setPmId(b1.getPmId());
		}
		if (!b1.getPmName().isEmpty() && b2.getPmName().isEmpty()) {
			b2.setPmName(b1.getPmName());
		}
	}

	public boolean isValidLine(String line) {
		return !StringUtils.isBlank(line.replaceAll(";", "")) && !line.contains("Schlagwort");
	}

	public Option<IndustryCsvLine> toIndustryCsvLine(CsvMapper mapper, CsvSchema schema, String s) {
		try {
			// Hackfix that needs proper solution, see issue: https://github.com/FasterXML/jackson-dataformat-csv/issues/137#issuecomment-260038955
			if (countMatches(s, ";") < schema.size() - 1) {
				throw new IllegalArgumentException("CSV hat nicht die richtige Anzahl von Feldern");
			}
			IndustryCsvLine line = mapper.readerFor(IndustryCsvLine.class).with(schema).readValue(s);
			return line.getPartnerId() != null ? Option.of(line) : Option.none();
		}
		catch (Exception e) {
			return Option.none();
		}
	}

	public Option<OccupationCsvLine> toOccupationCsvLine(CsvMapper mapper, CsvSchema schema, String s) {
		try {
			// Hackfix that needs proper solution, see issue: https://github.com/FasterXML/jackson-dataformat-csv/issues/137#issuecomment-260038955
			if (countMatches(s, ";") < schema.size() - 1) {
				throw new IllegalArgumentException("CSV hat nicht die richtige Anzahl von Feldern");
			}
			OccupationCsvLine line = mapper.readerFor(OccupationCsvLine.class).with(schema).readValue(s);
			return Option.of(line);
		}
		catch (Exception e) {
			return Option.none();
		}
	}

	public InputStream getResourceAsStream(String path, String filename) {
		String name = path + "/" + filename;
		InputStream inputStream = null;
		try {
			File file = new File(name);
			inputStream = new FileInputStream(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	public File saveFile(String partnerName, String path, String mapType, String sql) throws IOException {

		Path file = null;
		BufferedWriter bw = null;
		try {
			file = Files
				.createFile(
						Paths.get(path + "/V" + (LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))) + "__insert_" + mapType + "_map_for_"
								+ partnerName.trim().replaceAll(" ", "_") + ".sql"));
			bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
			bw.write(sql);

		}
		finally {
			if (bw != null) {
				bw.close();
			}
		}
		return file != null ? file.toFile() : null;
	}

	/**
	 * is file ending csv? default false
	 *
	 * @param filename
	 * @return
	 */
	public Boolean checkFileEnding(String filename) {
		Boolean csvFile = false;
		String fileEnding = filename.substring(filename.lastIndexOf('.'), filename.length());
		if (CSV_FILE_ENDING.equals(fileEnding)) {
			csvFile = true;
		}
		return csvFile;
	}

	public String getPath(@CliOption(key = { "path", "p" }, mandatory = true, help = "give please path") String path) {
		if (org.springframework.util.StringUtils.isEmpty(path)) {
			path = "/tmp";
		}
		return path;
	}

	public String getFilename(@CliOption(key = { "file", "f" }, mandatory = true, help = "give please file name") String file, String filename) {
		if (!org.springframework.util.StringUtils.isEmpty(file)) {
			filename = file;
		}
		return filename;
	}

	public void closeExecution(StringBuilder builder, File sqlFilename) {
		builder.append("\n");
		builder.append("\n");
		builder.append(sqlFilename + " erstellt.");
	}

}
