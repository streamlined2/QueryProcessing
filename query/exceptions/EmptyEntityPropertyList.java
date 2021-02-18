package query.exceptions;

public class EmptyEntityPropertyList extends EntityPersistFailedException {
	
	public EmptyEntityPropertyList() {
		super("list of entity properties shouldn't be empty");
	}

}
