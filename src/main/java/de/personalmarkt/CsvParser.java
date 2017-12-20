package de.personalmarkt;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import de.personalmarkt.commands.CommandHelper;
import javaslang.collection.List;

/**
 * Created by jonas.taenzer on 05.05.2017.
 */
public class CsvParser {

	private final Integer partnerId;
	private final String filename;

	private CommandHelper helper = new CommandHelper();

	public CsvParser(String filename, Integer partnerId) {
		this.filename = filename;
		this.partnerId = partnerId;

		CsvMapper mapper = new CsvMapper();
		mapper.enable(JsonParser.Feature.ALLOW_MISSING_VALUES);
		mapper.enable(com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);

		CsvSchema schema = mapper
			.schemaFor(IndustryCsvLine.class)
			.withAllowComments(true)
			.withColumnSeparator(';');

		Stream<String> csvLines = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/" + filename))).lines();
		List<IndustryCsvLine> csvList = List.ofAll(csvLines.collect(toList()))
			.filter(helper::isValidLine)
			.flatMap(s -> helper.toIndustryCsvLine(mapper, schema, s));
		csvList.sliding(2).forEach(lb -> helper.copyIfNotPresent(lb.get(0), lb.get(1)));
		/*
		 * Exchange the printQuery call for desired SQL-output TODO: Extract the methods to interface/function
		 */
		csvList.forEach(this::printInsertJobAdIndustriesPartnerQuery);
	}

	private void printUpdateJobAdIndustriesPartnerQuery(IndustryCsvLine line) {
		/*
		 * TODO: Change sout to custom "printer" to enable unit-testability.
		 */
		System.out.println(
				"UPDATE job_ad_industries_partner SET job_ad_industries_partner_caption = '" + line.getPmName() + "' " +
						"WHERE job_ad_industries_partner_id = " + line.getPmId() + " " +
						"AND partner_id = " + partnerId + ";");
	}

	private void printJobAdIndustriesMappingQuery(IndustryCsvLine line) {
		System.out.println(
				"INSERT INTO job_ad_industries_mapping(job_ad_industries_id, industries_id) " +
						"\nVALUES (" + line.getPmId() + ", " + line.getPartnerId() + ");");
	}

	private void printInsertJobAdIndustriesPartnerQuery(IndustryCsvLine line) {
		System.out.println(
				"INSERT INTO job_ad_industries_partner(job_ad_industries_id, job_ad_industries_partner_id, job_ad_industries_partner_caption, partner_id) " +
						"\nVALUES (" + line.getPmId() + ", " + line.getPartnerId() + ", '" + line.getPartnerName() + "', " + partnerId + ");");
	}

}
