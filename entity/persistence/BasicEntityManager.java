package entity.persistence;

import java.io.ObjectStreamField;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import entity.definition.Entity;
import entity.definition.EntityInspector;
import entity.definition.Entity.PrimaryKey;
import entity.definition.EntityInspector.EntityDefinition;
import query.exceptions.ConnectionCommitModeException;
import query.exceptions.EmptyEntityPropertyList;
import query.exceptions.EntityCreationFailedException;
import query.exceptions.EntityMergeFailedException;
import query.exceptions.EntityPersistFailedException;
import query.exceptions.EntityRemoveFailedException;
import query.exceptions.EntitySeekFailedException;
import query.exceptions.NoPrimaryKeyException;
import query.exceptions.NullForeignKeyException;
import query.exceptions.QueryException;
import query.exceptions.ReferencedEntityShouldBePresentInPoolException;
import query.exceptions.RemoveFailedException;

/**
 * Simple entity manager implementation
 * @author Engine
 *
 */
public class BasicEntityManager implements EntityManager, EntityToDBMapper {
	
	private class EntityPool {
		private Map<Entity.PrimaryKey,Entity> stash=new HashMap<>();
		
		Optional<? extends Entity> get(final Entity.PrimaryKey key){
			return stash.containsKey(key)?
					Optional.of(stash.get(key)):
						Optional.empty();
		}
		
		void add(final Entity entity) {
			stash.put(entity.id(),entity);
		}
		
		void remove(final Entity.PrimaryKey key) {
			stash.remove(key);
		}
	}
	
	private final EntityPool pool=new EntityPool();
	private final Connection connection;
	
