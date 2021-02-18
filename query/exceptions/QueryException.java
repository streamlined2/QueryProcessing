package query.exceptions;

public class QueryException extends Exception {
	
	public QueryException(final String msg) {
		super(msg);
	}

	public QueryException(final Exception e) {
		super(e);
	}
}

