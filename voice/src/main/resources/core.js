// Ensures compatibility between Nashorn and Rhino
if (typeof importClass != "function") {
    load("nashorn:mozilla_compat.js")
}

// XXX: Must test this with Nashorn
// XXX: Ensure this is all we need to restrict
java = undefined
javax = undefined
sdk = undefined
org = undefined
Packages = undefined

// Default synthesizer and  voice
var SYNTH_56579084eaa1f291d1c99900 = "default";
var ASRENG_56579084eaa1f291d1c99900 = "default";
var VOICE_56579084eaa1f291d1c99900 = 'allison';

function voice(voice) {
    if (voice === undefined) {
        return VOICE_56579084eaa1f291d1c99900;
    }
    VOICE_56579084eaa1f291d1c99900 = voice;
}

function synth(engine, voice) {
    if (engine === undefined) {
        return SYNTH_56579084eaa1f291d1c99900;
    }

    SYNTH_56579084eaa1f291d1c99900 = engine;

    if (voice !== undefined) {
        VOICE_56579084eaa1f291d1c99900 = voice;
    }
}

function asr(engine) {
    if (engine === undefined) {
        return ASRENG_56579084eaa1f291d1c99900;
    }

    ASRENG_56579084eaa1f291d1c99900 = engine;
}

function answer() {
    $response.answer();
}

function hangup(auto) {
    // TODO: Post exception is auto is not number
    if (auto) {
        $response.setAutoHangup(auto);
    } else {
        $response.hangup();
    }
}

/**
 *
 * Param file - Is a file that has been previously uploaded or is available by default.
 */
function play(file, config) {
    var timeout = 1000;

    if (file === undefined) {
        throw 'You must indicate a file.';
    }

    if (config !== undefined && config.timeout !== undefined) {
        if (timeout < 0) throw config.timeout +' is not an acceptable timeout value. For no timeout use zero. Timeout must be equal or greater than zero';
        timeout = config.timeout * 1000;
    }

    return $response.getOption(file, '1234567890#*', timeout);
}

/**
 * Param text - Will be convert into a file and put in a cache for future use.
 * This method behavior is similar than play.
 * config {
 *      voice: 'default',
 *      timeout: 1
 * }
 */
function say(text, config) {

    if (!text) throw 'You must provide a text.';

    var voice = VOICE_56579084eaa1f291d1c99900;

    if(config !== undefined) {
        if (config.voice !== undefined) voice = config.voice;
    }

    // This returns the route to the generated audio
    var fn = TTS_56579084eaa1f291d1c99900
        .getTTSEngine(SYNTH_56579084eaa1f291d1c99900)
            .generate(voice, text);

    return play(fn, config);
}

/**
 * Gather is to be use in combination with verbs Play, Say, Wait. The are pipeline together
 * to create this powerful verb.
 *
 * config {
 *      timeout: 4,         // Default
 *      finishOnKey: #,     // Default
 *      numDigits: 0        // Wait for the user to press digit.
 * }
 *
 * Please take in consideration that either 'timeout' or 'numDigits' must be greater than Zero.
 */
function gather(f, config) {
    // a timeout of 0 means no timeout
    // Less than one second will have no effect
    var timeout = 4 * 1000;
    var finishOnKey = '#';
    // Not limit
    var numDigits = 0;
    var digits = "";

    if (config !== undefined) {
        if (config.finishOnKey !== undefined && config.finishOnKey.length != 1) throw 'finishOnKey must a single char. Default value is #. Acceptable values are digits from 0-9,#,*';
        // Less than one second will have no effect on the timeout
        if (config.timeout     !== undefined && config.timeout < 0) throw config.timeout +' is not an acceptable timeout value. For no timeout use zero. Timeout must be equal or greater than zero';
        if (config.numDigits   !== undefined && config.numDigits < 0) throw config.numDigits +' is not an acceptable numDigits value. Must be equal or greater than zero';
        if (config.timeout     !== undefined
            && config.numDigits!== undefined
            && config.numDigits == 0
            && config.timeout == 0) {
                throw config.numDigits +' Either numDigits or timeout must be greater than zero';
            }
        // Overwrites timeout
        if (config.timeout !== undefined) {
            if(config.timeout == 0 ) timeout = 0;
            // Anywhere on from 0.1 to 0.9 the timeout should be near to zero(1 milly is close enough)
            if(config.timeout > 0  && config.timeout <= 1) timeout = 1;
            // Rest on second to compensate the silence = 1 in getOption
            if(config.timeout > 1) timeout = (config.timeout - 1) * 1000;
        }
        if (config.finishOnKey !== undefined) finishOnKey = config.finishOnKey;
        if (config.numDigits !== undefined) numDigits = config.numDigits;
    }

    var c;

    if (f !== undefined && ("1234567890#*").indexOf(f) > -1) {
       digits = f;
    }

    for (;;) {
        // Break it if
        // 1. User enters finishOnKey(ie.: #)
        // 2. The length of digits is equal or greater than numDigits but numDigits is different than 0
        // 3. Character c is null given that timeout != 0
        if (c == finishOnKey
            || digits.length >= numDigits
            // Timeout != 0 means no timeout
            || (("1234567890#*").indexOf(c) > -1 && timeout != 0)) {
            break;
        }

        // Ideally we should play an audio that has near zero seconds
        c = $response.getOption('silence/1', '1234567890#*', timeout);

        if (("1234567890#*").indexOf(c) > -1) {
            digits = digits.concat(c);
            continue;
        }
        break;
    }

    return digits;
}

