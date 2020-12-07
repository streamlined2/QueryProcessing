package query.exceptions;

import entity.definition.Entity;
import query.definition.Entry;
import query.definition.Link;

public class NoRelationException extends QueryException {
	public NoRelationException(final Entry<? extends Entity> src) {
		super(String.format("can't find relation from %s to any other entity", src));
	}
	
	public NoRelationException(final Entry<? extends Entity> src,final Entry<? extends Entity> dst){
		super(String.format("can't establish relation between entities %s and %s", src, dst));
	}

	public NoRelationException(
			final Link<? extends Entity,? extends Entity> src,
			final Link<? extends Entity,? extends Entity> dst){
		super(String.format("can't establish relation between entities %s and %s", src.getSourceProperty().getEntry(), dst.getSourceProperty().getEntry()));
	}
}

