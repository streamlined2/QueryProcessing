package query.processor;

import entity.definition.Entity;
import query.definition.Entry;

public class NoRelationException extends QueryException {
	public NoRelationException(final Entry<? extends Entity> src,final Entry<? extends Entity> dst){
		super(String.format("can't establish relation between %s and %s", src, dst));
	}
}

