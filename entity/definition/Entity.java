package entity.definition;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Basic class for every entity
 * @author Serhii Pylypenko
 *
 */

@SuppressWarnings("serial")
public abstract class Entity implements Serializable, Cloneable, Comparable<Entity> {
	
	@Override
	public String toString() {
		return utils.Utilities.toString(this);
	}
	
	@Override
	public int hashCode() {
		return utils.Utilities.hash(this);
	}
	
	@Override
	public boolean equals(final Object o) {
		if(getClass()==o.getClass()) {
			return utils.Utilities.equals(this, (Entity)o);
		}else return false;
	}
	
	@Override
	public Entity clone() {
		return utils.Utilities.clone(this);
	}
	
	protected StringBuilder getKey(){
		final Object[] values=utils.Utilities.evaluateFields(this);//values of serializable fields
		final StringBuilder b=new StringBuilder();
		Arrays.asList(values).forEach(x->b.append(x.toString()));
		return b;
	}
	
	@Override
	public int compareTo(final Entity entity) {
		return getKey().compareTo(entity.getKey());
	}
	
	@Override
	public void finalize() {}//TODO complete method implementation

}
