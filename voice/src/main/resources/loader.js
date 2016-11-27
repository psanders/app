function loadJS(name) {
    // If namespace is "fn"
    if (name.startsWith("fn:") || name.startsWith("bluemix:")) {
        LOADER_56579084eaa1f291d1c99900.load(name);
    } else {
        LOADER_56579084eaa1f291d1c99900.load(APP_56579084eaa1f291d1c99900, name);
    }
}