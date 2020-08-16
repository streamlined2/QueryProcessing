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
	
	public void add(final Entry<? extends Entity> entry) {
		entries.add(entry);
	}
	
	public Entry<? extends Entity> get(final int index){
		return entries.get(index);
	}
	
	public int size() {
		return entries.size();
	}
	
	public Character getAlias(final Entry<? extends Entity> e) {
		int index=entries.indexOf(e);
		if(index!=-1) {
			return Character.valueOf((char)('A'+index));			
		}else {
			throw new RuntimeException(String.format("Entry %s not found in list.", e));
		}
	}
	
	@Override
	public String toString() {
		StringJoiner joiner=new StringJoiner(",");
		entries.forEach((Entry<? extends Entity> x)->joiner.add(x.toString()));
		return joiner.toString();
	}

	@Override
	public Iterator<Entry<? extends Entity>> iterator() {
		return entries.iterator();
	}
}
