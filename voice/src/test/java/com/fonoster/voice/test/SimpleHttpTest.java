package com.fonoster.voice.test;

import com.fonoster.exception.SequenceException;
import com.fonoster.voice.js.SimpleHttp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;

import javax.script.*;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class SimpleHttpTest {

    private ClientAndProxy proxy;
    private ClientAndServer mockServer;

    @Before
    public void startProxy() {
        mockServer = startClientAndServer(1080);
        proxy = startClientAndProxy(1090);
    }

    @After
    public void stopProxy() {
        proxy.stop();
        mockServer.stop();
    }

    @Test
    public void testGet() throws Exception {
        final int[] code = new int[1];
        final String[] body = new String[1];

        new MockServerClient("localhost", 1080)
                .when(
                        request()
                        .withMethod("GET")
                        .withPath("/get")
                )
                .respond(
                        response()
                        .withStatusCode(200)
                        .withBody("{v:'ok'}")
                );

        new SimpleHttp().get("http://localhost:1080/get").then(r -> {
            code[0] = r.getCode();
            body[0] = r.getBody();
        });

        assertEquals(code[0], 200);
        assertEquals(body[0], "{v:'ok'}");
    }

    @Test
    public void testPost() throws Exception {
        final int[] code = new int[1];
        final String[] body = new String[1];

        new MockServerClient("localhost", 1080)
                .when(
                        request()
                        .withMethod("POST")
                        .withPath("/post")
                )
                .respond(
                        response()
                        .withStatusCode(200)
                        .withBody("{v:'ok'}")
                );

        new SimpleHttp().post("http://localhost:1080/post").then(r -> {
            code[0] = r.getCode();
            body[0] = r.getBody();
        });

        assertEquals(code[0], 200);
        assertEquals(body[0], "{v:'ok'}");
    }

    @Test
    public void testHeader() throws Exception {
        final int[] code = new int[1];
        final String[] body = new String[1];

        new MockServerClient("localhost", 1080)
                .when(
                        request()
                        .withMethod("POST")
                        .withPath("/withHeader")
                        .withHeader("content", "application/xml")
                )
                .respond(
                        response()
                        .withStatusCode(200)
                        .withBody("<message>OK</message>")
                );

        new SimpleHttp().post("http://localhost:1080/withHeader")
                .then(r -> {
                    code[0] = r.getCode();
                    body[0] = r.getBody();
                });

        assertNotSame(code[0], 200);
        assertNotSame(body[0], "<message>OK</message>");

        new SimpleHttp().post("http://localhost:1080/withHeader")
                .header("content", "application/xml")
                .then(r -> {
                    code[0] = r.getCode();
                    body[0] = r.getBody();
                });

        assertEquals(code[0], 200);
        assertEquals(body[0], "<message>OK</message>");
    }

    @Test(expected = org.apache.commons.httpclient.ConnectTimeoutException.class)
    public void testTimeout() throws Exception {

        new SimpleHttp().post("http://www.google.com:81")
                .timeout(5000)
                .then(r -> {
                });
    }

    @Test
    public void testField() throws Exception {
        final int[] code = new int[1];
        final String[] body = new String[1];

        new MockServerClient("localhost", 1080)
                .when(
                        request()
                        .withMethod("POST")
                        .withPath("/withField")
                        .withBody("name=quijote")
                )
                .respond(
                        response()
                        .withStatusCode(200)
                        .withBody("{v:'ok'}")
                );

        new SimpleHttp().post("http://localhost:1080/withField")
                .field("name", "quijote")
                .then(r -> {
                    code[0] = r.getCode();
                    body[0] = r.getBody();
                });

        assertEquals(code[0], 200);
        assertEquals(body[0], "{v:'ok'}");
    }

    @Test
    public void testQueryString() throws Exception {
        final int[] code = new int[1];
        final String[] body = new String[1];

        new MockServerClient("localhost", 1080)
                .when(
                        request()
                        .withMethod("GET")
                        .withPath("/getQueryString")
                        .withQueryStringParameter("name")
                )
                .respond(
                        response()
                        .withStatusCode(200)
                        .withBody("{v:'ok'}")
                );

        new SimpleHttp().get("http://localhost:1080/getQueryString")
                .queryString("name", "quijote")
                .then(r -> {
                    code[0] = r.getCode();
                    body[0] = r.getBody();
                });

        assertEquals(code[0], 200);
        assertEquals(body[0], "{v:'ok'}");
    }

    @Test(expected = SequenceException.class)
    public void testSecuenceException() throws SequenceException, IOException {

        new SimpleHttp().timeout(5000).post("http://random.page")
                .then(r -> {
                });
    }

    @Test
    public void testBasicAuth() throws SequenceException, IOException {
        final int[] code = new int[1];
        final String[] body = new String[1];

        new MockServerClient("localhost", 1080)
                .when(
                        request()
                        .withMethod("POST")
                        .withPath("/login")
                        .withHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
                )
                .respond(
                        response()
                        .withStatusCode(200)
                        .withBody("{name: logged}")
                );

        new SimpleHttp().post("http://localhost:1080/login")
                .basicAuth("user", "password")
                .then(r -> {
                    code[0] = r.getCode();
                    body[0] = r.getBody();
                });

        assertEquals(code[0], 200);
        assertEquals(body[0], "{name: logged}");
    }

    @Test
    public void testWithJS() throws ScriptException {

        new MockServerClient("localhost", 1080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/hello.txt")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("hi!")
                );

        // Init Js Engine
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        ScriptContext newContext = new SimpleScriptContext();
        newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        engineScope.put("http", new SimpleHttp());

        engine.eval("var hi; http.get('http://localhost:1080/hello.txt').then(function(r) {hi = r.body;});", engineScope);
        String hi = engineScope.get("hi").toString();
        assertEquals("hi!", hi);
    }
}
