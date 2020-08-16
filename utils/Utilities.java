package utils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;

import entity.definition.Entity;

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
	
	/**
	 * Seeks for target reference of type {@code T} in given {@code entity}
	 * @param <E> type of inspected entity
	 * @param <T> type of reference property
	 * @param entity to inspect for reference property
	 * @param target type of reference property
	 * @return set of suitable properties of type {@code target}
	 */
	public <E extends Entity,T extends Entity> Set<ObjectStreamField> getReferences(final E entity,final Class<T> target) {
		final ObjectStreamField[] fields=getSerializableFields(entity.getClass());
		final Set<ObjectStreamField> properties=new HashSet<>();
		for(final ObjectStreamField property:fields) {
			if(property.getType().equals(target)) {
				properties.add(property);
			}
		}
		return properties;		
	}
	
	/**
	 * Evaluates every serializable field of given {@code obj} and composes string representation 
	 * @param <T> type of parameter object
	 * @param obj object that holds serializable values
	 * @return string representation of {@code obj}
	 */
	public static <T extends Serializable> String toString(final T obj) {
		final StringJoiner joiner=new StringJoiner(",","{","}");
		for(final Object field:evaluateFields(obj)) {
			joiner.add(field.toString());
		}/*
		final Object[] fields=evaluateFields(obj);
		Arrays.asList(fields).forEach(x->joiner.add(x.toString()));*/
		return joiner.toString();
	}
	
	/**
	 * Checks if values of serializable fields of given parameters {@code a} and {@code b} are equal
	 * @param <T> type of passed parameters
	 * @param a first parameter to evaluate
	 * @param b second parameter to evaluate
	 * @return {@code true} if values of serializable fields of first parameter are equal to corresponding values of second parameter, {@code false} otherwise
	 */
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
	
	/**
	 * Evaluates and combines hash code of serializable fields of given {@code obj}
	 * @param <T> type of parameter
	 * @param obj obtain hashcode of given object
	 * @return hash code of passed parameter
	 */
	public static <T extends Serializable> int hash(final T obj){
		return Objects.hash(evaluateFields(obj));
	}
	
	/**
	 * Fetch list of serializable fields of given {@code obj} and evaluate their values
	 * @param <T> type of parameter
	 * @param obj parameter
	 * @return array of serializable fields values
	 */
	public static <T extends Serializable> Object[] evaluateFields(final T obj) {
		return evaluateFields(obj,getSerializableFields(obj.getClass()));
	}
	
	public static <T extends Serializable> Object[] evaluateFields(final T obj,final ObjectStreamField[] fields) {
		final Object[] values=new Object[fields.length];
		try {
			int k=0;
			for(final ObjectStreamField field:fields) {
				final Field fieldRef=obj.getClass().getDeclaredField(field.getName());
				values[k++]=fieldRef.get(obj);
			}
			return values;
		}catch(NoSuchFieldException | SecurityException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Fetches list of serializable field descriptors {@code ObjectStreamField} of given class
	 * @param <T> type of parameter class
	 * @param cl given class to inspect
	 * @return array of serializable field descriptors
	 */
	public static <T extends Serializable> ObjectStreamField[] getSerializableFields(final Class<T> cl) {
		final ObjectStreamClass osc=ObjectStreamClass.lookup(cl);
		return osc==null? new ObjectStreamField[0]: osc.getFields();
	}
	
	/**
	 * Deep copies  given object
	 * @param <T> type of parameter object
	 * @param obj object to copy
	 * @return deep copy of {@code obj}
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T clone(final T obj) {
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try {
			new ObjectOutputStream(baos).writeObject(obj);
			final ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
			return (T)ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Performs linear search within passed {@code array}
	 * @param <T> type of array element
	 * @param <K> type of key to search for
	 * @param array to search
	 * @param key value to search for
	 * @param keyFunc mapping function that turns array element into key
	 * @return index of array element if found or {@code array.length} otherwise
	 */
	public static <T,K> T linearSearch(final T[] data,final K key,final Function<T, K> keyFunc) {
		for(final T value:data) {
			if(Objects.equals(key, keyFunc.apply(value))) return value;
		}
		return null;
	}
	
	public static <T,K> T linearSearch(final Iterable<T> data,final K key,final Function<T, K> keyFunc) {
		for(final T value:data) {
			if(Objects.equals(key, keyFunc.apply(value))) return value;
		}
		return null;
	}
	
	public static <T,K> T linearSearch(final Iterator<T> iterator,final K key,final Function<T,K> keyFunc) {
		for(T value;iterator.hasNext();) {
			value=iterator.next();
			if(Objects.equals(key, keyFunc.apply(value))) return value;
		}
		return null;
	}

}
