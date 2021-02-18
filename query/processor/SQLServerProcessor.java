package query.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import query.definition.Query;
import query.definition.result.SQLQueryResult;
import query.exceptions.QueryException;
import query.exceptions.SQLQueryFailedException;

/**
 * Simple query processor which takes query, forms SQL statement, relays it to SQL servers, and creates query result
 * @author Serhii Pylypenko
 *
 */
public class SQLServerProcessor extends AbstractQueryProcessor {
	
	private final Connection connection;

	public SQLServerProcessor(final Connection connection,final Query query) throws QueryException {
		super(query);
		this.connection=connection;
	}

	@Override
	public SQLQueryResult fetch() throws QueryException {
		try(final Statement statement=connection.createStatement()){
			final ResultSet resultSet=statement.executeQuery(query.getSQLStatement());
			return new SQLQueryResult(query, resultSet);
		}catch(SQLException e) {
			throw new SQLQueryFailedException(e);
		}
	}

}
