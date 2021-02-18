package query.exceptions;

public class EntitySeekFailedException extends QueryException {
	
	public EntitySeekFailedException() {
		super("entity seek operation failed");
	}
	
	public EntitySeekFailedException(final Exception e) {
		super(e);
	}

	public EntitySeekFailedException(final String format,final String args) {
		super(String.format(format, args));
	}

}
