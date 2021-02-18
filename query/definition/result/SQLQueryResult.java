package query.definition.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

import query.definition.Query;
import query.definition.Tuple;
import query.exceptions.QueryException;
import query.exceptions.SQLQueryFailedException;
import query.processor.SQLServerProcessor;

/**
 * Copies and collects tuples from passed {@code ResultSet}
 * @author Serhii Pylypenko
 *
 */
public class SQLQueryResult extends AbstractQueryResult {
	
	private final List<Tuple> tuples=new ArrayList<>();
	
	public SQLQueryResult(final Query query,final SQLServerProcessor processor,final ResultSet resultSet) throws QueryException {
		super(query,processor);
		fillTuples(resultSet);
	}
	
	private void fillTuples(final ResultSet resultSet) throws QueryException {
		try {
			for(;resultSet.next();) {
				final Tuple tuple=new Tuple(resultSet.getMetaData().getColumnCount());
				for(int k=0;k<tuple.getDimension();k++) {
					tuple.setValue(k, resultSet.getObject(k+1));
				}
				tuples.add(tuple);
			}
		} catch (SQLException e) {
			throw new SQLQueryFailedException(e);
		}
	}

	@Override public String toString() {
		final StringJoiner resultJoiner=new StringJoiner("\n");
		for(final Tuple tuple:tuples) {
			final StringJoiner rowJoiner=new StringJoiner(" - ","[","]");
			for(int k=0;k<tuple.getDimension();k++) {
				rowJoiner.add(tuple.getValue(k).toString());
			}
			resultJoiner.add(rowJoiner.toString());
		}
		return resultJoiner.toString();
	}

	@Override
	public Iterator<Tuple> iterator() {
		return tuples.iterator();
	}

	@Override
	public long getTupleCount() {
		return tuples.size();
	}
	
	public final String getSQLStatement(){
		return ((SQLServerProcessor)processor).getSQLStatement();
	}

}
