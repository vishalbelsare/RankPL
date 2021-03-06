package com.tr.rp.varstore;

import java.util.List;

import com.tr.rp.ast.AbstractExpression;
import com.tr.rp.exceptions.RPLException;
import com.tr.rp.exceptions.RPLTypeError;
import com.tr.rp.exceptions.RPLUndefinedException;
import com.tr.rp.varstore.types.Type;

/**
 * Represents a variable store: an assignment of values to variables.
 */
public interface VarStore {

	/**
	 * Return value but throw RPLUndefinedException if the variable is
	 * not defined, and return RPLTypeError if the value is not of the
	 * provided type.
	 * 
	 * @return Value of given variable
	 */
	public <T> T getValue(String var, Type<T> asType) throws RPLUndefinedException, RPLTypeError;
	
	/**
	 * @return Value of given variable (return null if not initialized).
	 */
	public Object getValue(String var);
	
	/**
	 * Mutates this variable store. Returns a new variable store where the
	 * given variable is set to the given value, without changing the
	 * original variable store. Using a null value will un-set the variable.
	 * 
	 * @return A new variable store where var is set to value.
	 */
	public VarStore create(String var, Object value);

	/**
	 * @return A new variable store where var is set to null (uninitialized).
	 */
	public VarStore unset(String var);
	
	/**
	 * @return A marginalization of this variable store to a given set of variables.
	 */
	public VarStore marginalize(List<String> vars);

	public boolean equals(Object o);
	
	public int hashCode();
	
	public String toString();

	/**
	 * @return Number of assigned variables
	 */
	public int getSize();

	/**
	 * @return True if var is assigned a value.
	 */
	public boolean containsVar(String var);

	public VarStore getParent();
	
	public VarStore createClosureWith(String[] vars, List<Object> values) throws RPLException;

	public VarStore getParentOfClosure(String target, AbstractExpression returnValueExp) throws RPLException;

}
