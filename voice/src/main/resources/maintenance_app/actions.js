loadJS("humanize.js");

// Process action base on the users intent
function processAction(result, customerProvider) {
	var customer = customerProvider.getCustomer();
	//print(JSON.stringify(customer));

	switch(result.getIntent()) {
		case "report_issue":
			var e = result.getEntities();
			if (e.size() === 0) {
				say(humanize("issue_example"));
				break;
			}

			e.forEach(function(entity){
				say(entity.getValue());
				if (customer.issues === undefined) customer.issues = [];
				customer.issues.push({value: entity.getValue()});
			});

			customerProvider.setCustomer(customer);
			say(humanize("another_request"));
			break;
		case "list_pending_issues":
			if (customer.issues === undefined || customer.issues.length === 0) {
				say("There is not pending issues in your account. ");
				say(humanize("another_request"));
				break;
			}
			say("You reported issues with the following items: ");
			customer.issues.forEach(function(issue) {
				print("issue -> " + issue.value);
				say(issue.value);
			});
			say(humanize("another_request"));
			break;
		case "out_of_scope":
			say("Sorry! We don't work with those types of issues. ");
			say(humanize("another_request"));
			break;
        case "all_done":
			say("A report was sent to the management team, with the following issues: ");

			customer.issues.forEach(function(issue) {
				say(issue.value);
			});

			say("A technitian is on his way! Ensure there is someone waiting. ");
            say("I hope I was helpful! Have a great day!");
            hangup();
            break;
		case "out_of_context":
			say("Please, stay in context");
			break;
		default:
			say("Sorry. I'm still learning. Try something else.");
	}
}
