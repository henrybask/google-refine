package com.google.gridworks.gel.ast;

import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gridworks.expr.EvalError;
import com.google.gridworks.expr.Evaluable;
import com.google.gridworks.expr.ExpressionUtils;
import com.google.gridworks.expr.HasFields;

/**
 * An abstract syntax tree node encapsulating a field accessor,
 * e.g., "cell.value" is accessing the field named "value" on the
 * variable called "cell".
 */
public class FieldAccessorExpr implements Evaluable {
    final protected Evaluable     _inner;
    final protected String        _fieldName;
    
    public FieldAccessorExpr(Evaluable inner, String fieldName) {
        _inner = inner;
        _fieldName = fieldName;
    }
    
    public Object evaluate(Properties bindings) {
        Object o = _inner.evaluate(bindings);
        if (ExpressionUtils.isError(o)) {
            return o; // bubble the error up
        } else if (o == null) {
            return new EvalError("Cannot retrieve field from null");
        } else if (o instanceof HasFields) {
            return ((HasFields) o).getField(_fieldName, bindings);
        } else if (o instanceof JSONObject) {
            try {
                return ((JSONObject) o).get(_fieldName);
            } catch (JSONException e) {
                return new EvalError("Object does not have any field, including " + _fieldName);
            }
        } else {
            return new EvalError("Object does not have any field, including " + _fieldName);
        }
    }

    @Override
    public String toString() {
        return _inner.toString() + "." + _fieldName;
    }
}
