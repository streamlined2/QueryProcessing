package entity.definition;

import java.util.function.Function;

/**
 * Holds reference property to another entity linking them together, functioning as foreign key
 * @author Serhii Pylypenko
 *
 * @param <T> entity type
 * @param <R> type of linked entity
 */

public class ReferenceProperty<T extends Entity,R extends Entity> extends Property<T> {

	public ReferenceProperty(Function<T, R> getter) {
		super(getter);
	}

}
