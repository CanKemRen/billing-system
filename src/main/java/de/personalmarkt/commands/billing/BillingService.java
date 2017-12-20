package de.personalmarkt.commands.billing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * kemal please enter a comment
 *
 * @author kemal
 * @since 11.12.17
 */
@Component
public class BillingService {

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	TestTableDao testTableDao;

	public List<TestTable> findAllData() {
		return testTableDao.findAll();

	}

	public TestTable findOneById(Integer id) {
		return testTableDao.findOne(id);

	}

	public List<TestTable> findAllDataByJdbc() {
		String sql = "select * from table1";

		List<TestTable> list = new ArrayList<>();
		list = namedParameterJdbcTemplate.query(sql,
				(record, rowNum) -> new TestTable().setId(record.getInt("id"))
					.setField(record.getString("field1"))
					.setUpdated(record.getTimestamp("updated"))
					.setZeit(record.getTimestamp("zeit")));
		return list;
	}

	public TestTable findOneByIdWithJdbc(Integer id) {

		String sql = "select * from table1 where id = :id";
		MapSqlParameterSource parameters = new MapSqlParameterSource();

		parameters.addValue("id", id);

		TestTable result = namedParameterJdbcTemplate.queryForObject(sql, parameters,
				(record, rowNum) -> new TestTable().setId(record.getInt("id"))
					.setField(record.getString("field1"))
					.setUpdated(record.getTimestamp("updated"))
					.setZeit(record.getTimestamp("zeit")));
		return result;
	}

	public Boolean insertOneRow(TestTable testTable) {
		testTableDao.save(testTable);
		return true;
	}

	public Boolean updateOneRow(TestTable testTable) {
		TestTable tmp = testTableDao.findOne(testTable.getId());
		tmp.setZeit(testTable.getZeit()).setUpdated(testTable.getUpdated()).setField(testTable.getField());
		testTableDao.save(tmp);
		return true;
	}

	public Boolean insertOneRowWithJdbc(TestTable testTable) {

		String sql = "insert into table1 (id, zeit, field1, updated) VALUES (:id, :zeit, :field1, :updated);";
		MapSqlParameterSource parameters = new MapSqlParameterSource();

		parameters.addValue("id", testTable.getId());
		parameters.addValue("updated", testTable.getUpdated());
		parameters.addValue("zeit", testTable.getZeit());
		parameters.addValue("field1", testTable.getField());

		int updated = namedParameterJdbcTemplate.update(sql, parameters);

		return updated > 0;
	}

	public Boolean updateOneRowWithJdbc(TestTable testTable) {

		String sql = "update table1 set zeit = :zeit, field1 = :field1, updated = :updated where id = :id;";
		MapSqlParameterSource parameters = new MapSqlParameterSource();

		parameters.addValue("id", testTable.getId());
		parameters.addValue("updated", testTable.getUpdated());
		parameters.addValue("zeit", testTable.getZeit());
		parameters.addValue("field1", testTable.getField());

		int updated = namedParameterJdbcTemplate.update(sql, parameters);

		return updated > 0;
	}
}
