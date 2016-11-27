loadJS("variations.js");

function humanize(q) {
	var v = getTopicVariations(q);
	var n = Object.keys(v).length;
	return v[randomize(n)].value;
}

// Returns a number 0 to n-1
function randomize(n) {
	return Math.floor((Math.random() * n));
}