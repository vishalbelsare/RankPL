package com.tr.rp.ast.expressions;

import com.tr.rp.ast.AbstractExpression;
import com.tr.rp.ast.LanguageElement;
import com.tr.rp.exceptions.RPLEmptyStackException;
import com.tr.rp.exceptions.RPLException;
import com.tr.rp.iterators.ranked.ExecutionContext;
import com.tr.rp.iterators.ranked.RankedIterator;
import com.tr.rp.varstore.VarStore;
import com.tr.rp.varstore.types.PersistentStack;
import com.tr.rp.varstore.types.Type;

/**
 * pop(stack): pops top element from stack
 */
public class StackPop extends AbstractFunctionCall {

	private final AssignmentTarget assignmentTarget;
	
	public StackPop(AssignmentTarget assignmentTarget) {
		this.assignmentTarget = assignmentTarget;
	}
	
	@Override
	public LanguageElement replaceVariable(String a, String b) {
		return new StackPop((AssignmentTarget) assignmentTarget.replaceVariable(a, b));
	}

	@Override
	public String toString() {
		return "pop("+assignmentTarget+")";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StackPop) {
			return ((StackPop)o).assignmentTarget.equals(assignmentTarget);
		}
		return false;
	}

	@Override
	public RankedIterator<VarStore> getIterator(ExecutionContext c, String assignToVar,
			RankedIterator<VarStore> parent) throws RPLException {
		return new RankedIterator<VarStore>() {

			private Object poppedValue;
			
			private void setPoppedValue(Object value) {
				this.poppedValue = value;
			}
			
			private Object getPoppedValue() {
				return poppedValue;
			}
			
			@Override
			public boolean next() throws RPLException {
				return parent.next();
			}

			@Override
			public VarStore getItem() throws RPLException {
				VarStore v = parent.getItem();
				PersistentStack<Object> newStack = assignmentTarget.convertToRHSExpression()
						.getValue(v, Type.STACK)
						.pop(this::setPoppedValue);
				Object poppedValue = getPoppedValue();
				if (poppedValue == null) {
					throw new RPLEmptyStackException();
				}
				v = assignmentTarget.assign(v, newStack);
				v = v.create(assignToVar, poppedValue);
				return v;
			}

			@Override
			public int getRank() {
				return parent.getRank();
			}
			
		};
	}

	@Override
	public AbstractExpression doRankExpressionTransformation(VarStore v, int rank) throws RPLException {
		return new StackPop((AssignmentTarget) assignmentTarget.doRankExpressionTransformation(v, rank));
	}

	@Override
	public AbstractExpression replaceEmbeddedFunctionCall(AbstractFunctionCall fc, String var) {
		if (fc == this) {
			return new Variable(var);
		} else {
			return new StackPop((AssignmentTarget) assignmentTarget.replaceEmbeddedFunctionCall(fc, var));
		}
	}

}