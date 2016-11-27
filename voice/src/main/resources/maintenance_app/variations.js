// Add as many variations possible for questions and commands
// to obtain a more natural iteration.
var variations = {
	"welcome": [
		{"value": "Welcome to the maintenance line"}
	],
	"off_track": [
		{"value": "I'm not sure I understood. Please repeat"},
		{"value": "Let's try again"}
	],
	"whats_your_pin": [
		{"value": "Say your pin number?"},
		{"value": "Say your four digit pin?"}
	],
	"how_may_i_help": [
		{"value": "How may I help you?"},
		{"value": "What can I do for you?"}
	],
	"another_request": [
		{"value": "Anything else I can help you with?"},
		{"value": "Anything else?"}
	],
	"issue_example": [
		{"value": "I need more details. For example, say, my toilet is broken!"},
		{"value": "I need more details. For example, you can say, the alarm needs batteries"},
		{"value": "I need more details. For example, you can say, the air conditioner is not working"}
	],
	"excited": [
		{"value": "Great!"},
		{"value": "Perfect!"},
		{"value": "Alright!"}
	],
	"wait":[
		{"value": "Please wait!"},
		{"value": " One moment, please!"},
		{"value": "Just a moment, please!"}
	]
};

function getTopicVariations(topic) {
	return variations[topic];
}
