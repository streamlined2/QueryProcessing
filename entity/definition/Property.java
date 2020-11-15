package entity.definition;

import java.util.Optional;

public interface Property<R> {

	public R getValue(final Optional<? extends Entity> entity);
	
}
