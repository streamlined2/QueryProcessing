package utils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiPredicate;
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
	 * Seeks for target reference of type {@code tgtClass} in given {@code srcClass}
	 * @param srcClass entity type to inspect
	 * @param tgtClass entity type referred to
	 * @return set of suitable properties of type {@code target}
	 */
	public static Set<ObjectStreamField> getEntityRelations(final Class<? extends Entity> srcClass,final Class<? extends Entity> tgtClass) {
		final Set<ObjectStreamField> properties=new HashSet<>();
		for(final ObjectStreamField property:getSerializableFields(srcClass)) {
			if(tgtClass.isAssignableFrom(property.getType())) {
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
			boolean equal=(aFields.length==bFields.length);
			for(int k=0;equal && k<aFields.length;k++) {
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
				fieldRef.setAccessible(true);
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
		return osc==null? new ObjectStreamField[0]: osc.getFields();//excessive check, should never be 'null'
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
	 * @param startAt start search from index {@code startAt}
	 * @return index of array element if found or {@code array.length} otherwise
	 */
	public static <T,K> Optional<T> linearSearch(final T[] data,final int startAt,final K key,final Function<T, K> keyFunc) {
		for(int k=Math.max(startAt,0);k<data.length;k++) {
			if(Objects.equals(key, keyFunc.apply(data[k]))) return Optional.ofNullable(data[k]);
		}
		return Optional.empty();
	}
	
	public static <T,K> Optional<T> linearSearch(final Iterable<T> data,final K key,final Function<T, K> keyFunc) {
		return linearSearch(data.iterator(),key,keyFunc);
	}
	
	public static <T,K> Optional<T> linearSearch(final Iterator<T> iterator,final K key,final Function<T,K> keyFunc) {
		for(T value;iterator.hasNext();) {
			value=iterator.next();
			if(Objects.equals(key, keyFunc.apply(value))) return Optional.ofNullable(value);
		}
		return Optional.empty();
	}
	
	public static <T,K> Optional<T> linearSearch(final Iterable<T> data,final K key,final BiPredicate<T, K> check) {
		return linearSearch(data.iterator(),key,check);
	}
	
	public static <T,K> Optional<T> linearSearch(final Iterator<T> iterator,final K key,final BiPredicate<T,K> check){
		for(T value;iterator.hasNext();) {
			value=iterator.next();
			if(check.test(value,key)) return Optional.ofNullable(value);
		}
		return Optional.empty();
	}

}
