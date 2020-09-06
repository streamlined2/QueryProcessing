package query.processor;

import entity.definition.DataSource;
import entity.definition.Entity;

public class NoDataException extends QueryException {

	public NoDataException(final Class<? extends Entity> entityClass,final DataSource dataSource) {
		super(String.format("the data source %s does not contain data for entity class %s",entityClass.getSimpleName(),dataSource.toString()));
	}

}
