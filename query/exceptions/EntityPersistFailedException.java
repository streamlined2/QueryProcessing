package query.exceptions;

public class EntityPersistFailedException extends QueryException {
	
	public EntityPersistFailedException() {
		super("entity persist operation failed");
	}
	
	public EntityPersistFailedException(final Exception e) {
		super(e);
	}

	public EntityPersistFailedException(final String msg) {
		super(msg);
	}

}
