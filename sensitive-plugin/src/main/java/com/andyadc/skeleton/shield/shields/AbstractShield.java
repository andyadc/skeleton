package com.andyadc.skeleton.shield.shields;

import com.andyadc.skeleton.shield.Shield;

public abstract class AbstractShield implements Shield {

    @Override
    public String sensitive(Object fieldValue, String[] addition) {
        if (fieldValue == null) {
            return handleNull();
        }
        if (fieldValue.toString().isEmpty()) {
            return handleEmpty();
        }
        return doSensitive(fieldValue, addition);
    }

    @Override
    public boolean isClean() {
        return false;
    }

    protected String handleNull() {
        return null;
    }

    protected String handleEmpty() {
        return "";
    }

    protected abstract String doSensitive(Object fieldValue, String[] addition);

}
