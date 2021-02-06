package query.exceptions;

import query.definition.Query;

public class OrderGroupClausesNotCompatibleException extends QueryException {

	public OrderGroupClausesNotCompatibleException(final Query query) {
		super(String.format("the group-by clause must contain sublist of attributes of order-by clause"));
	}

}
