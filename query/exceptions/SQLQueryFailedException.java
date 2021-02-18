package query.exceptions;

public class SQLQueryFailedException extends QueryException {
	
	public SQLQueryFailedException(final Exception e) {
		super(e);
	}
	
	public SQLQueryFailedException(final String msg) {
		super(msg);
	}

}
