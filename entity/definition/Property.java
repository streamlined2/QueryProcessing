package entity.definition;

import java.util.Optional;

public interface Property<R> {

	R getValue(final Optional<? extends Entity> entity);
	default String getName() { return "";}
	
}
