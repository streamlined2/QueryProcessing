package query.exceptions;

public class EntityCreationFailedException extends QueryException {
	
	public EntityCreationFailedException() {
		super("entity seek operation failed");
	}
	
	public EntityCreationFailedException(final Exception e) {
		super(e);
	}

	public EntityCreationFailedException(final String format,final String args) {
		super(String.format(format, args));
	}

}
