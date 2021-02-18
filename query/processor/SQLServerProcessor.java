package query.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.function.Predicate;

import entity.definition.Entity;
import entity.definition.EntityInspector.EntityDefinition;
import entity.persistence.BasicEntityManager;
import math.Numeric;
import query.definition.AggregationProperty;
import query.definition.Entry;
import query.definition.Link;
import query.definition.OrderProperty;
import query.definition.OrderProperty.OrderKind;
import query.definition.QualifiedProperty;
import query.definition.Query;
import query.definition.result.SQLQueryResult;
import query.exceptions.QueryException;
import query.exceptions.SQLQueryFailedException;

/**
 * Simple query processor which takes query, forms SQL statement, relays it to SQL server, obtains reply and creates query result
 * @author Serhii Pylypenko
 *
 */
public class SQLServerProcessor extends AbstractQueryProcessor {
	
	private final BasicEntityManager entityManager;

	public SQLServerProcessor(final BasicEntityManager entityManager,final Query query) throws QueryException {
		super(query);
		this.entityManager=entityManager;
	}

	@Override
	public SQLQueryResult fetch() throws QueryException {
		buildListOfRelations();

		try(final Statement statement=entityManager.getConnection().createStatement()){
			final String sqlStatement=getSQLStatement();
			final ResultSet resultSet=statement.executeQuery(sqlStatement);
			return new SQLQueryResult(query, this, resultSet);
		}catch(SQLException e) {
			throw new SQLQueryFailedException(e);
		}
	}
	
	private StringBuilder getPropertyName(final QualifiedProperty<? extends Entity,?> property) {
		return new StringBuilder().
				append(property.getEntry().getAlias()).append(".").
				append(entityManager.mapPropertyToAttribute(property.getProperty().getName()));
	}

	private StringBuilder getOrderPropertyName(final OrderProperty<? extends Entity,?> property) {
		final QualifiedProperty qualifiedProperty=property.getQualifiedProperty();
		return 
				qualifiedProperty instanceof AggregationProperty aggregationProperty?
				getAccumulatorPropertyName(aggregationProperty):
					getPropertyName(qualifiedProperty);
	}

	private StringBuilder selectClause() {
		final StringBuilder b=new StringBuilder().append("SELECT ");
		var i=query.selectProperties().iterator();
		if(i.hasNext()) {
			b.append(getPropertyName(i.next()));
			while(i.hasNext()) {
				b.append(",").append(getPropertyName(i.next()));
			}
		}
		return b;
	}

	private StringBuilder aggregationClause() {
		final StringBuilder b=new StringBuilder();
		if(query.aggregationProperties().iterator().hasNext() && 
				query.selectProperties().iterator().hasNext()) {
			b.append(",");
		}
		query.aggregationProperties().forEach(x->b.append(getAccumulatorPropertyName(x)));
		b.append(" ");
		return b;
	}
	
	private StringBuilder getAccumulatorPropertyName(final AggregationProperty<? extends Entity,? extends Numeric> property) {
		return new StringBuilder().
				append(property.aggregator().getName()).
				append("(").
				append(getPropertyName(property)).
				append(")");
	}

	private String getEntryClause() {
		final StringBuilder b=new StringBuilder();
		final Iterator<Entry<? extends Entity>> i=query.entries().iterator();
		if(i.hasNext()) {
			do {
				final Entry<? extends Entity> x=i.next();
				b.
					append(entityManager.mapEntityToTable(x.getEntityClass())).
					append(" AS ").
					append(x.getAlias()).append(" ");
				if(i.hasNext()) {
					b.append("INNER JOIN ");
				}
			}while(i.hasNext());
		}
		return b.toString();		
	}
	
	private StringBuilder fromClause() {
		final String joinClause=getJoinClause();
		final StringBuilder b=new StringBuilder(" FROM ").
				append(getEntryClause()).
				append(!joinClause.isEmpty()?" ON("+joinClause+") ":" ");
		return b;
	}
	
	private String getJoinClause() {
		final StringBuilder b=new StringBuilder();
		for(Link<? extends Entity,? extends Entity> link:relations) {
			if(
					query.containsEntry(link.getSource()) && 
					link.getDestination()!=null &&
					query.containsEntry(link.getDestination())) {
				b.append(getPropertyName(link.getSourceProperty())).
				append("=").
				append(link.getDestination().getAlias()).append(".").append(EntityDefinition.getPrimaryKeyName());
			}
		}
		return b.toString();
	}
	
	private StringBuilder whereClause() {
		final StringBuilder b=new StringBuilder();
		if(query.countPredicates()!=0) {
			b.append("WHERE ");
			query.entries().forEach(x->b.append(predicateClause(x)));
			b.append("\n");
		}
		return b;			
	}
	
	private String predicateClause(final Entry<? extends Entity> entry) {
		final StringJoiner joiner=new StringJoiner(" and ");
		entry.predicates().forEach((Predicate<? extends Entity> x)->joiner.add(x.toString()));
		return joiner.toString();
	}
	
	private StringBuilder orderByClause() {
		final StringBuilder b=new StringBuilder();
		if(query.sortByProperties().iterator().hasNext()) {
			b.append("ORDER BY ");
			query.sortByProperties().forEach(x->{
				b.append(getOrderPropertyName(x)).append(" ").append(x.getOrderKind()==OrderKind.ASCENDING?"ASC":"DESC");
			});
			b.append(" ");
		}
		return b;
	}

	private StringBuilder groupByClause() {
		final StringBuilder b=new StringBuilder();
		if(query.groupByProperties().iterator().hasNext()) {
			b.append("GROUP BY ");
			query.groupByProperties().forEach(x->b.append(getPropertyName(x)));
			b.append(" ");
		}
		return b;
	}

	private StringBuilder havingByClause() {
		final StringBuilder b=new StringBuilder();
		if(query.havingByProperties().iterator().hasNext()) {
			b.append("HAVING BY ");
			query.havingByProperties().forEach(x->b.append(getPropertyName(x)));
			b.append(" ");
		}
		return b;
	}
	
	public String getSQLStatement() {
		return new StringBuilder()
				.append(selectClause())
				.append(aggregationClause())
				.append(fromClause())
				.append(whereClause())
				.append(groupByClause())
				.append(orderByClause())
				.append(havingByClause())
				.toString();
	}

}
