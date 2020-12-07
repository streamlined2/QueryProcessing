package query.exceptions;

import entity.definition.DataSource;
import entity.definition.Entity;
import query.definition.Entry;

public class NoDataException extends QueryException {

	public NoDataException(final Class<? extends Entity> entityClass,final DataSource dataSource) {
		super(String.format("the data source %s does not contain data for entity class %s",entityClass.getSimpleName(),dataSource.toString()));
	}

	public NoDataException(final Entry<? extends Entity> entry,final DataSource dataSource) {
		this(entry.getEntityClass(),dataSource);
	}

}
