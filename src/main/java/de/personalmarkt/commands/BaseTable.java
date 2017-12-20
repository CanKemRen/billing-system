package de.personalmarkt.commands;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 06.07.17
 */
public class BaseTable implements Serializable {

	private String tableName;

	private Set<BaseField> fieldList = new HashSet<>();

	private Operation operation = Operation.INSERT;

	private String sequence;

	public enum Operation {
		INSERT, UPDATE, DELETE
	}

	public String getTableName() {
		return tableName;
	}

	public Set<BaseField> getFieldList() {
		return fieldList;
	}

	public String getSequence() {
		return sequence;
	}

	public Operation getOperation() {
		return operation;
	}

	public BaseTable setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public BaseTable setFieldList(Set<BaseField> fieldList) {
		this.fieldList = fieldList;
		return this;
	}

	public BaseTable setOperation(Operation operation) {
		this.operation = operation;
		return this;
	}

	public BaseTable setSequence(String sequence) {
		this.sequence = sequence;
		return this;
	}
}
