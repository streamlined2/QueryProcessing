package entity.definition;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Objects;

import query.exceptions.EntitySetterException;

/**
 * Basic class for every entity
 * @author Serhii Pylypenko
 *
 */

@SuppressWarnings("serial")
public abstract class Entity implements Serializable, Cloneable, Comparable<Entity> {
	
	public static class PrimaryKey implements Serializable/*, SQLData*/ {
		private final BigDecimal value;
		private final Class<? extends Entity> entityClass;
		
		public PrimaryKey(final Class<? extends Entity> entityClass,final BigDecimal value) { 
			this.entityClass=entityClass;
			this.value=value;
		}
		
		public PrimaryKey(final Class<? extends Entity> entityClass,final Integer value) { 
			this.entityClass=entityClass;
			this.value=BigDecimal.valueOf(value);
		}
		
		//primary key value to store in tuple 
		public final Object internalValue() {
			return value;
		}
		
		public final Class<? extends Entity> getEntityClass(){
			return entityClass;
		}
		
		//implementation specific 
		public void setPrimaryKeyInStatement(final PreparedStatement statement,final int index) throws SQLException {
			statement.setBigDecimal(index, value);
		}
		
		@Override public boolean equals(final Object o) { 
			return o instanceof PrimaryKey key?
					entityClass==key.entityClass && value.equals(key.value):
						false;
		};
		
		@Override public int hashCode() { 
			return Objects.hash(entityClass,value);
		}
		
		@Override public String toString() { return value.toString();}
		/*
		 * private String sqlType;
		 * 
		 * @Override public String getSQLTypeName() throws SQLException { return
		 * sqlType; }
		 * 
		 * @Override public void readSQL(SQLInput stream, String typeName) throws
		 * SQLException { sqlType=typeName; value=stream.readBigDecimal(); }
		 * 
		 * @Override public void writeSQL(SQLOutput stream) throws SQLException {
		 * stream.writeBigDecimal(value); }
		 */
	}
	
	private PrimaryKey id;
	public PrimaryKey id() { return id;}
	public void setId(final PrimaryKey id) { this.id=id;}
	
	public void setValue(final String propertyName,final Object value) throws EntitySetterException {
		try {
			final Field f=getClass().getDeclaredField(propertyName);
			f.setAccessible(true);
			f.set(this, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new EntitySetterException(e);
		}
	}
	
	@Override
	public String toString() {
		return entity.definition.EntityInspector.toString(this);
	}
	
	@Override
	public int hashCode() {
		return entity.definition.EntityInspector.hash(this);
	}
	
	@Override
	public boolean equals(final Object o) {
		if(getClass()==o.getClass()) {
			return entity.definition.EntityInspector.equals(this, (Entity)o);
		}else return false;
	}
	
	@Override
	public Entity clone() {
		return entity.definition.EntityInspector.clone(this);
	}
	
	protected StringBuilder getKey(){
		final Object[] values=entity.definition.EntityInspector.evaluateFields(this);//values of serializable fields
		final StringBuilder b=new StringBuilder();
		Arrays.asList(values).forEach(x->b.append(x==null?"":x.toString()));
		return b;
	}
	
	@Override
	public int compareTo(final Entity entity) {
		return getKey().compareTo(entity.getKey());
	}
	
	@Override
	public void finalize() {}//optional resource release

}
