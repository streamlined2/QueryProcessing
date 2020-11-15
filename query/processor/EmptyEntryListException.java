package query.processor;

public class EmptyEntryListException extends QueryException {

	public EmptyEntryListException() {
		super("data entry list should contain at least one entry");
	}

}
