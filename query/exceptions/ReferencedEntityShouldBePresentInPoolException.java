package query.exceptions;

public class ReferencedEntityShouldBePresentInPoolException extends QueryException {
	
	public ReferencedEntityShouldBePresentInPoolException(final String msg,final Object...args) {
		super(String.format(msg, args));
	}

}
