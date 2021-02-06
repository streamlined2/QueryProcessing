package math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Real implements Numeric {
	
	private final BigDecimal value;
	
	public final static Real ZERO=new Real(BigDecimal.ZERO);
	public final static Real ONE=new Real(BigDecimal.ONE);
	
	public Real(final Double value) {
		this.value=BigDecimal.valueOf(value);
	}
	
	public Real(final BigDecimal value) {
		this.value=value;
	}
	
	public Real add(final Real add) {
		return new Real(value.add(add.value));
	}
	
	public Real add(final Cardinal add) {
		return new Real(value.add(new BigDecimal(add.getValue())));
	}

	@Override public Real add(final Numeric add) {
		 if(add instanceof Real addReal) return add(addReal);//new Real(value.add(addReal.value));
		 else if(add instanceof Cardinal addCardinal) return add(addCardinal);//new Real(value.add(new BigDecimal(addCardinal.getValue()))); else
		 throw new IllegalArgumentException("parameter for 'add' should be either Real or Cardinal");
	}
	
	public Real divide(final Real div) {
		return new Real(value.divide(div.value,RoundingMode.HALF_UP));
	}
	
	public Real divide(final Cardinal div) {
		return new Real(value.divide(new BigDecimal(div.getValue()),RoundingMode.HALF_UP));
	}
	
	@Override public Real divide(final Numeric divisor) {
		if(divisor instanceof Real divReal) return divide(divReal);
		else if(divisor instanceof Cardinal divCardinal) return divide(divCardinal);
		else throw new IllegalArgumentException("parameter for 'divide' should be either Real or Cardinal");
	}
	
	@Override public Real zero() {
		return ZERO;
	}

	@Override public Real one() {
		return ONE;
	}

	@Override public String toString() { return value.toString();}
	
	@Override public boolean equals(Object o) { return o instanceof Real other? value.equals(other.value): false;} 

	@Override public int compareTo(final Numeric other) { 
		if(other instanceof Real real) return value.compareTo(real.value);
		else throw new IllegalArgumentException("can compare Real with another Real");
	}

}
