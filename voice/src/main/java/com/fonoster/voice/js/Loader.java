/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.voice.js;

import com.fonoster.annotations.Since;
import com.fonoster.exception.ApiException;
import com.fonoster.model.App;
import com.fonoster.model.Script;
import com.fonoster.voice.conversation.Conversation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

@Since("1.0")
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

    if (script.equals("fn:core.js")) { // For internal use only
      BufferedReader loader =
          new BufferedReader(
              new InputStreamReader(
                  getClass().getClassLoader().getResource("core.js").openStream()));
      engine.eval(loader, scope);
      return;
    }

    if (script.equals("fn:loader.js")) { // For internal use only
      BufferedReader loader =
          new BufferedReader(
              new InputStreamReader(
                  getClass().getClassLoader().getResource("loader.js").openStream()));
      engine.eval(loader, scope);
      return;
    }

    if (script.equals("bluemix:conversation.js")) {
      scope.put("$conversation", new Conversation());
      return;
    }
  }

  public void load(App app, String scriptName) throws IOException, ScriptException, ApiException {
    scope.put("o", getScript(app, scriptName));
    engine.eval("load({name: o.name, script: o.source})", scope);
  }

  private Script getScript(App app, String scriptName) throws ApiException {
    Iterator<Script> scripts = app.getScripts().iterator();
    while (scripts.hasNext()) {
      Script script = scripts.next();
      if (script.getName().equals(scriptName)) {
        return script;
      }
    }
    throw new ApiException("App -> " + app.getId() + " does not have a script " + app.getName());
  }
}
