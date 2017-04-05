// Authentication
import _ from './components/auth/auth.service.js'
import _ from './components/auth/auth.directive.js'
import _ from './components/auth/auth.ctrl.js'
import _ from './components/auth/interceptor.js'
import _ from './components/auth/login/login.directive.js'
import _ from './components/auth/forgot/forgot.directive.js'
import _ from './components/auth/signup/signup.directive.js'

// Shared
import _ from './config.js'
import _ from './shared/common-directives.js'
import _ from './shared/common-filters.js'
import _ from './shared/sidenav/sidenav.service.js'
import _ from './shared/sidenav/sidenav.directive.js'
import _ from './shared/sidenav/sidenav.ctrl.js'
import _ from './shared/topnav/topnav.service.js'
import _ from './shared/topnav/topnav.directive.js'
import _ from './shared/topnav/topnav.ctrl.js'
import _ from './shared/tour.ctrl.js'
import _ from './shared/countries.js'
import _ from './shared/timezones.js'

// Users stuff
import _ from './components/users/users.service.js'
import _ from './components/users/users.directive.js'
import _ from './components/users/users.ctrl.js'
// Applications
import _ from './components/apps/apps.service.js'
import _ from './components/apps/apps.ctrl.js'
// Billing
import _ from './components/billing/billing.service.js'
import _ from './components/billing/billing.directive.js'
import _ from './components/billing/billing.ctrl.js'
// Calls
import _ from './components/calls/calls.service.js'
import _ from './components/calls/calls.ctrl.js'
// Dashboard
import _ from './components/dashboard/dashboard.service.js'
import _ from './components/dashboard/dashboard.ctrl.js'
// Phone/Sip Numbers
import _ from './components/numbers/numbers.service.js'
import _ from './components/numbers/numbers.ctrl.js'
// Recordings
import _ from './components/recordings/recordings.service.js'
import _ from './components/recordings/recordings.ctrl.js'
// Settings
import _ from './components/settings/settings.ctrl.js'

(function() {

    'use strict';

    angular.module('fonoster',
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
        'fnApps',
        'fnRecordings',
        'fnCalls'
        ]);

    angular.module('fonoster').config(['$stateProvider', '$urlRouterProvider', '$mdThemingProvider',
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
