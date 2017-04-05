(function() {

    'use strict';

    angular.module('fnConfig', []);

    angular.module('fnConfig').run(function($rootScope,  $location) {
        if ($location.search().apiUrl) {
            $rootScope.apiUrl = $location.search().apiUrl;
        } else {
            $rootScope.apiUrl = "/v1"
        }
    });
})();
