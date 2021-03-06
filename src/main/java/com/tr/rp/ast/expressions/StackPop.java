package com.tr.rp.ast.expressions;

import java.util.Objects;
import java.util.Set;

import com.tr.rp.ast.AbstractExpression;
import com.tr.rp.ast.LanguageElement;
import com.tr.rp.exceptions.RPLEmptyStackException;
import com.tr.rp.exceptions.RPLException;
import com.tr.rp.varstore.VarStore;
import com.tr.rp.varstore.arrays.ArrayFactory;
import com.tr.rp.varstore.arrays.PersistentObjectArray;
import com.tr.rp.varstore.datastructures.PersistentStack;
import com.tr.rp.varstore.datastructures.PersistentStack.PopResult;
import com.tr.rp.varstore.types.Type;

/**
 * [stack, item] = pop(stack): pop item from stack.
 */
public class StackPop extends AbstractExpression {

	private final AbstractExpression e;

	public StackPop(AbstractExpression e) {
		this.e = e;
	}

	@Override
	public void getVariables(Set<String> list) {
		e.getVariables(list);
	}

	@Override
	public boolean hasRankExpression() {
		return e.hasRankExpression();
	}

	@Override
	public AbstractFunctionCall getEmbeddedFunctionCall() {
		return e.getEmbeddedFunctionCall();
	}

	public AbstractExpression transformRankExpressions(VarStore v, int rank) throws RPLException {
		AbstractExpression x = new StackPop(e.transformRankExpressions(v, rank));
		x.setLineNumber(getLineNumber());
		return x;
	}

	@Override
	public AbstractExpression replaceEmbeddedFunctionCall(AbstractFunctionCall fc, String var) {
		AbstractExpression x = new StackPop(e.replaceEmbeddedFunctionCall(fc, var));
		x.setLineNumber(getLineNumber());
		return x;
	}

	@Override
	public Object getValue(VarStore e) throws RPLException {
		PopResult<?> popResult = this.e.getValue(e, Type.STACK).pop();
		return ArrayFactory.newArrayOf(popResult.mutatedStack, popResult.poppedElement);
	}

	@Override
	public boolean hasDefiniteValue() {
		return e.hasDefiniteValue();
	}

	@Override
	public Object getDefiniteValue() throws RPLException {
		return this.e.getDefiniteValue(Type.STACK).peek();
	}

	public String toString() {
		return "pop(" + e + ")";
	}

	public boolean equals(Object o) {
		return (o instanceof StackPop) && ((StackPop) o).e.equals(e);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClass().hashCode(), e.hashCode());
	}

}
