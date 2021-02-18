package query.exceptions;

public class QueryParametersSettingFailedException extends QueryException {
	
	public QueryParametersSettingFailedException(final String msg) {
		super(msg);
	}
	
	public QueryParametersSettingFailedException(final Exception e) {
		super(e);
	}

}
