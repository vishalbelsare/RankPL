package com.tr.rp.executors;

import com.tr.rp.base.State;
import com.tr.rp.exceptions.RPLException;

/**
 * An executor that passes on its input to another executor unchanged, and 
 * checks the input for correctness (i.e., correct rank order, and no push 
 * or close events after the first close event). 
 * 
 * A static method (checkIfEnabled) is provided which applies this executor
 * on the basis of a static flag.
 */
public final class Guard implements Executor {

	private final Executor out;
	private boolean closed = false;
	private int rank = 0;
	private boolean first = true;
	
	public static boolean enabled = false;
	
	/**
	 * @return Guard executor only if enabled.
	 */
	public static synchronized Executor checkIfEnabled(Executor out) {
		return enabled? new Guard(out): out;
	}
	
	/**
	 * Set enabled flag.
	 */
	public static synchronized void setEnabled(boolean f) {
		enabled = f;
	}
	
	private Guard(Executor out) {
		this.out = out;
	}
	
	@Override
	public void close() throws RPLException {
		if (closed) {
			throw new IllegalStateException("Close called on closed executor");
		}
		closed = true;
		out.close();
	}

	@Override
	public void push(State s) throws RPLException {
		if (closed) {
			throw new IllegalStateException("Push called on closed executor");
		}
		if (first && s.getRank() != 0) {
			throw new IllegalStateException("First pushed state should be ranked 0 (got " + s.getRank() + ")");
		}
		first = false;
		if (s.getRank() < rank) {
			throw new IllegalStateException("Illegal rank order (got " + s.getRank() + " but should be at least " + rank + ")");
		}
		rank = s.getRank();
		out.push(s);
	}

}
