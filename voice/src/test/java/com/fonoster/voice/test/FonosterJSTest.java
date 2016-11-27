package com.fonoster.voice.test;

import com.fonoster.config.CommonsConfig;
import com.fonoster.model.Account;
import com.fonoster.model.CallDetailRecord;
import com.fonoster.model.Recording;
import com.fonoster.voice.config.VoiceConfig;
import com.fonoster.voice.js.Loader;
import com.fonoster.voice.tts.BluemixTTS;
import com.fonoster.voice.tts.TTSFactory;
import org.astivetoolkit.astivlet.AstivletResponse;
import org.bson.types.ObjectId;
import org.junit.Test;

import javax.script.*;
import java.io.File;
import java.io.FileReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FonosterJSTest {

    private static final VoiceConfig config = VoiceConfig.getInstance();
    private static final CommonsConfig commonsConfig = CommonsConfig.getInstance();

    @Test
    public void testPlay() throws Exception {
        // Mockito stubbing
        AstivletResponse response = mock(AstivletResponse.class);
        when(response.getOption("test1", "1234567890#*", 1000)).thenReturn("1".charAt(0));
        when(response.getOption("test2", "1234567890#*", 1000)).thenReturn('\0');

        // Init Js Engine
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        ScriptContext newContext = new SimpleScriptContext();
        newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        engineScope.put("$response", response);

        // Loading built-in libraries
        engineScope.put("LOADER_56579084eaa1f291d1c99900", new Loader (engine, engineScope));
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:loader.js')", engineScope);
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:core.js')", engineScope);

        // User press 1
        engine.eval("var key = play('test1')", engineScope);
        char key = (engineScope.get("key").toString()).charAt(0);
        assertSame('1', key);

        // User press nothing
        engine.eval("key = play('test2')", engineScope);
        key = (engineScope.get("key").toString()).charAt(0);
        assertSame('\0', key);
    }

    @Test
    public void testSay() throws Exception {
        // Mockito stubbing
        AstivletResponse response = mock(AstivletResponse.class);
        TTSFactory ttsFactory = mock(TTSFactory.class);
        BluemixTTS tts = mock(BluemixTTS.class);

        when(response.getOption("test1", "1234567890#*", 1000)).thenReturn("1".charAt(0));
        when(response.getOption("test2", "1234567890#*", 1000)).thenReturn('\0');
        when(ttsFactory.getTTSEngine("default")).thenReturn(tts);
        when(ttsFactory.getTTSEngine("default").generate("allison", "pressed 1")).thenReturn("test1");
        when(ttsFactory.getTTSEngine("default").generate("allison", "pressed nothing")).thenReturn("test2");

        // Init Js Engine
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        ScriptContext newContext = new SimpleScriptContext();
        newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        engineScope.put("$response", response);
        engineScope.put("TTS_56579084eaa1f291d1c99900", ttsFactory);

        // Loading built-in libraries
        engineScope.put("LOADER_56579084eaa1f291d1c99900", new Loader (engine, engineScope));
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:loader.js')", engineScope);
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:core.js')", engineScope);

        // User press 1
        engine.eval("var key = say('pressed 1', {commons: 'allison'})", engineScope);

        char key = (engineScope.get("key").toString()).charAt(0);
        assertSame('1', key);

        // User press nothing
        engine.eval("key = say('pressed nothing', {commons: 'allison', timeout: 1})", engineScope);

        key = (engineScope.get("key").toString()).charAt(0);
        assertSame('\0', key);
    }

    @Test
    public void testGatherPressOneDigit() throws Exception {
        // Mockito stubbing
        AstivletResponse response = mock(AstivletResponse.class);

        // Init Js Engine
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        ScriptContext newContext = new SimpleScriptContext();
        newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        engineScope.put("response", response);

        // Loading built-in libraries
        engineScope.put("LOADER_56579084eaa1f291d1c99900", new Loader (engine, engineScope));
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:loader.js')", engineScope);
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:core.js')", engineScope);

        // User press 1
        engine.eval("var key = gather('1', {numDigits: 1})", engineScope);

        char key = (engineScope.get("key").toString()).charAt(0);
        assertSame('1', key);
    }

    @Test
    public void testGatherPressNothing() throws Exception {
        // Mockito stubbing
        AstivletResponse response = mock(AstivletResponse.class);
        when(response.getOption("silence/10", "1234567890#*", 5000))
            .thenReturn('\0');

        // Init Js Engine
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        ScriptContext newContext = new SimpleScriptContext();
        newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        engineScope.put("$response", response);

        // Loading built-in libraries
        engineScope.put("LOADER_56579084eaa1f291d1c99900", new Loader (engine, engineScope));
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:loader.js')", engineScope);
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:core.js')", engineScope);

        // User press nothing
        engine.eval("var digits = gather('\0', {numDigits: 1})", engineScope);

        String digits = (engineScope.get("digits").toString());

        assertEquals("", digits);
    }

    @Test
    public void testGatherPressFewDigits() throws Exception {
        // Mockito stubbing
        AstivletResponse response = mock(AstivletResponse.class);
        when(response.getOption("silence/1", "1234567890#*", 0))
            .thenReturn('1')
            .thenReturn('2');

        // Init Js Engine
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        ScriptContext newContext = new SimpleScriptContext();
        newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        engineScope.put("$response", response);

        engine.eval(new FileReader(new File(config.getCoreLib ().toURI())), engineScope);

        // User press 1. Method say or play return key=3
        engine.eval("digits = gather('3', {numDigits: 3, timeout: 0})", engineScope);

        String digits = (engineScope.get("digits").toString());

        assertEquals("312", digits);
    }

    @Test
    public void testRecord() throws Exception {
        // Mockito stubbing
        AstivletResponse response = mock(AstivletResponse.class);
        CallDetailRecord cdr = mock(CallDetailRecord.class);
        RecordingsAPIMock recordingAPI = mock(RecordingsAPIMock.class);

        Account account = mock(Account.class);
        Recording record = mock(Recording.class);
        record.setId(new ObjectId());
        record.setAccount(account);

        when(account.getId()).thenReturn(new ObjectId());
        when(record.getAccount()).thenReturn(account);

        when(recordingAPI.createRecording(cdr)).thenReturn(record);
        when(response.recordFile(commonsConfig.getRecordingsPath().concat("/") + record.getId(),
            "wav", "1234567890*#", 3600 * 1000, 0, true, 5)).thenReturn("1".charAt(0));

        // Init Js Engine
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        ScriptContext newContext = new SimpleScriptContext();
        newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        engineScope.put("API_56579084eaa1f291d1c99900", recordingAPI);
        engineScope.put("RECORD_56579084eaa1f291d1c99900", commonsConfig.getRecordingsPath());
        engineScope.put("CDR_56579084eaa1f291d1c99900", cdr);
        engineScope.put("$response", response);

        // Loading built-in libraries
        engineScope.put("LOADER_56579084eaa1f291d1c99900", new Loader (engine, engineScope));
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:loader.js')", engineScope);
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:core.js')", engineScope);

        // User press 1
        engine.eval("var key = record().keyPressed", engineScope);
        char key = (engineScope.get("key").toString()).charAt(0);
        assertSame('1', key);
    }

    @Test
    public void testLoadJS() throws Exception {
        // Init Js Engine
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");

        // Engine context and scope
        ScriptContext newContext = new SimpleScriptContext();
        newContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        // Loading built-in libraries
        engineScope.put("LOADER_56579084eaa1f291d1c99900", new Loader (engine, engineScope));
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:loader.js')", engineScope);
        engine.eval("LOADER_56579084eaa1f291d1c99900.load('fn:core.js')", engineScope);

        engine.eval("loadJS('fn:http.js')", engineScope);

        engine.eval("print($http)", engineScope);   // ReferenceError: "$http" is not defined wil be launch if it fails

        engine.eval("loadJS('bluemix:conversation.js')", engineScope);
        engine.eval("print($conversation)", engineScope);   // ReferenceError: "$http" is not defined wil be launch if it fails
    }

    class RecordingsAPIMock {
        public RecordingsAPIMock() {
        }
        public Recording createRecording(CallDetailRecord cdr) {
            throw new UnsupportedOperationException();
        }
    }
}
