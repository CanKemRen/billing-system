package de.personalmarkt.commands.billing;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTableDao extends JpaRepository<TestTable, Integer> {

	@Query("select t from TestTable t")
	public List<TestTable> gettestData();
}
