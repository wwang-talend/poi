/*
 * Created on May 8, 2005
 *
 */
package org.apache.poi.hssf.record.formula.eval;

import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.hssf.record.formula.DividePtg;

/**
 * @author Amol S. Deshmukh &lt; amolweb at ya hoo dot com &gt;
 *  
 */
public class DivideEval extends NumericOperationEval {

    private DividePtg delegate;

    private static final ValueEvalToNumericXlator NUM_XLATOR = 
        new ValueEvalToNumericXlator((short)
                ( ValueEvalToNumericXlator.BOOL_IS_PARSED 
                | ValueEvalToNumericXlator.EVALUATED_REF_BOOL_IS_PARSED
                | ValueEvalToNumericXlator.EVALUATED_REF_STRING_IS_PARSED
                | ValueEvalToNumericXlator.REF_BOOL_IS_PARSED
                | ValueEvalToNumericXlator.STRING_IS_PARSED
                ));

    public DivideEval(Ptg ptg) {
        delegate = (DividePtg) ptg;
    }
    
    protected ValueEvalToNumericXlator getXlator() {
        return NUM_XLATOR;
    }

    public Eval evaluate(Eval[] operands, int srcRow, short srcCol) {
        Eval retval = null;
        double d0 = 0;
        double d1 = 0;
        switch (operands.length) {
        default: // will rarely happen. currently the parser itself fails.
            retval = ErrorEval.UNKNOWN_ERROR;
            break;
        case 2:
            ValueEval ve = singleOperandEvaluate(operands[0], srcRow, srcCol);
            if (ve instanceof NumericValueEval) {
                d0 = ((NumericValueEval) ve).getNumberValue();
            }
            else if (ve instanceof BlankEval) {
                // do nothing
            }
            else {
                retval = ErrorEval.VALUE_INVALID;
            }
            
            if (retval == null) { // no error yet
                ve = singleOperandEvaluate(operands[1], srcRow, srcCol);
                if (ve instanceof NumericValueEval) {
                    d1 = ((NumericValueEval) ve).getNumberValue();
                }
                else if (ve instanceof BlankEval) {
                    // do nothing
                }
                else {
                    retval = ErrorEval.VALUE_INVALID;
                }
            }
        } // end switch

        if (retval == null) {
            retval = (d1 == 0) 
                ? ErrorEval.DIV_ZERO
                : (Double.isNaN(d0) || Double.isNaN(d1)) 
                    ? (ValueEval) ErrorEval.VALUE_INVALID 
                    : new NumberEval(d0 / d1);
        }
        return retval;
    }

    public int getNumberOfOperands() {
        return delegate.getNumberOfOperands();
    }

    public int getType() {
        return delegate.getType();
    }
}
