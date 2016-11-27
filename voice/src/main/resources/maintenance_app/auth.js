/*jshint loopfunc: true */
loadJS("humanize.js");
loadJS("support.js");

function login(customerProvider) {
	var customer = customerProvider.getCustomer();
	while(true) {
		say(humanize("whats_your_pin"));

		var authenticated = false;

		recognize(function(result) {
			var pin = result.transcript.trim();
			print("[transcript: " + pin + ", confidence: " + result.confidence + "]");
			if (result.confidence >= getConfidenceThreshold() && getNumber(pin) === customer.pin) {
				say(humanize("excited"));
				authenticated = true;
			} else {
				say(humanize("off_track"));
			}
		},{background: humanize("wait")});
		if (authenticated === true) break;
	}
}
