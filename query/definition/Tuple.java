package query.definition;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Holds set of property values of query result
 * @author Serhii Pylypenko
 *
 */

public class Tuple implements Comparable<Tuple>{
	
	private final Object[] values;
	private Comparable<? super Comparable<?>> orderKey;
	
	public Tuple(final int dimension){
		this.values=new Object[dimension];
	}
	
	public void setOrderKey(final Comparable<? super Comparable<?>> orderKey) {
		this.orderKey=orderKey;		
	}
	
	public Comparable<? super Comparable<?>> getOrderKey(){
		return orderKey;
	}
	
	public void set(final int index,final Object value) {
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
		StringJoiner joiner=new StringJoiner(",","[","]");
		Arrays.asList(values).forEach(x->joiner.add(x.toString()));
		return joiner.toString();
	}

	@Override
	public int compareTo(Tuple o) {
		return orderKey.compareTo(o.orderKey);
	}

}
