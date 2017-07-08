package com.tr.rp.varstore.types;

import org.organicdesign.fp.collections.PersistentHashSet;

/**
 * Persistent set data structure. Mutations create new set objects
 * without changing the original set object.
 */
public class PersistentSet<T> {

	private final PersistentHashSet<T> set;
	
	public PersistentSet() {
		this.set = PersistentHashSet.empty();
	}
	
	private PersistentSet(PersistentHashSet<T> set) {
		this.set = set;
	}

	public boolean contains(T o) {
		return set.contains(o);
	}

	public PersistentSet<T> put(T o) {
		if (set.contains(o)) {
			return this;
		}
		return new PersistentSet<T>(set.put(o));
	}
	
	public PersistentSet<T> remove(T o) {
		if (set.contains(o)) {
			return this;
		}
		return new PersistentSet<T>(set.without(o));
	}
	
	public int size() {
		return set.size();
	}
	
	
}
