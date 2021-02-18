package query.exceptions;

public class EntityRemoveFailedException extends QueryException {

	public EntityRemoveFailedException() {
		super("entity remove operation failed");
	}
	
	public EntityRemoveFailedException(final Exception e) {
		super(e);
	}

	public EntityRemoveFailedException(final String msg) {
		super(msg);
	}

}
