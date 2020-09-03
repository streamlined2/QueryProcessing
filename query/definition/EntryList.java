package query.definition;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import entity.definition.Entity;

public class EntryList implements Iterable<Entry<? extends Entity>> {
	private final List<Entry<? extends Entity>> entries=new LinkedList<>();
	
	public boolean empty() {
		return entries.isEmpty();
	}
	
	public <T extends Entity> Entry<T> add(final Query query,final Class<T> entityClass) {
		final Entry<T> entry=new Entry<>(query,entityClass,getNextAlias());
		entries.add(entry);
		return entry;
	}
	
	public Entry<? extends Entity> get(final int index){
		return entries.get(index);
	}
	
	public int size() {
		return entries.size();
	}
	
	private Character getNextAlias() {
		final int nextIndex=entries.size();
		if(nextIndex<('Z'-'A'+1)) {
			return Character.valueOf((char)('A'+nextIndex));			
		}else {
			throw new RuntimeException(String.format("There are too many entries in the query already."));
		}
	}
	
	@Override
	public String toString() {
		final StringJoiner joiner=new StringJoiner(",");
		entries.forEach((Entry<? extends Entity> x)->joiner.add(x.toString()+" AS "+x.getAlias()));
		return joiner.toString();
	}

	@Override
	public Iterator<Entry<? extends Entity>> iterator() {
		return entries.iterator();
	}
}
