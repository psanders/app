loadJS("bluemix:conversation.js");

var cCredentials = {
	username: "46d1f4cc-0556-47bd-b439-dfe165f09afa",
	password: "sJM0hSldxyD4",
	workspace: "17e25b4b-dfb9-4d99-8342-a07ff5f44b58"
};

// Returns the bluemix-conversation credentials and workspace
function getConversation() {
	return $conversation
		.login(cCredentials.username, cCredentials.password)
		.workspace(cCredentials.workspace);
}

function processIntent(processAction, txt, customerProvider) {
	var c = getConversation();
	c.input(txt)
		.then(function(result) {
		processAction(result, customerProvider);
	});
}
