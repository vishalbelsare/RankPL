package com.tr.rp.ast.expressions;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import com.tr.rp.ast.AbstractExpression;
import com.tr.rp.ast.LanguageElement;
import com.tr.rp.exceptions.RPLException;
import com.tr.rp.varstore.VarStore;
import com.tr.rp.varstore.types.Type;

public class NumNumBoolOp extends AbstractExpression {

	private final AbstractExpression e1, e2;
	private final BiFunction<Integer, Integer, Boolean> f;
	
	public NumNumBoolOp(BiFunction<Integer, Integer, Boolean> f, AbstractExpression e1, AbstractExpression e2) {
		this.e1 = e1;
		this.e2 = e2;
		this.f = f;
	}

	@Override
	public void getVariables(Set<String> list) {
		e1.getVariables(list);
		e2.getVariables(list);
	}

	@Override
	public LanguageElement replaceVariable(String a, String b) {
		return new NumNumBoolOp(f, (AbstractExpression)e1.replaceVariable(a, b), (AbstractExpression)e2.replaceVariable(a, b));
	}

	@Override
	public boolean needsRankExpressionTransformation() {
		return e1.needsRankExpressionTransformation() || e2.needsRankExpressionTransformation();
	}

	@Override
	public AbstractExpression doRankExpressionTransformation(VarStore v, int rank) throws RPLException {
		return new NumNumBoolOp(f, (AbstractExpression)e1.doRankExpressionTransformation(v, rank), (AbstractExpression)e2.doRankExpressionTransformation(v, rank));
	}

	@Override
	public AbstractFunctionCall getEmbeddedFunctionCall() {
		AbstractFunctionCall fc = e1.getEmbeddedFunctionCall();
		if (fc != null) return fc;
		return e2.getEmbeddedFunctionCall();
	}

	@Override
	public AbstractExpression replaceEmbeddedFunctionCall(AbstractFunctionCall fc, String var) {
		return new NumNumBoolOp(f, (AbstractExpression)e1.replaceEmbeddedFunctionCall(fc, var), (AbstractExpression)e2.replaceEmbeddedFunctionCall(fc, var));
	}

	@Override
	public Object getValue(VarStore e) throws RPLException {
		int o1 = e1.getValue(e, Type.INT);
		int o2 = e2.getValue(e, Type.INT);
		return f.apply(o1, o2);
	}
	@Override
	public boolean hasDefiniteValue() {
		return e1.hasDefiniteValue() && e2.hasDefiniteValue();
	}

	@Override
	public Object getDefiniteValue() throws RPLException {
		return f.apply((int)e1.getDefiniteValue(Type.INT), (int)e2.getDefiniteValue(Type.INT));
	}
	
	public AbstractExpression getE1() {
		return e1;
	}

	public AbstractExpression getE2() {
		return e2;
	}
	
	public String toString() {
		return "(" + f.toString().replace("$1", e1.toString()).replace("$2", e2.toString()) + ")";
	}
	
	public boolean equals(Object o) {
		return (o instanceof NumNumBoolOp) &&
				((NumNumBoolOp)o).e1.equals(e1) &&
				((NumNumBoolOp)o).e2.equals(e2) &&
				((NumNumBoolOp)o).f == f;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(e1, e2, f);
	}

}