	public BasicEntityManager(final Connection connection) throws QueryException {
		this.connection=connection;
		try {
			connection.setAutoCommit(false);
			/*
			 * var typeMap=connection.getTypeMap(); 
			 * typeMap.put("mydb.PrimaryKey",PrimaryKey.class); 
			 * connection.setTypeMap(typeMap);
			 */
		} catch (SQLException e) {
			throw new ConnectionCommitModeException(e);
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public String mapEntityToTable(final Class<? extends Entity> eClass) {
		return eClass.getSimpleName().toUpperCase();
	}
	
	public String mapPropertyToAttribute(final String propertyName) {
		return Pattern.
				compile("[A-Z]").
				matcher(propertyName).
				replaceAll(r->"_"+r.group()).
				toUpperCase();
	}
	
	@Override
	public void close() throws Exception {
	}

	private void retrievePrimaryKey(final Entity entity,final Statement statement) throws QueryException {
		try(final ResultSet keys=statement.getGeneratedKeys()){
			if(keys.next()) entity.setId(new PrimaryKey(entity.getClass(),keys.getBigDecimal(1)));
			else throw new NoPrimaryKeyException("no primary key retrieved");
		} catch (SQLException e) {
			throw new NoPrimaryKeyException(e);
		}
	}
	
	private String getInsertQuery(final Entity entity) throws QueryException {
		final StringBuilder query=
				new StringBuilder("INSERT INTO ").append(mapEntityToTable(entity)).append(" (");
		final ObjectStreamField[] entityFields=EntityInspector.getSerializableFields(entity.getClass());
		if(entityFields.length>0) {
			query.append(mapPropertyToAttribute(entityFields[0].getName()));
			for(int k=1;k<entityFields.length;k++)
				query.append(",").append(mapPropertyToAttribute(entityFields[k].getName()));
			query.append(") VALUES (");
			query.append("?");
			for(int k=1;k<entityFields.length;k++)
				query.append(",").append("?");
			query.append(")");
			return query.toString();
		} else {
			throw new EmptyEntityPropertyList();
		}
	}
	
	private static void setQueryParameter(final PreparedStatement statement,final int index,final Object value) throws QueryException {
		try {
			if(value instanceof String aString) {
				statement.setString(index,aString);
			}else if(value instanceof BigDecimal aDecimal) {
				statement.setBigDecimal(index,aDecimal);
			}else if(value instanceof Enum anEnum) {
				statement.setString(index,anEnum.name());
			}else if(value instanceof Date aDate) {
				statement.setDate(index, aDate);
			}else {
				statement.setObject(index,value);
			}
		}catch(SQLException e) {
			throw new QueryException(e);
		}
	}
	
	private static int setQueryParameters(final PreparedStatement statement,final Entity entity) throws QueryException {
		final ObjectStreamField[] entityFields=EntityInspector.getSerializableFields(entity.getClass());
		final Object[] values=EntityInspector.evaluateFields(entity,entityFields);
		int k=0;
		for(final ObjectStreamField field:entityFields) {
			if(EntityDefinition.isForeignKeyReference(field)) {
				final Entity ref=(Entity)values[k];
				if(ref!=null) setQueryParameter(statement,k+1,ref.id().internalValue());
				else throw new NullForeignKeyException("foreign key %s should be set before 'persist' operation",field.getName());
			}else {
				setQueryParameter(statement,k+1,values[k]);
			}
			k++;
		}
		return k;
	}

	@Override
	public void persist(final Entity entity) throws QueryException {
		try(final PreparedStatement statement=
				connection.prepareStatement(
						getInsertQuery(entity), 
						new String[] {EntityDefinition.getPrimaryKeyName()})){
			setQueryParameters(statement, entity);
			statement.executeUpdate();
			retrievePrimaryKey(entity,statement);
			connection.commit();
			pool.add(entity);
		} catch (SQLException | QueryException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EntityPersistFailedException(e1);
			}
			throw new EntityPersistFailedException(e);
		}
	}

	private String getUpdateQuery(final Entity entity) throws QueryException {
		final StringBuilder query=
				new StringBuilder("UPDATE ").append(mapEntityToTable(entity)).append(" SET ");
		final ObjectStreamField[] entityFields=EntityInspector.getSerializableFields(entity.getClass());
		if(entityFields.length>0) {
			query.
				append(mapPropertyToAttribute(entityFields[0].getName())).
				append("=?");
			for(int k=1;k<entityFields.length;k++) {
				query.append(",").
					append(mapPropertyToAttribute(entityFields[k].getName())).
					append("=?");
			}
			query.append(" WHERE ").append(EntityDefinition.getPrimaryKeyName()).append("=?");
			return query.toString();
		} else {
			throw new EmptyEntityPropertyList();
		}
	}
	
	@Override
	public void merge(final Entity entity) throws QueryException {
		try(final PreparedStatement statement=connection.prepareStatement(getUpdateQuery(entity))){
			final int lastArgIndex=setQueryParameters(statement, entity);
			entity.id().setPrimaryKeyInStatement(statement,lastArgIndex+1);
			statement.executeUpdate();
			connection.commit();
			pool.add(entity);
		} catch (SQLException | QueryException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EntityMergeFailedException(e1);
			}
			throw new EntityMergeFailedException(e);
		}
	}
	
