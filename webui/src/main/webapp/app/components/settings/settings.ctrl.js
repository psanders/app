(function() {
    'use strict';

    var app = angular.module('fnSettings', ['ngResource']);

    app.config(['$stateProvider', config])
       .controller('SettingsCtrl', SettingsCtrl);

    SettingsCtrl.$inject = [];

    function SettingsCtrl() {
        // Nothing goes here
    }

    function config($stateProvider) {
        $stateProvider.state('settings', {
            url: '/settings',
            templateUrl: 'app/components/settings/settings.tpl.html',
            controller: 'SettingsCtrl'
        });
    }
})();