package math;

import java.math.BigInteger;

public class Cardinal implements Numeric {
	
	private final BigInteger value;
	
	public final static Cardinal ZERO=new Cardinal(BigInteger.ZERO);
	public final static Cardinal ONE=new Cardinal(BigInteger.ONE);
	
	public Cardinal(final Integer value) {
		this.value=BigInteger.valueOf(value);
	}
	
	public Cardinal(final BigInteger value) {
		this.value=value;
	}
	
	BigInteger getValue() {
		return value;
	}
	
	public Cardinal add(final Cardinal add) {
		return new Cardinal(value.add(add.value));
	}
	
	@Override public Cardinal add(final Numeric add) {
		if(add instanceof Cardinal addCardinal) return add(addCardinal);
		else throw new IllegalArgumentException("parameter for 'add' should be Cardinal");
	}
	
	public Cardinal divide(final Cardinal divisor) {
		return new Cardinal(value.divide(divisor.value));
	}
	
	@Override public Cardinal divide(final Numeric divisor) {
		if(divisor instanceof Cardinal divCardinal) return divide(divCardinal);
		else throw new IllegalArgumentException("parameter for 'divide' should be Cardinal");
	}
	
	@Override public Cardinal zero() {
		return ZERO;
	}
	
	@Override public Cardinal one() {
		return ONE;
	}
	
	@Override public String toString() { return value.toString();}
	
	@Override public boolean equals(Object o) { return o instanceof Cardinal other? value.equals(other.value): false;}
	
	@Override public int compareTo(final Numeric other) { 
		if(other instanceof Cardinal cardinal) return value.compareTo(cardinal.value);
		else throw new IllegalArgumentException("can compare Cardinal with another Cardinal");
	}

}
