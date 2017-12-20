package de.personalmarkt.commands.occupation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author kemal
 */
@JsonPropertyOrder({ "partnerid", "partner_beruf_id", "partner_beruf_name1", "partner_beruf_name2", "val_beruf" })
public class OccupationCsvLine {

	private Integer partnerId;
	private String partnerOccupationId;
	private String partnerOccupationName1;
	private String partnerOccupationName2;

	private List<String> occupationId;

	@JsonProperty("partnerid")
	public Integer getPartnerId() {
		return partnerId;
	}

	@JsonProperty("partner_beruf_id")
	public String getPartnerOccupationId() {
		return partnerOccupationId;
	}

	@JsonProperty("partner_beruf_name1")
	public String getPartnerOccupationName1() {
		return partnerOccupationName1;
	}

	@JsonProperty("partner_beruf_name2")
	public String getPartnerOccupationName2() {
		return partnerOccupationName2;
	}

	@JsonProperty("val_beruf")
	public List<String> getOccupationId() {

		return occupationId;
	}

	public OccupationCsvLine setPartnerOccupationId(String partnerOccupationId) {
		this.partnerOccupationId = partnerOccupationId;
		return this;
	}

	public OccupationCsvLine setPartnerOccupationName1(String partnerOccupationName1) {
		this.partnerOccupationName1 = partnerOccupationName1;
		return this;
	}

	public OccupationCsvLine setPartnerOccupationName2(String partnerOccupationName2) {
		this.partnerOccupationName2 = partnerOccupationName2;
		return this;
	}

	public OccupationCsvLine setOccupationId(String occupationId) {

		this.occupationId = StringUtils.isEmpty(occupationId) ? new ArrayList<>() : Arrays.asList(occupationId.split(","));
		return this;
	}

	public OccupationCsvLine setPartnerId(Integer partnerId) {
		this.partnerId = partnerId;
		return this;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("OccupationCsvLine{");
		sb.append("partnerId=").append(partnerId);
		sb.append(", partnerOccupationId=").append(partnerOccupationId);
		sb.append(", partnerOccupationName1='").append(partnerOccupationName1).append('\'');
		sb.append(", partnerOccupationName2='").append(partnerOccupationName2).append('\'');
		sb.append(", occupationId=").append(occupationId);
		sb.append('}');
		return sb.toString();
	}
}
