package query.exceptions;

public class NullForeignKeyException extends QueryException {
	
	public NullForeignKeyException(final Exception e) {
		super(e);
	}
	
	public NullForeignKeyException(final String msg,final Object... args) {
		super(String.format(msg, args));
	}

}
