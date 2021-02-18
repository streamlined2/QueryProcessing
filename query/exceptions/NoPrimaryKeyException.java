package query.exceptions;

public class NoPrimaryKeyException extends EntityPersistFailedException {
	
	public NoPrimaryKeyException() {
		super("no key returned by INSERT for new entity");
	}

	public NoPrimaryKeyException(final String msg) {
		super(String.format("no key returned by INSERT for new entity: %s", msg));
	}

	public NoPrimaryKeyException(final Exception e) {
		super(e);
	}

}
