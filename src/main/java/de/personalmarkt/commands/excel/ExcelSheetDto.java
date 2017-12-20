package de.personalmarkt.commands.excel;

import java.io.Serializable;
import java.util.List;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 17.07.17
 */
public class ExcelSheetDto implements Serializable {

	private String externeId;

	private String externeName;

	private List<String> interneIdList;

	public String getExterneId() {
		return externeId;
	}

	public String getExterneName() {
		return externeName;
	}

	public List<String> getInterneIdList() {
		return interneIdList;
	}

	public ExcelSheetDto setExterneId(String externeId) {
		this.externeId = externeId;
		return this;
	}

	public ExcelSheetDto setExterneName(String externeName) {
		this.externeName = externeName;
		return this;
	}

	public ExcelSheetDto setInterneIdList(List<String> interneIdList) {
		this.interneIdList = interneIdList;
		return this;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("ExcelSheetDto{");
		sb.append("externeId='").append(externeId).append('\'');
		sb.append(", externeName='").append(externeName).append('\'');
		sb.append(", interneIdList=").append(interneIdList);
		sb.append('}');
		return sb.toString();
	}
}
