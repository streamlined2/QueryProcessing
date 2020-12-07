package query.exceptions;

import java.util.Optional;

import entity.definition.Entity;
import entity.definition.EntitySource;

public class WrongInitialEntityClassException extends QueryException {

	public WrongInitialEntityClassException(final Optional<EntitySource<? extends Entity>> eSource,final Optional<? extends Entity> tuple) {
		super(String.format("entity class %s of relation chain's first node isn't the same as initial tuple's class %s", eSource.get().getEntityClass(),tuple.get().getClass()));
	}

}
