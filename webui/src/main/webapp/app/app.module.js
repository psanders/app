(function() {

    'use strict';

    angular.module('fonoster',
        ['ui.router',
        'ngMaterial',
        'md.data.table',
        'nvd3',
        'ngAudio',
        'fnDirectives',
        'fnFilters',
        'fnUsers',
        'fnBilling',
        'fnSettings',
        'fnAuth',
        'fnDashboard',
        'fnNumbers',
        'fnApps',
        'fnEditor',
        'fnRecordings',
        'fnCalls',
        'fnSettings',
        'fnSidenav',
        'fnTopnav',
        'fnTour',
        'fnConfig'
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
