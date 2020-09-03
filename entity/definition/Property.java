package entity.definition;

public interface Property<T extends Entity,R> {

	public R getValue(final T entity);

}
