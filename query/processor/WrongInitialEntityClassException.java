package query.processor;

import entity.definition.Entity;
import entity.definition.EntitySource;

public class WrongInitialEntityClassException extends QueryException {

	public WrongInitialEntityClassException(final EntitySource<? extends Entity> eSource,final Entity tuple) {
		super(String.format("entity class %s of relation chain's first node isn't the same as initial tuple's class %s", eSource.getEntityClass(),tuple.getClass()));
	}

}
