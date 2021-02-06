package entity.definition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import query.exceptions.QueryException;
import start.Runner;

/**
 * Entity inspection & analysis utility class 
 * @author Serhii Pylypenko
 *
 */
public final class EntityInspector {
	
	private static final String ENTITY_BEANS_PACKAGE="entity.beans.";
	
	private EntityInspector() {}

	/**
	 * Seeks for target reference of type {@code tgtClass} in given {@code srcClass}
	 * @param srcClass entity type to inspect
	 * @param tgtClass entity type referred to
	 * @return set of suitable properties of type {@code target}
	 */
	public static Set<ObjectStreamField> getEntityRelations(final Class<? extends Entity> srcClass,final Class<? extends Entity> tgtClass) {
		final Set<ObjectStreamField> properties=new HashSet<>();
		for(final ObjectStreamField property:EntityInspector.getSerializableFields(srcClass)) {
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
		for(final Object field:EntityInspector.evaluateFields(obj)) {
			joiner.add(field.toString());
		}/*
		Arrays.asList(evaluateFields(obj)).forEach(x->joiner.add(x.toString()));*/
		return joiner.toString();
	}

	/**
	 * Checks if values of serializable fields of given entities {@code a} and {@code b} are equal
	 * @param <T> type of passed parameters
	 * @param a first parameter to evaluate
	 * @param b second parameter to evaluate
	 * @return {@code true} if values of serializable fields of first parameter are equal to corresponding values of second parameter, {@code false} otherwise
	 */
	public static <T extends Serializable> boolean equals(final T a,final T b) {
		if(a!=null && b!=null) {
			final Object[] aFields=EntityInspector.evaluateFields(a);
			final Object[] bFields=EntityInspector.evaluateFields(b);
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
		return Objects.hash(EntityInspector.evaluateFields(obj));
	}

	/**
	 * Fetch list of serializable fields of given {@code obj} and evaluate their values
	 * @param <T> type of parameter
	 * @param obj parameter
	 * @return array of serializable fields values
	 */
	public static <T extends Serializable> Object[] evaluateFields(final T obj) {
		return EntityInspector.evaluateFields(obj,EntityInspector.getSerializableFields(obj.getClass()));
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
	 * Deep-copies given object
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
	 * Finds entity classes that are located within classpath and collects them in resulting set
	 * @return set of entity classes
	 * @throws QueryException incorrect URI or class file not found 
	 */
	public static Set<Class<? extends Entity>> getEntityBeans() {
		final Set<Class<? extends Entity>> entityClasses=new HashSet<>();
		final ClassLoader cl=Thread.currentThread().getContextClassLoader();
		final URL packageURL=cl.getResource(
				ENTITY_BEANS_PACKAGE.replace(".", "/")+"package-info.class");
		try {
			final File[] entityFiles=new File(packageURL.toURI()).getParentFile().listFiles(new FilenameFilter() {
				@Override public boolean accept(final File dir, final String name) {
					return name.endsWith(".class"); 
				}
			});
			for(final File f:entityFiles) {
				try {
					final Class<?> cls=cl.loadClass(getPackageClass(f));
					if(Entity.class.isAssignableFrom(cls)) entityClasses.add((Class<? extends Entity>) cls);
				} catch (ClassNotFoundException e) {//skip class if search failed
				}
			}
		} catch (URISyntaxException e) {
		}
		
		return entityClasses;
	}
	
	private static String getPackageClass(final File path) {
		final int index=path.getName().indexOf(".");
		return ENTITY_BEANS_PACKAGE+
				(index!=-1?path.getName().substring(0,index):path.getName().substring(0));
	}
	

}
