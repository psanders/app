(function() {

    'use strict';

    var app = angular.module('fnConfig', []);

    app.run(['$rootScope',  '$location', function($rootScope,  $location) {
        $rootScope.ready = true;

        if ($location.search().apiUrl) {
            $rootScope.apiUrl = $location.search().apiUrl;
        } else {
            $rootScope.apiUrl = "/v1"
        }
    }]);
})();
