package com.tr.rp.executors;

import java.util.LinkedList;

import com.tr.rp.base.Rank;
import com.tr.rp.base.State;
import com.tr.rp.exceptions.RPLException;

/**
 * Merger is constructed with a given output executor and produces two 
 * input executors. States pushed into the two input executors are merged
 * (in such a way that rank order is respected) and pushed into the output 
 * executor. 
 * 
 * In addition, Merger allows the ranks of the states pushed into the second
 * iterator the be shifted up by a constant number of ranks. This number is
 * specified by the shift parameter. This is the basic functionality required 
 * by the ranked choice statement.
 * 
 * See also DynamicMerger, which does the same, except that the shift parameter
 * is an expression whose value depends on the state that is pushed. This 
 * constant-value merger is, however, more efficient, and should be used
 * whenever the shift parameter is known to be a constant.
 */
public final class Merger {

	private final Executor out;
	private final Executor in1;
	private final Executor in2;
	private boolean in1Closed = false;
	private boolean in2Closed = false;
	
	private final LinkedList<State> in1Queue = new LinkedList<State>();
	private final LinkedList<State> in2Queue = new LinkedList<State>();
	
	private int minPotentialNextRank1 = 0;
	private int minPotentialNextRank2 = 0;
	
	private final int shift;
	
	private int outputOffset = -1;
	
	public Merger(Executor out) {
		this(Guard.checkIfEnabled(out), 0);
	}
	
	public Merger(Executor out, int shift) {
		this.shift = shift;
		this.out = new Executor() {

			@Override
			public void close() throws RPLException {
				out.close();
			}

			@Override
			public void push(State s) throws RPLException {
				if (outputOffset == -1) {
					outputOffset = s.getRank();
				}
				out.push(s.shiftDown(outputOffset));
			}
			
		};
		this.in1 = new Executor() {

			@Override
			public void close() throws RPLException {
				in1Closed = true;
				minPotentialNextRank1 = Rank.MAX;
				flush();
			}

			@Override
			public void push(State s) throws RPLException {
				if (in1Closed) {
					throw new IllegalStateException();
				}
				minPotentialNextRank1 = s.getRank();
				in1Queue.addLast(s);
				flush();
			}
			
		};
		this.in2 = new Executor() {

			@Override
			public void close() throws RPLException {
				in2Closed = true;
				flush();
			}

			@Override
			public void push(State s) throws RPLException {
				if (in2Closed) {
					throw new IllegalStateException();
				}
				minPotentialNextRank2 = s.getRank();
				in2Queue.addLast(s.shiftUp(shift));
				flush();
			}
			
		};
	}
	
	private void flush() throws RPLException {
		State s = getNextItem();
		while (s != null) {
			out.push(s);
			s = getNextItem();
		}
		if (in1Closed && in2Closed) {
			out.close();
		}
	}
	
	private State getNextItem() {
		
		// Can we return item from queue 1?
		boolean in1Filled = !in1Queue.isEmpty();
		boolean in2Filled = !in2Queue.isEmpty();

		if (in1Filled) {
			int in1Rank = in1Queue.getFirst().getRank();
			
			// Does rank not exceed shift? Then return item from queue 1.
			if (in1Rank <= shift) {
				return in1Queue.removeFirst();
			}
			
			// Is queue 2 filled, but first item is ranked lower? Then return item from queue 1.
			if (in2Filled && in2Queue.getFirst().getRank() >= in1Rank) {
				return in1Queue.removeFirst();
			}
			
			// Is queue 2 empty, but input is closed? Then return item from queue 1.
			if (!in2Filled && in2Closed) {
				return in1Queue.removeFirst();
			}
			
			// Is queue 2 empty, but any item that might be added there will be ranked higher? 
			// Then return item from queue 1.
			int minPotentialNextRankInQueue2 = Rank.add(minPotentialNextRank2, shift);
			if (!in2Filled && minPotentialNextRankInQueue2 >= in1Rank) {
				return in1Queue.removeFirst();
			}
		}
		
		if (in2Filled) {
			int in2Rank = in2Queue.getFirst().getRank();

			// Is queue 1 filled, but first item is ranked lower? Then return item from queue 2.
			if (in1Filled && in1Queue.getFirst().getRank() > in2Rank) {
				return in2Queue.removeFirst();
			}
			
			// Is queue 1 empty, but input is closed? Then return item from queue 2.
			if (!in1Filled && in1Closed) {
				return in2Queue.removeFirst();
			}

			// Is queue 1 empty, but any item that might be added there will be ranked higher? 
			// Then return item from queue 2.
			if (!in1Filled && minPotentialNextRank1 >= in2Rank) {
				return in2Queue.removeFirst();
			}
		}
		
		return null;
	}

	public Executor getIn1() {
		return in1;
	}

	public Executor getIn2() {
		return in2;
	}
	
}
