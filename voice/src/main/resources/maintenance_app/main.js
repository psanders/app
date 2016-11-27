/*jshint loopfunc: true */
loadJS("support.js");
loadJS("intent.js");
loadJS("humanize.js");
loadJS("actions.js");
loadJS("auth.js");

voice("lisa");

var self = this;
var customer;

self.setCustomer = function(c) {
	customer = c;
};

self.getCustomer = function() {
	return customer;
};

function conversation() {
	while(true) {
		var txt;
		var confidence;
		recognize(function(result) {
			txt = result.transcript.trim();
			confidence = result.confidence;
			print("[transcript: '" + txt + "', confidence: " + result.confidence + "]");
		}, {background: humanize("wait")});

		if (txt !== "" && confidence < getConfidenceThreshold()) {
			say(humanize("off_track"));
			continue;
		}
		processIntent(processAction, txt, self);
	}
}

// Human/machine conversation
self.setCustomer(getCustomerByPhone($request.getTo()));
say(humanize("welcome"));
login(self);
say(humanize("how_may_i_help"));
conversation();