	@Override
	public <E extends Entity> void remove(final Class<E> entityClass,final PrimaryKey key) throws QueryException {
		final String query=
				String.format("DELETE FROM %s WHERE %s=?",
						mapEntityToTable(entityClass),
						EntityDefinition.getPrimaryKeyName());
		try(final PreparedStatement statement=connection.prepareStatement(query)){
			key.setPrimaryKeyInStatement(statement,1);
			final int count=statement.executeUpdate();
			if(count!=1) throw new EntityRemoveFailedException();
			connection.commit();
			//pool.remove(key);
		}catch(SQLException | QueryException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new RemoveFailedException(e1);
			}
			throw new RemoveFailedException(e);
		}
	}
	
	@Override
	public <E extends Entity> int removeAll(final Class<E> entityClass) throws QueryException {
		try(final Statement statement=connection.createStatement()){
			final int count=statement.executeUpdate(
					String.format("DELETE FROM %s", mapEntityToTable(entityClass)));
			connection.commit();
			return count;
		} catch (SQLException e) {
			try {
				connection.rollback();
			}catch(SQLException e1) {
				throw new RemoveFailedException(e1);
			}
			throw new RemoveFailedException(e);
		}
	}

	private <E extends Entity> E createEntity(final Class<E> entityClass,final PrimaryKey primaryKey,final Object[] args) throws QueryException {

		final ObjectStreamField[] entityFields=EntityInspector.getSerializableFields(entityClass);
		final Class<?> paramTypes[]=new Class<?>[entityFields.length];

		int k=0; for(final ObjectStreamField field:entityFields) paramTypes[k++]=field.getType();

		try {
			final Constructor<E> construct=entityClass.getDeclaredConstructor(paramTypes);//constructor must have parameters sorted alphabetically to conform with EntityInspector.getSerializableFields
			final E entity=construct.newInstance(args);
			entity.setId(primaryKey);
			return entity;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new EntityCreationFailedException(e);
		}
	}
	
	private <E extends Entity,T extends Enum<T>> Object convertArgument(
			final Class<E> entityClass,final ObjectStreamField osField, final Object object) throws QueryException {
		Object result=object;
		if(EntityDefinition.isForeignKeyReference(osField)) {
			PrimaryKey key=new PrimaryKey((Class<? extends Entity>) osField.getType(),(Integer)object);
			Optional<? extends Entity> entity=pool.get(key);
			if(entity.isPresent()) {
				result=entity.get();
			}else throw new ReferencedEntityShouldBePresentInPoolException("referenced entity for type %s and key %d should be present in entity pool",key.getEntityClass(),key.internalValue());
		}else if(Enum.class.isAssignableFrom(osField.getType())) {
			result=Enum.<T>valueOf((Class<T>)osField.getType(),(String)object);
		}else if(BigDecimal.class.isAssignableFrom(osField.getType())) {
			result=BigDecimal.valueOf(((Number)object).longValue());
		}else if(java.sql.Date.class.isAssignableFrom(osField.getType())) {
			result=Date.valueOf(((LocalDateTime)object).toLocalDate());
		}
		return result;
	}

	private <E extends Entity> Object[] collectArguments(final Class<E> entityClass,final ResultSet rs) throws QueryException {
		final ObjectStreamField[] entityFields=EntityInspector.getSerializableFields(entityClass);
		final Object[] args=new Object[entityFields.length];
		try {
			for(int k=0;k<entityFields.length;k++) {
				args[k]=convertArgument(entityClass,entityFields[k],rs.getObject(k+1));
			}
		} catch (SQLException e) {
			throw new EntityCreationFailedException(e);
		}
		return args;
	}
	
	private <E extends Entity> E createEntityFromResultSet(final Class<E> entityClass,final PrimaryKey primaryKey,final ResultSet rs) throws QueryException {
		return createEntity(
				entityClass,
				primaryKey,
				collectArguments(entityClass,rs));
	}

	private StringBuilder getEntityProperties(final Class<? extends Entity> entityClass) {
		final ObjectStreamField[] entityFields=EntityInspector.getSerializableFields(entityClass);
		final StringBuilder propertyList=new StringBuilder();
		if(entityFields.length>0) {
			propertyList.append(mapPropertyToAttribute(entityFields[0].getName()));
			for(int k=1;k<entityFields.length;k++)
				propertyList.append(",").append(mapPropertyToAttribute(entityFields[k].getName()));
		}
		return propertyList;
	}

	@Override
	public <E extends Entity> Optional<E> find(final Class<E> entityClass,final PrimaryKey primaryKey) throws QueryException {
		final String query=
				String.format("SELECT %s FROM %s WHERE %s=?", 
						getEntityProperties(entityClass),
						mapEntityToTable(entityClass),
						EntityDefinition.getPrimaryKeyName());
		try(final PreparedStatement statement=connection.prepareStatement(query)) {
			primaryKey.setPrimaryKeyInStatement(statement, 1);
			try(final ResultSet rs=statement.executeQuery()) {
				if(rs.next()) {
					final E entity=createEntityFromResultSet(entityClass,primaryKey,rs);
					if(rs.next()) throw new EntitySeekFailedException("found extra entity for primary key %s",primaryKey.toString());
					else return Optional.of(entity);
				}else {
					return Optional.empty();//throw new EntitySeekFailedException("no entity found for primary key %s",primaryKey.toString());
				}
			}
		} catch (SQLException e) {
			throw new EntitySeekFailedException(e);
		}
	}

}
