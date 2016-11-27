package com.fonoster.voice.js;

import com.fonoster.exception.ApiException;
import com.fonoster.model.App;
import com.fonoster.voice.conversation.Conversation;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Loader {
    private ScriptEngine engine;
    private Bindings scope;

    public Loader(ScriptEngine engine, Bindings scope) {
        this.engine = engine;
        this.scope = scope;
    }

    public void load(String script) throws IOException, ScriptException, ApiException {
        if (script.equals("fn:http.js")) {
            scope.put("$http", new SimpleHttp());
            return;
        }

        if(script.equals("fn:core.js")) {     // For internal use only
            BufferedReader loader = new BufferedReader(
                new InputStreamReader (getClass().getClassLoader().getResource("core.js").openStream()));
            engine.eval(loader, scope);
            return;
        }

        if(script.equals("fn:loader.js")) {   // For internal use only
            BufferedReader loader = new BufferedReader(
                new InputStreamReader (getClass().getClassLoader().getResource("loader.js").openStream()));
            engine.eval(loader, scope);
            return;
        }

        if (script.equals("bluemix:conversation.js")) {
            scope.put("$conversation", new Conversation());
            return;
        }
    }

    public void load(App app, String script) throws IOException, ScriptException, ApiException {
        scope.put("o", app.getScriptByName(script));
        engine.eval("load({name: o.name, script: o.source})", scope);
    }
}
