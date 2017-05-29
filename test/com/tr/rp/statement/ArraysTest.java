package com.tr.rp.statement;

import static com.tr.rp.expressions.Expressions.*;

import com.tr.rp.core.Expression;
import com.tr.rp.core.ProgramBuilder;
import com.tr.rp.core.VarStore;
import com.tr.rp.core.rankediterators.InitialVarStoreIterator;
import com.tr.rp.core.rankediterators.RankedIterator;
import com.tr.rp.exceptions.RPLException;
import com.tr.rp.expressions.ArrayInitExpression;
import com.tr.rp.expressions.PersistentList;
import com.tr.rp.expressions.Variable;
import com.tr.rp.expressions.Literal;

public class ArraysTest extends RPLBaseTest {

	public void testArrayInitialization() throws RPLException {
		
		final int d1 = 5;
		final int d2 = 4;
		final int d3 = 3;
		
		// 1D array of ints
		RankedIterator<VarStore> result = new Assign("x", 
				new ArrayInitExpression(null, new Expression[] { new Literal<Integer>(d1) }))
				.getIterator(new InitialVarStoreIterator());
		assert(result.next());
		PersistentList list = (PersistentList)result.getItem().getValue("x");
		assertEquals(d1, list.size());
		for (int i = 0; i < d1; i++) {
			assertEquals(0, list.get(i));
		}

		// 1D array of strings
		result = new Assign("x", 
				new ArrayInitExpression(new Literal<String>(""), new Expression[] { new Literal<Integer>(d1) }))
				.getIterator(new InitialVarStoreIterator());
		assert(result.next());
		assert(result.getItem().getValue("x") instanceof PersistentList);
		list = (PersistentList)result.getItem().getValue("x");
		assertEquals(d1, list.size());
		for (int i = 0; i < d1; i++) {
			assertEquals("", list.get(i));
		}

		// 2D array of ints
		result = new Assign("x", 
				new ArrayInitExpression(null, new Expression[] { new Literal<Integer>(d1), new Literal<Integer>(d2) }))
				.getIterator(new InitialVarStoreIterator());
		assert(result.next());
		assert(result.getItem().getValue("x") instanceof PersistentList);
		list = (PersistentList)result.getItem().getValue("x");
		assertEquals(5, list.size());
		for (int i = 0; i < d1; i++) {
			assert(list.get(i) instanceof PersistentList);
			PersistentList list2 = (PersistentList)list.get(i);
			assert(list2.size() == d2);
			for (int j = 0; j < d2; j++) {
				assertEquals(0, list2.get(j));
			}
		}

		// 3D array of ints
		result = new Assign("x", 
				new ArrayInitExpression(null, new Expression[] { new Literal<Integer>(d1), new Literal<Integer>(d2), new Literal<Integer>(d3)  }))
				.getIterator(new InitialVarStoreIterator());
		assert(result.next());
		assert(result.getItem().getValue("x") instanceof PersistentList);
		list = (PersistentList)result.getItem().getValue("x");
		assertEquals(5, list.size());
		for (int i = 0; i < d1; i++) {
			assert(list.get(i) instanceof PersistentList);
			PersistentList list2 = (PersistentList)list.get(i);
			assert(list2.size() == d2);
			for (int j = 0; j < d2; j++) {
				assert(list2.get(j) instanceof PersistentList);
				PersistentList list3 = (PersistentList)list2.get(j);
				assert(list3.size() == d3);
				for (int k = 0; k < d3; k++) {
					assertEquals(0, list3.get(k));
				}
			}
		}
	}
	
	public void test1DArrayAssignment() throws RPLException {
		ProgramBuilder p = new ProgramBuilder()
				.add(new Assign("x", 
						new ArrayInitExpression(null, 
								new Expression[] { 
										new Literal<Integer>(3) })))
				.add(new Assign(target("x", 0), 
						new Literal<Integer>(1)))
				.add(new Assign(target("x", 1), 
						new Literal<Integer>(2)))
				.add(new Assign(target("x", 2), 
						new Literal<Integer>(3)))
				.add(new Assign(target("x", 2), 
						new Literal<Integer>(100)));
		RankedIterator<VarStore> result = p.build().getIterator(new InitialVarStoreIterator());
		
		assert(result.next());
		VarStore vs = result.getItem();
		assert(!result.next());
		assertEquals(1, (indexedExp(var("x"), 0)).getIntValue(vs));
		assertEquals(2, (indexedExp(var("x"), 1)).getIntValue(vs));
		assertEquals(100, (indexedExp(var("x"), 2)).getIntValue(vs));
	}
	
	public void test2DArrayAssignment() throws RPLException {
		ProgramBuilder p = new ProgramBuilder()
				.add(new Assign("x", 
						new ArrayInitExpression(null, 
								new Expression[] { 
										new Literal<Integer>(3), new Literal<Integer>(4) })))
				.add(new Assign(target("x", 0, 1), 
						new Literal<Integer>(1)))
				.add(new Assign(target("x", 1, 2), 
						new Literal<Integer>(2)))
				.add(new Assign(target("x", 2, 3), 
						new Literal<Integer>(3)))
				.add(new Assign(target("x", 2, 3), 
						new Literal<Integer>(100)));
		RankedIterator<VarStore> result = p.build().getIterator(new InitialVarStoreIterator());
		
		assert(result.next());
		VarStore vs = result.getItem();
		assert(!result.next());
		assertEquals(1, (indexedExp(var("x"), 0, 1)).getIntValue(vs));
		assertEquals(2, (indexedExp(var("x"), 1, 2)).getIntValue(vs));
		assertEquals(100, (indexedExp(var("x"), 2, 3)).getIntValue(vs));
	}
	
	public void testIndexed1DExpression() throws RPLException {
		ProgramBuilder p = new ProgramBuilder()
				.add(new Assign("x", 
						new ArrayInitExpression(null, 
								new Expression[] { 
										new Literal<Integer>(3) })))
				.add(new Assign(target("x", 2), 
						new Literal<Integer>(100)))
				.add(new Assign("y", indexedExp(var("x"), 2)));
		RankedIterator<VarStore> result = p.build().getIterator(new InitialVarStoreIterator());
		
		assert(result.next());
		VarStore vs = result.getItem();
		assert(!result.next());
		assertEquals(100, (indexedExp(var("x"), 2)).getIntValue(vs));
		assertEquals(100, vs.getIntValue("y"));
	}
	
	public void testIndexed2DExpression() throws RPLException {
		ProgramBuilder p = new ProgramBuilder()
				.add(new Assign("x", 
						new ArrayInitExpression(null, 
								new Expression[] { 
										new Literal<Integer>(3),
										new Literal<Integer>(4)})))
				.add(new Assign(target("x", 2, 3), new Literal<Integer>(100)))
				.add(new Assign("y", indexedExp(var("x"), 2, 3)));
		RankedIterator<VarStore> result = p.build().getIterator(new InitialVarStoreIterator());
		
		assert(result.next());
		VarStore vs = result.getItem();
		assert(!result.next());
		assertEquals(100, indexedExp(var("x"), 2, 3).getIntValue(vs));
		assertEquals(100, vs.getIntValue("y"));
	}

}
