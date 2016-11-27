(function() {

    'use strict';

    angular.module('fnEditor', ['ngResource']);

    angular.module('fnEditor').service('ConfigService', function($window) {

        this.setTheme = function(theme) {
            $window.localStorage.theme = theme;
        };

        this.getTheme = function() {
            if (!$window.localStorage.theme) return "base16-light";
            return $window.localStorage.theme;
        };
    });

})();
