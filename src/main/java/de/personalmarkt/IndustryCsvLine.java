package de.personalmarkt;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by jonas.taenzer on 05.05.2017.
 */
@JsonPropertyOrder({ "pmId", "pmName", "partnerName", "partnerId" })
public class IndustryCsvLine {

	Integer pmId;
	String pmName;
	String partnerName;
	Integer partnerId;

	public Integer getPmId() {
		return pmId;
	}

	public IndustryCsvLine setPmId(Integer pmId) {
		this.pmId = pmId;
		return this;
	}

	public String getPmName() {
		return pmName;
	}

	public IndustryCsvLine setPmName(String pmName) {
		this.pmName = pmName;
		return this;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public IndustryCsvLine setPartnerName(String partnerName) {
		this.partnerName = partnerName;
		return this;
	}

	public Integer getPartnerId() {
		return partnerId;
	}

	public IndustryCsvLine setPartnerId(Integer partnerId) {
		this.partnerId = partnerId;
		return this;
	}

	@Override
	public String toString() {
		return "IndustryCsvLine{" +
				"pmId=" + pmId +
				", pmName='" + pmName + '\'' +
				", partnerName='" + partnerName + '\'' +
				", partnerId=" + partnerId +
				'}';
	}
}
