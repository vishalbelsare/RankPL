package com.tr.rp.expressions;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import com.tr.rp.core.Expression;
import com.tr.rp.core.LanguageElement;
import com.tr.rp.core.VarStore;
import com.tr.rp.exceptions.RPLException;
import com.tr.rp.exceptions.RPLMiscException;
import com.tr.rp.exceptions.RPLTypeMismatchException;
import com.tr.rp.exceptions.RPLUndefinedException;

/**
 * The equality (==) expression. This expression evaluates to true iff 
 * both arguments have the same type represent the same value (whether
 * integer, boolean, string, or list).
 */
public class Equals extends Expression {

	private final Expression e1, e2;
	
	public Equals(Expression e1, Expression e2) {
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public void getVariables(Set<String> list) {
		e1.getVariables(list);
		e2.getVariables(list);
	}

	@Override
	public LanguageElement replaceVariable(String a, String b) {
		return new Equals((Expression)e1.replaceVariable(a, b), (Expression)e2.replaceVariable(a, b));
	}

	@Override
	public boolean hasRankExpression() {
		return e1.hasRankExpression() || e2.hasRankExpression();
	}

	@Override
	public Expression transformRankExpressions(VarStore v, int rank) throws RPLException {
		return new Equals((Expression)e1.transformRankExpressions(v, rank), (Expression)e2.transformRankExpressions(v, rank));
	}

	@Override
	public AbstractFunctionCall getEmbeddedFunctionCall() {
		AbstractFunctionCall fc = e1.getEmbeddedFunctionCall();
		if (fc != null) return fc;
		return e2.getEmbeddedFunctionCall();
	}

	@Override
	public Expression replaceEmbeddedFunctionCall(AbstractFunctionCall fc, String var) {
		return new Equals((Expression)e1.replaceEmbeddedFunctionCall(fc, var), (Expression)e2.replaceEmbeddedFunctionCall(fc, var));
	}

	@Override
	public Object getValue(VarStore v) throws RPLException {
		Object v1 = e1.getValue(v);
		Object v2 = e2.getValue(v);
		if (v1 == null) {
			throw new RPLUndefinedException(e1);
		}
		if (v2 == null) {
			throw new RPLUndefinedException(e2);
		}
		if (!Objects.equals(v1.getClass(), v2.getClass())) {
			throw new RPLTypeMismatchException(v1, v2, this);
		}
		return Objects.equals(v1, v2);
	}
	
	@Override
	public boolean hasDefiniteValue() {
		return e1.hasDefiniteValue() && e2.hasDefiniteValue();
	}

	@Override
	public Object getDefiniteValue() throws RPLException {
		Object v1 = e1.getDefiniteValue();
		Object v2 = e2.getDefiniteValue();
		if (v1 == null) {
			throw new RPLUndefinedException(e1);
		}
		if (v2 == null) {
			throw new RPLUndefinedException(e2);
		}
		if (!Objects.equals(v1.getClass(), v2.getClass())) {
			throw new RPLTypeMismatchException(v1, v2, this);
		}
		return Objects.equals(v1, v2);
	}
	
	public Expression getE1() {
		return e1;
	}

	public Expression getE2() {
		return e2;
	}
	
	public String toString() {
		return "(" + e1 + " == " + e2 + ")";
	}
	
	public boolean equals(Object o) {
		return (o instanceof Equals) &&
				((Equals)o).e1.equals(e1) &&
				((Equals)o).e2.equals(e2);
	}

}