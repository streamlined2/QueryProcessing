package query.definition;

/**
 * Holds query definition
 * @author Serhii Pylypenko
 *
 */

public class Query {
	
	private final EntityList sources;
	private PropertyList selectProperties;
	private PredicateList predicates;
	private PropertyList sortByProperties;
	private PropertyList groupByProperties;
	
	public Query(final AliasedEntity...entries) {
		sources=new EntityList(entries);
		selectProperties=null;//fetch all properties of all entities
	}
	
	public void select() {
		
	}

}
