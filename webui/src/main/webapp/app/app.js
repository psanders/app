// Authentication
require('./components/auth/auth.service.js');
require('./components/auth/auth.directive.js');
require('./components/auth/auth.ctrl.js');
require('./components/auth/interceptor.js');
require('./components/auth/login/login.directive.js');
require('./components/auth/forgot/forgot.directive.js');
require('./components/auth/signup/signup.directive.js');
// Shared
require('./config.js');
require('./shared/common-directives.js');
require('./shared/common-filters.js');
require('./shared/sidenav/sidenav.service.js');
require('./shared/sidenav/sidenav.directive.js');
require('./shared/sidenav/sidenav.ctrl.js');
require('./shared/topnav/topnav.service.js');
require('./shared/topnav/topnav.directive.js');
require('./shared/topnav/topnav.ctrl.js');
require('./shared/tour.ctrl.js');
require('./shared/countries.js');
require('./shared/timezones.js');
// Users stuff
require('./components/users/users.service.js');
require('./components/users/users.directive.js');
require('./components/users/users.ctrl.js');
// Applications
require('./components/apps/apps.service.js');
require('./components/apps/apps.ctrl.js');
// Billing
require('./components/billing/billing.service.js');
require('./components/billing/billing.directive.js');
require('./components/billing/billing.ctrl.js');
// Calls
require('./components/calls/calls.service.js');
require('./components/calls/calls.ctrl.js');
// Dashboard
require('./components/dashboard/dashboard.service.js');
require('./components/dashboard/dashboard.ctrl.js');
// Phone/Sip Numbers
require('./components/numbers/numbers.service.js');
require('./components/numbers/numbers.ctrl.js');
// Recordings
require('./components/recordings/recordings.service.js');
require('./components/recordings/recordings.ctrl.js');
// Settings
require('./components/settings/settings.ctrl.js');
// Editor
require('./components/editor/editor.service.js');
require('./components/editor/editor.directive.js');
require('./components/editor/topbar.directive.js');
require('./components/editor/editor.ctrl.js');

(function() {

    'use strict';

    var app = angular.module('fonoster',
        ['ui.router',
        'ngMaterial',
        'md.data.table',
        'nvd3',
        'ngAudio',
        'fnAuth',
        'fnUsers',
        'fnTour',
        'fnDirectives',
        'fnFilters',
        'fnConfig',
        'fnSidenav',
        'fnTopnav',
        'fnBilling',
        'fnSettings',
        'fnDashboard',
        'fnNumbers',
        'fnRecordings',
        'fnCalls',
        'fnEditor',
        'fnApps'
        ]);

    app.config(['$stateProvider', '$urlRouterProvider', '$mdThemingProvider',
        function($stateProvider, $urlRouterProvider, $mdThemingProvider) {

        $urlRouterProvider.otherwise('/dashboard');

        $mdThemingProvider.theme('default')
           .primaryPalette('green', {
                 'default': '400',
                 'hue-1': '100',
                 'hue-2': '600',
                 'hue-3': 'A100'
               })
               .accentPalette('grey', {
                 'default': '200'
               });

        $mdThemingProvider.theme('alt')
            .primaryPalette('blue')
            .accentPalette('green');
    }]);

})();
