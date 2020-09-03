package entity.definition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *  Holds data for a set of entities
 *  
 * @author Serhii Pylypenko
 *
 */

public class DataSource implements Iterable<EntitySource<? extends Entity>>{
	
	private List<EntitySource<? extends Entity>> data=new LinkedList<>();
	
	@SuppressWarnings("unchecked")
	public DataSource(final EntitySource<? extends Entity>...sources) {
		data.addAll(Arrays.asList(sources));
	}
	
	public DataSource addSource(final EntitySource<? extends Entity> e) {
		data.add(e);
		return this;
	}

	@Override
	public Iterator<EntitySource<? extends Entity>> iterator() {
		return data.iterator();
	} 

}