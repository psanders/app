package com.fonoster.voice.js;

import com.fonoster.exception.SequenceException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleHttp {

    private static final HttpClient client = new HttpClient();
    private static HttpMethod method;   
    private static List<NameValuePair> qString;

    public SimpleHttp() {
    }

    public SimpleHttp get(String url) {
        method = new GetMethod(url);
        qString = new ArrayList<>();
        return this;
    }

    public SimpleHttp post(String url) throws SequenceException {
        method = new PostMethod(url);
        qString = new ArrayList<>();
        header("content", "application/json");
        return this;
    }

    public SimpleHttp put(String url) throws SequenceException {
        method = new PutMethod(url);
        qString = new ArrayList<>();
        header("content", "application/json");
        return this;
    }

    public SimpleHttp delete(String url) throws SequenceException {
        method = new DeleteMethod(url);
        qString = new ArrayList<>();
        header("content", "application/json");
        return this;
    }

    public SimpleHttp head(String url) throws SequenceException {
        method = new HeadMethod(url);
        qString = new ArrayList<>();
        header("content", "application/json");
        return this;
    }

    public SimpleHttp header(String k, String v) throws SequenceException {
        if (method == null) {
            throw new SequenceException();
        }
        method.addRequestHeader(k, v);
        return this;
    }

    public SimpleHttp timeout(int timeout) throws SequenceException {
        if (method == null) {
            throw new SequenceException();
        }
        client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
        return this;
    }

    public SimpleHttp field(String k, String v) throws SequenceException {
        if (method == null) {
            throw new SequenceException();
        }
        if (method instanceof PostMethod) {
            ((PostMethod) method).addParameter(k, v);
        }
        return this;
    }

    public SimpleHttp queryString(String k, String v) throws SequenceException {
        if (method == null) {
            throw new SequenceException();
        }
        NameValuePair q = new NameValuePair();
        q.setName(k);
        q.setValue(v);
        qString.add(q);
        return this;
    }

    public SimpleHttp basicAuth(String name, String pass) throws SequenceException {
        if (method == null) {
            throw new SequenceException();
        }

        Credentials credentials = new UsernamePasswordCredentials(name, pass);
        client.getState().setCredentials(AuthScope.ANY, credentials);
        client.getParams().setAuthenticationPreemptive(true);

        return this;
    }

    public void then(JSFunc func) throws SequenceException, IOException {
        if (method == null) {
            throw new SequenceException("You must call a method before this using 'then'");
        }

        NameValuePair[] qs = qString.toArray(new NameValuePair[qString.size()]);

        method.setQueryString(qs);
        client.executeMethod(method);

        Result r = new Result(method.getStatusCode(), method.getResponseBodyAsString());

        func.r(r);

        method.releaseConnection();
        method = null;
    }

    public interface JSFunc {
        void r(Result r);
    }

    public static class Result {

        private final int code;
        private final String body;

        public Result(final int code, final String body) {
            this.code = code;
            this.body = body;
        }

        public int getCode() {
            return code;
        }

        public String getBody() {
            return body;
        }
    }
}
