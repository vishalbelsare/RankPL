package com.tr.rp.ast.expressions;

import java.util.Objects;
import java.util.Set;

import com.tr.rp.ast.AbstractExpression;
import com.tr.rp.ast.LanguageElement;
import com.tr.rp.ast.StringTools;
import com.tr.rp.exceptions.RPLException;
import com.tr.rp.exceptions.RPLIndexOutOfBoundsException;
import com.tr.rp.exceptions.RPLMiscException;
import com.tr.rp.varstore.VarStore;
import com.tr.rp.varstore.types.Type;

public class SubString extends AbstractExpression {

	private final AbstractExpression input;
	private final AbstractExpression begin;
	private final AbstractExpression end;

	public SubString(AbstractExpression stringExp, AbstractExpression start, AbstractExpression end) {
		this.input = stringExp;
		this.begin = start;
		this.end = end;
	}

	@Override
	public void getVariables(Set<String> list) {
		input.getVariables(list);
		begin.getVariables(list);
		end.getVariables(list);
	}

	@Override
	public boolean hasRankExpression() {
		return input.hasRankExpression() || begin.hasRankExpression() || end.hasRankExpression();
	}

	@Override
	public AbstractExpression transformRankExpressions(VarStore v, int rank) throws RPLException {
		AbstractExpression e = new SubString((AbstractExpression) input.transformRankExpressions(v, rank),
				(AbstractExpression) begin.transformRankExpressions(v, rank),
				(AbstractExpression) end.transformRankExpressions(v, rank));
		e.setLineNumber(getLineNumber());
		return e;
	}

	@Override
	public AbstractFunctionCall getEmbeddedFunctionCall() {
		AbstractFunctionCall fc = input.getEmbeddedFunctionCall();
		if (fc != null) {
			return fc;
		}
		fc = begin.getEmbeddedFunctionCall();
		if (fc != null) {
			return fc;
		}
		return end.getEmbeddedFunctionCall();
	}

	@Override
	public AbstractExpression replaceEmbeddedFunctionCall(AbstractFunctionCall fc, String var) {
		AbstractExpression e = new SubString((AbstractExpression) input.replaceEmbeddedFunctionCall(fc, var),
				(AbstractExpression) begin.replaceEmbeddedFunctionCall(fc, var),
				(AbstractExpression) end.replaceEmbeddedFunctionCall(fc, var));
		e.setLineNumber(getLineNumber());
		return e;
	}

	@Override
	public Object getValue(VarStore vs) throws RPLException {
		String s = input.getValue(vs, Type.STRING);
		int beginIndex = begin.getValue(vs, Type.INT);
		if (beginIndex < 0 || beginIndex >= s.length()) {
			throw new RPLIndexOutOfBoundsException(beginIndex, s.length(), this);
		}
		int endIndex = end.getValue(vs, Type.INT);
		if (endIndex < beginIndex) {
			throw new RPLMiscException("Illegal substring range (" + beginIndex + ", " + endIndex + ")");
		}
		if (endIndex > s.length()) {
			throw new RPLIndexOutOfBoundsException(endIndex, s.length(), this);
		}
		return s.substring(beginIndex, endIndex);
	}

	@Override
	public boolean hasDefiniteValue() {
		return input.hasDefiniteValue() && begin.hasDefiniteValue() && end.hasDefiniteValue();
	}

	@Override
	public Object getDefiniteValue() throws RPLException {
		String s = input.getDefiniteValue(Type.STRING);
		int beginIndex = begin.getDefiniteValue(Type.INT);
		if (beginIndex < 0 || beginIndex >= s.length()) {
			throw new RPLIndexOutOfBoundsException(beginIndex, s.length(), this);
		}
		int endIndex = end.getDefiniteValue(Type.INT);
		if (endIndex < beginIndex) {
			throw new RPLMiscException("Illegal substring range (" + beginIndex + ", " + endIndex + ")");
		}
		if (endIndex > s.length()) {
			throw new RPLIndexOutOfBoundsException(endIndex, s.length(), this);
		}
		return s.substring(beginIndex, endIndex);
	}

	public String toString() {
		return "SubString(" + StringTools.stripPars(input.toString()) + "," + StringTools.stripPars(begin.toString())
				+ ", " + StringTools.stripPars(end.toString()) + ")";
	}

	public boolean equals(Object o) {
		return (o instanceof SubString) && ((SubString) o).input.equals(input) && ((SubString) o).begin.equals(begin)
				&& ((SubString) o).end.equals(end);
	}

	@Override
	public int hashCode() {
		return Objects.hash(input, begin, end);
	}

}
