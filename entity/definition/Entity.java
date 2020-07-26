package entity.definition;

import java.io.Serializable;

/**
 * Basic class for every entity
 * @author Serhii Pylypenko
 *
 */

@SuppressWarnings("serial")
public abstract class Entity implements Serializable, Cloneable {
	
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
	
	@Override
	public void finalize() {}

}
