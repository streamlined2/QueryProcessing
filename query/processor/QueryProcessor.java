package query.processor;

import java.io.ObjectStreamField;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import entity.definition.Entity;
import query.definition.Entry;
import query.definition.EntryList;
import entity.definition.Property;
import entity.definition.ReferenceProperty;
import query.definition.QueryResult;
import utils.Utilities;

/**
 * Query processor interface
 * @author Serhii Pylypenko
 *
 */

public interface QueryProcessor {
	
	public class QueryException extends Exception {
		public QueryException(final String msg) {
			super(msg);
		}
	}
	
	public class NoRelationException extends QueryException {
		public NoRelationException(final Entry<? extends Entity> src,final Entry<? extends Entity> dst){
			super(String.format("can't establish relation between %s and %s", src, dst));
		}
	}
	
	QueryResult fetch() throws QueryException;
	
	class Relation<T extends Entity,R extends Entity> {
		private final Entry<R> destination;
		private final ReferenceProperty<T, R> reference;
		
		Relation(final Entry<R> dest,final ReferenceProperty<T, R> ref){
			destination=dest;
			reference=ref;
		}
		
		Entry<R> getDestination(){ return destination;	}
		ReferenceProperty<T, R> getReference(){ return reference;}
	}

	default <T extends Entity,R extends Entity> 
	SortedMap<Entry<T>,Relation<T,R>> buildRelations(
			final EntryList entries,final Map<Entry<T>,Entry<R>> joints) throws QueryException {

		final SortedMap<Entry<T>,Relation<T,R>> relations=new TreeMap<>();
/*
		for(final Entry<? extends Entity> source:entries) {//TODO except last item
			final ObjectStreamField[] fields=Utilities.getSerializableFields(source.getEntityClass());
			final Entry<R> dest=joints.get(source);
			if(dest != null) {
				final Class<R> destType=dest.getEntityClass();
				ObjectStreamField field=Utilities.linearSearch(fields,destType,osField->osField.getType());
				if(field==null) throw new NoRelationException(source,dest);
				relations.put(
						source, new Relation<>(dest, new ReferenceProperty<T,R>(Property.getProperty(field))));
			}else {
				
			}
			
			Entry<R> destination=null;
			if((destination=joints.get(source)) == null) {
				
			}
			//Relation<T,R> relation=new Relation<>(destination,);
			//relations.put(source, destination);
		}*/
		return relations;
	}

}
