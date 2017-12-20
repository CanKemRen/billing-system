package de.personalmarkt.commands.billing;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.Id;

/**
 * create table table1 ( id integer not null constraint table1_pkey1 primary key, zeit timestamp(1) default now() not null, field1 varchar, updated
 * timestamp(1)) ;
 *
 * @author kemal
 * @since 12.12.17
 */
@Entity
@Table(name = "table1",schema = "public")
public class TestTable implements Serializable {

	@Id
	private Integer id;

	@Temporal(TemporalType.DATE)
	private Timestamp zeit;

	@Column(name = "field1")
	private String field;

	@Temporal(TemporalType.DATE)
	private Timestamp updated;

	public Integer getId() {
		return id;
	}

	public Timestamp getZeit() {
		return zeit;
	}

	public String getField() {
		return field;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public TestTable setId(Integer id) {
		this.id = id;
		return this;
	}

	public TestTable setZeit(Timestamp zeit) {
		this.zeit = zeit;
		return this;
	}

	public TestTable setField(String field) {
		this.field = field;
		return this;
	}

	public TestTable setUpdated(Timestamp updated) {
		this.updated = updated;
		return this;
	}
}