// TODO: Add parameter canInterrupt
function wait(time) {
    var i = 1;

    if (time && time < 0) throw 'reps must an integer equal or greater than zero.';
    if (time) i = time;

    while(i > 0) {
        play('silence/1');
        i--;
    }
}

// Secondary verbs
function redirect(appId) {
    return 'nop';
}

/**
 * Record creates a file with the sound send by receiving device
 * config {
 *      timeout: 4,         // Default
 *      finishOnKey: #,     // You may add as many scape characters as you wan't
 *      beep: true,
 *      offset: 0,
 *      maxDuration: 3600
 * }
 *
 * Keep in mind that either 'timeout' or 'numDigits' must be greater than Zero.
 */
function record(config) {
    var result = {};
    var format = "wav";
    var offset = 0;

    var beep = true;
    var timeout = 5;
    var maxDuration = 3600 * 1000;
    var finishOnKey = "1234567890*#";

    var recording = API_56579084eaa1f291d1c99900.createRecording(CDR_56579084eaa1f291d1c99900);

    if (config !== undefined) {
        // Less than one second will have no effect on the timeout
        if (config.timeout     !== undefined && config.timeout < 0) throw config.timeout +' is not an acceptable timeout value. For no timeout use zero. Timeout must be equal or greater than zero';
        if (config.maxDuration !== undefined && config.maxDuration < 1) throw config.maxDuration +' is not an acceptable maxDuration value. Must be integer greater than 1. Default is 3600 (1 hour)';
        if (config.beep !== undefined && typeof(config.beep) !== "boolean") throw config.beep +' is not an acceptable value. Must be a boolean';

        // Overwrite values
        if (config.maxDuration !== undefined) maxDuration = config.maxDuration * 1000;
        if (config.timeout !== undefined) timeout = config.timeout;
        if (config.beep !== undefined) beep = config.beep;
        if (config.finishOnKey !== undefined) finishOnKey = config.finishOnKey;
    }

    result.keyPressed = $response.recordFile(RECORD_56579084eaa1f291d1c99900 + "/" + recording.getId(),
        format,
        finishOnKey,
        maxDuration,
        offset,
        beep,
        timeout);

    result.recordingUri = recording.getUri();
    result.filename = recording.getId();

    return result;
}

// Draft functionality
function recognize(func, c) {
    var result = {};
    var config = {
        timeout: 200,
        maxDuration: 5000,
        finishOnKey: "1234567890*#",
        asr: 'default',
        model: 'en-US_NarrowbandModel',   // Warning: This may not work for all APIs
        background: 'loading'             // Not yet used
    };

    if (c !== undefined) {
        // Less than one second will have no effect on the timeout
        if (c.timeout !== undefined && c.timeout < 0) throw c.timeout +' is not an acceptable timeout value. For no timeout use zero. Timeout must be equal or greater than zero';
        if (c.maxDuration !== undefined && c.maxDuration < 1) throw c.maxDuration +' is not an acceptable maxDuration value. Must be integer greater than 1. Default is 3600 (1 hour)';

        // Overwrite values
        if (c.timeout !== undefined) config.timeout = c.timeout;
        if (c.maxDuration !== undefined) config.maxDuration = c.maxDuration;
        if (c.finishOnKey !== undefined) config.finishOnKey = c.finishOnKey;
        if (c.asr !== undefined) config.asr = c.asr;
        if (c.model !== undefined) config.model = c.model;
        if (c.background !== undefined) config.background = c.background;
    }

    var asr = ASR_56579084eaa1f291d1c99900.getASREngine(config.asr);
    var filename = RECORD_56579084eaa1f291d1c99900 + "/" + $request.getCallId() + "_" + Date.now(); // Fix me

    result.keyPressed  = $response.recordFile(filename,
        "wav",
        config.finishOnKey,
        config.maxDuration,
        0,
        false,
        config.timeout);

    // Ugly hack :(
    say(config.background);

    asr.setModel(config.model);
    asr.transcribe(filename + ".wav", func);
}

// Store variable with the cdr
function stash(name, value) {
    CDR_56579084eaa1f291d1c99900.addVar(name, value);
}