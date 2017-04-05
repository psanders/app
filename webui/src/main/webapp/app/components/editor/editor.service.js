(function() {

    'use strict';

    var app = angular.module('fnEditor', ['ngResource']);

    app.service('ConfigService', ['$window', function($window) {
        var self = this;

        self.setTheme = function(theme) {
            $window.localStorage.theme = theme;
        };

        self.getTheme = function() {
            if (!$window.localStorage.theme) return "base16-light";
            return $window.localStorage.theme;
        };
    }]);

})();
