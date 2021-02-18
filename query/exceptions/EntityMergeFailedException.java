package query.exceptions;

public class EntityMergeFailedException extends QueryException {

	public EntityMergeFailedException() {
		super("entity merge operation failed");
	}
	
	public EntityMergeFailedException(final Exception e) {
		super(e);
	}

	public EntityMergeFailedException(final String msg) {
		super(msg);
	}

}
