package entity.definition;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Basic class for every entity
 * @author Serhii Pylypenko
 *
 */

@SuppressWarnings("serial")
public abstract class Entity implements Serializable, Cloneable, Comparable<Entity> {
	
	@Override
	public String toString() {
		return entity.definition.EntityInspector.toString(this);
	}
	
	@Override
	public int hashCode() {
		return entity.definition.EntityInspector.hash(this);
	}
	
	@Override
	public boolean equals(final Object o) {
		if(getClass()==o.getClass()) {
			return entity.definition.EntityInspector.equals(this, (Entity)o);
		}else return false;
	}
	
	@Override
	public Entity clone() {
		return entity.definition.EntityInspector.clone(this);
	}
	
	protected StringBuilder getKey(){
		final Object[] values=entity.definition.EntityInspector.evaluateFields(this);//values of serializable fields
		final StringBuilder b=new StringBuilder();
		Arrays.asList(values).forEach(x->b.append(x.toString()));
		return b;
	}
	
	@Override
	public int compareTo(final Entity entity) {
		return getKey().compareTo(entity.getKey());
	}
	
	@Override
	public void finalize() {}//optional resource release

}
