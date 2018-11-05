package com.henglong.cloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;

public class Result {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(Result.class);

    public static BigDecimal result(String s) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Object result = engine.eval(s);
        return new BigDecimal(""+result);
    }

}
