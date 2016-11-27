(function() {
    'use strict';

    angular.module('fnSettings', ['ngResource']);

    angular.module('fnSettings').config(['$stateProvider', function($stateProvider) {
      $stateProvider.state('settings', {
        url: '/settings',
        templateUrl: 'app/components/settings/settings.tpl.html',
        controller: 'SettingsCtrl'
      });
    }]);

    angular.module('fnSettings').controller('SettingsCtrl', ['$scope', function($scope) {
        // Nothing yet
    }]);
})();