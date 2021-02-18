package query.definition;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Holds set of property values of ordinary query or key values of aggregated query result
 * @author Serhii Pylypenko
 *
 */

public class Tuple implements Comparable<Tuple>{
	
	private final Object[] values;
	private StringBuilder orderKey;
	
	public Tuple(final int dimension){
		this.values=new Object[dimension];
	}
	
	public int getDimension() {
		return values.length;
	}
	
	public void setOrderKey(final StringBuilder orderKey) {
		this.orderKey=orderKey;		
	}
	
	public StringBuilder getOrderKey(){
		return orderKey;
	}
	
	public Object getValue(final int index) {
		return values[index];
	}
	
	public void setValue(final int index,final Object value) {
		values[index]=value;
	}
	
	@Override public int hashCode() {
		return orderKey.hashCode();
	}
	
	@Override public boolean equals(final Object o) {
		return o instanceof Tuple tuple?
				orderKey.equals(tuple.orderKey):false;
	}
	
	@Override
	public String toString() {
		final StringJoiner joiner=new StringJoiner(",","[","]");
		Arrays.asList(values).forEach(x->joiner.add(x.toString()));
		return joiner.toString();
	}

	@Override
	public int compareTo(final Tuple o) {
		return orderKey.compareTo(o.orderKey);
	}

}
