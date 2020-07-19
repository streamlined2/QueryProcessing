package utils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.Objects;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.Serializable;

/**
 * Common purpose utilities
 * @author Serhii Pylypenko
 *
 */
public final class Utilities {
	
	private Utilities(){}//never instantiate the class
	
	public static <T extends Serializable> boolean equals(final T a,final T b) {
		if(a!=null && b!=null) {
			final Object[] aFields=evaluateFields(a);
			final Object[] bFields=evaluateFields(b);
			boolean equal=true;
			for(int k=0;equal && k<Math.min(aFields.length, bFields.length);k++) {
				equal=equal && Objects.equals(aFields[k], bFields[k]);
			}
			return equal;
		}
		return false;
	}
	
	public static <T extends Serializable> int hash(final T obj){
		return Objects.hash(evaluateFields(obj));
	}
	
	public static <T extends Serializable> Object[] evaluateFields(final T obj) {
		return evaluateFields(obj,getSerializableFields(obj.getClass()));
	}
	
	public static <T extends Serializable> Object[] evaluateFields(final T obj,final ObjectStreamField[] fields) {
		final Object[] values=new Object[fields.length];
		try {
			int k=0;
			for(final ObjectStreamField field:fields) {
				final Field fieldRef=obj.getClass().getField(field.getName());
				values[k++]=fieldRef.get(obj);
			}
			return values;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T extends Serializable> ObjectStreamField[] getSerializableFields(final Class<T> cl) {
		final ObjectStreamClass osc=ObjectStreamClass.lookup(cl);
		return osc==null? new ObjectStreamField[0]: osc.getFields();
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T clone(final T obj) {
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try {
			new ObjectOutputStream(baos).writeObject(obj);
			return (T)new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
