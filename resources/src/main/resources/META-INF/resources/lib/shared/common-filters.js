(function() {

    'use strict';

    var filters = angular.module('fnFilters',[]);

    filters.filter('truncate', function () {
        return function (text, length, end, last) {
            if (isNaN(length))
                length = 10;

            if (end === undefined)
                end = "...";

            if (text === undefined)
                return;

            if (text.length <= length || text.length - end.length <= length) {
                return text;
            }   else {
                if (last) {
                    return String(text).substring(text.length - length - end.length, text.length) + end;
                }

                return String(text).substring(0, length-end.length) + end;
            }
        };
    });

    filters.filter('humanize', function () {
        return function (text) {
            text = text.toLowerCase();
            text = text.replace(new RegExp('_', 'g'), ' ');
            return text[0].toUpperCase() + text.slice(1);
        };
    });

    filters.filter('asCalendar', function () {
        return function (date) {
            if (moment().format("DDMMYYYY") == moment(date).format("DDMMYYYY")) {
                return moment(date).calendar();
            } else if (moment().format("YYYY") == moment(date).format("YYYY")) {
                return moment(date).format("DD MMM.");
            } else {
              return moment(date).format("YYYY.");
            }
        };
    });


})();
