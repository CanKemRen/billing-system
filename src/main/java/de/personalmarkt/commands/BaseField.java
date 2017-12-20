package de.personalmarkt.commands;

import java.io.Serializable;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 06.07.17
 */
public class BaseField implements Serializable {

	private String fieldName;

	private FieldType fieldType = FieldType.STRING;

	private String sequence;

	public enum FieldType {
		STRING, INTEGER, DOUBLE, SERIAL
	}

	public String getSequence() {
		return sequence;
	}

	public String getFieldName() {
		return fieldName;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	public BaseField setFieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public BaseField setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
		return this;
	}

	public BaseField setSequence(String sequence) {
		this.sequence = sequence;
		return this;
	}
}
