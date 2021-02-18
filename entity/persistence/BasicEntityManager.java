package entity.persistence;

import java.io.ObjectStreamField;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import entity.definition.Entity;
import entity.definition.EntityInspector;
import entity.definition.Entity.PrimaryKey;
import entity.definition.EntityInspector.EntityDefinition;
import query.exceptions.ConnectionCommitModeException;
import query.exceptions.EmptyEntityPropertyList;
import query.exceptions.EntityMergeFailedException;
import query.exceptions.EntityPersistFailedException;
import query.exceptions.EntityRemoveFailedException;
import query.exceptions.NoPrimaryKeyException;
import query.exceptions.QueryException;
import query.exceptions.RemoveFailedException;

public class BasicEntityManager implements EntityManager {
	
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
	
	private static String mapEntityToTable(final Class<? extends Entity> eClass) {
		return eClass.getSimpleName().toUpperCase();
	}
	
	private static String mapEntityToTable(final Entity e) {
		return mapEntityToTable(e.getClass());
	}
	
	private static String mapPropertyToAttribute(final String propertyName) {
		return Pattern.
				compile("[A-Z]").
				matcher(propertyName).
				replaceAll(r->"_"+r.group()).
				toLowerCase();
	}
	
	@Override
	public <E extends Entity> E find(final Class<E> entityClass,final PrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	private PrimaryKey retrievePrimaryKey(final Statement statement) throws QueryException {
		try(final ResultSet keys=statement.getGeneratedKeys()){
			if(keys.next()) return new PrimaryKey(keys.getBigDecimal(1));
			else throw new NoPrimaryKeyException("no primary key retrieved");
		} catch (SQLException e) {
			throw new NoPrimaryKeyException(e);
		}
	}
	
	private static String getInsertQuery(final Entity entity) throws QueryException {
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
				statement.setInt(index,anEnum.ordinal());
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
			setQueryParameter(statement,k+1,values[k++]);
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
			entity.setId(retrievePrimaryKey(statement));
			connection.commit();
		} catch (SQLException | QueryException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EntityPersistFailedException(e1);
			}
			throw new EntityPersistFailedException(e);
		}
	}

	private static String getUpdateQuery(final Entity entity) throws QueryException {
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

}
