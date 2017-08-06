package com.tr.rp.core.rankediterators;

import com.tr.rp.exceptions.RPLException;
import com.tr.rp.iterators.ranked.AbsurdIterator;
import com.tr.rp.iterators.ranked.ChooseMergingIteratorFixed;
import com.tr.rp.iterators.ranked.DuplicateRemovingIterator;
import com.tr.rp.iterators.ranked.RankedIterator;
import com.tr.rp.statement.RPLBaseTest;
import com.tr.rp.varstore.VarStore;
import com.tr.rp.varstore.types.Type;

public class ChooseMergingIteratorFixedTest extends RPLBaseTest {

	public void testEqualInput1() throws RPLException {
		for (int i = 0; i < 5; i++) {
			ChooseMergingIteratorFixed s = new ChooseMergingIteratorFixed(getTestIterator(), getTestIterator(), i);
			DuplicateRemovingIterator<VarStore> ds = new DuplicateRemovingIterator<VarStore>(s);
			iteratorsEqual(ds, getTestIterator());
		}
	}
	
	public void testWithNonEmptyIterators1() throws RPLException {
		for (int i = 0; i < 12; i++) {
			for (int as = 1; as < 6; as++) {
				for (int bs = 1; bs < 6; bs++) {
					int count = 0;
					ChooseMergingIteratorFixed s = new ChooseMergingIteratorFixed(getIterator("a", as), getIterator("b", bs), i);
					while (s.next()) {
						count++;
						VarStore v = s.getItem();
						if (v.getValue("a") != null) {
							assertEquals(s.getRank(), (int)v.getValue("a", Type.INT));
						} else if (v.getValue("b") != null) {
							assertEquals(s.getRank() - i, (int)v.getValue("b", Type.INT));
						}
					}
					assert(count == as + bs);
				}
			}
		}
	}
	
	public void testWithEmptyA1() throws RPLException {
		for (int i = 0; i < 10; i++) {
			for (int bs = 1; bs < 6; bs++) {
				ChooseMergingIteratorFixed s = new ChooseMergingIteratorFixed(new AbsurdIterator<VarStore>(), getIterator("b", bs), i);
				for (int j = 0; j < bs; j++) {
					assert(s.next());
					assertEquals((int)s.getItem().getValue("b", Type.INT), j);
				}
				assert(!s.next());
			}
		}
	}
	
	public void testWithEmptyB1() throws RPLException {
		for (int i = 0; i < 10; i++) {
			for (int as = 1; as < 6; as++) {
				ChooseMergingIteratorFixed s = new ChooseMergingIteratorFixed(getIterator("a", as), new AbsurdIterator<VarStore>(), i);
				for (int j = 0; j < as; j++) {
					assert(s.next());
					assertEquals((int)s.getItem().getValue("a", Type.INT), j);
				}
				assert(!s.next());
			}
		}
	}
	
	public void testEqualInput2() throws RPLException {
		for (int i = 0; i < 5; i++) {
			ChooseMergingIteratorFixed s = new ChooseMergingIteratorFixed(getTestIterator(), getTestIterator(), i);
			DuplicateRemovingIterator<VarStore> ds = new DuplicateRemovingIterator<VarStore>(s);
			iteratorsEqual(ds, getTestIterator());
		}
	}
	
	public void testWithNonEmptyIterators2() throws RPLException {
		for (int i = 0; i < 12; i++) {
			for (int as = 1; as < 6; as++) {
				for (int bs = 1; bs < 6; bs++) {
					int count = 0;
					ChooseMergingIteratorFixed s = new ChooseMergingIteratorFixed(getIterator("a", as), getIterator("b", bs), i);
					while (s.next()) {
						count++;
						VarStore v = s.getItem();
						if (v.getValue("a") != null) {
							assertEquals(s.getRank(), (int)s.getItem().getValue("a", Type.INT));
						} else if (v.getValue("b") != null) {
							assertEquals(s.getRank() - i, (int)s.getItem().getValue("b", Type.INT));
						}
					}
					assert(count == as + bs);
				}
			}
		}
	}
	
	public void testWithEmptyA2() throws RPLException {
		for (int i = 0; i < 10; i++) {
			for (int bs = 1; bs < 6; bs++) {
				ChooseMergingIteratorFixed s = new ChooseMergingIteratorFixed(new AbsurdIterator<VarStore>(), getIterator("b", bs), i);
				for (int j = 0; j < bs; j++) {
					assert(s.next());
					assertEquals((int)s.getItem().getValue("b", Type.INT), j);
				}
				assert(!s.next());
			}
		}
	}
	
	public void testWithEmptyB2() throws RPLException {
		for (int i = 0; i < 10; i++) {
			for (int as = 1; as < 6; as++) {
				ChooseMergingIteratorFixed s = new ChooseMergingIteratorFixed(getIterator("a", as), new AbsurdIterator<VarStore>(), i);
				for (int j = 0; j < as; j++) {
					assert(s.next());
					assertEquals((int)s.getItem().getValue("a", Type.INT), j);
				}
				assert(!s.next());
			}
		}
	}

	
	public void testNormalization() throws RPLException {
		for (int i = 0; i < 10; i++) {
			for (int r = 0; r < 10; r++) {
				ChooseMergingIteratorFixed s = new ChooseMergingIteratorFixed(new AbsurdIterator<VarStore>(), getIterator("a", i), r);
				for (int j = 0; j < i; j++) {
					assert(s.next());
					assertEquals((int)s.getItem().getValue("a", Type.INT), j);
					assertEquals((int)s.getRank(), j);
				}
				assert(!s.next());
			}
		}
	}

	private RankedIterator<VarStore> getIterator(String var, int b) {
		return new RankedIterator<VarStore>() {
			private int i = -1;

			@Override
			public boolean next() throws RPLException {
				i++;
				return i < b;
			}

			@Override
			public VarStore getItem() throws RPLException {
				if (i >= b) {
					throw new IllegalStateException();
				}
				VarStore v = new VarStore();
				v = v.create(var, i);
				return v;
			}

			@Override
			public int getRank() {
				if (i >= b) {
					throw new IllegalStateException();
				}
				return i;
			}
			
		};
	}

}