package query.exceptions;

public class NoInitialEntityException extends QueryException {
	
	public NoInitialEntityException() {
		super("no initial entity provided for relation list");
	}

}
