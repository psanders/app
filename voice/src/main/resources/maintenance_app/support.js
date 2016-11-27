loadJS("fn:http.js");

function getConfidenceThreshold() {
	return 0.45;
}

function getCustomerByPhone(phone) {
    var customers;
    var customer;
    $http.get('https://raw.githubusercontent.com/fonoster/callflow-examples/master/customers.json')
    .then(function(result) {
        customers = JSON.parse(result.body);
    });

    customers.forEach(function(c) {
        if (phone === c.phone) {
            customer = c;
            return;
        }
    });

    return customer;
}

function getNumber(text) {
	var result = "";
	var n = text.split(" ");
	n.forEach(function(t) {
		result = result + convertToNumber(t);
	});
	return result;
}

function convertToNumber(s) {
	switch(s) {
		case "one": return 1;
		case "two": return 2;
		case "three": return 3;
		case "four": return 4;
		case "five": return 5;
		case "six": return 6;
		case "seven": return 7;
		case "eight": return 8;
		case "nine": return 9;
		case "zero": return 0;
		default: return "";
	}
}
