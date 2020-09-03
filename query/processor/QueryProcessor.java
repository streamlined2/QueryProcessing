package query.processor;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import query.definition.Query;
import entity.definition.DataSource;
import entity.definition.Entity;
import entity.definition.Property;
import query.definition.Entry;
import query.definition.EntryList;
import query.definition.QueryResult;

/**
 * Query processor interface
 * @author Serhii Pylypenko
 *
 */

public interface QueryProcessor {
	
	QueryResult fetch(final Query query,final DataSource dataSource) throws QueryException;
	
	class Relation<T extends Entity,R extends Entity> {
		private final Entry<R> destination;
		private final Property<T, R> reference;
		
		Relation(final Entry<R> dest,final Property<T, R> ref){
			destination=dest;
			reference=ref;
		}
		
		Entry<R> getDestination(){ return destination;	}
		Property<T, R> getReference(){ return reference;}
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
