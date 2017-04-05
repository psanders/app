import * as moment from 'moment-timezone';

(function() {
    'use strict';

    console.log('DEBUG 0001');

    angular.module('fnApps')
        .config(['$stateProvider', config])
        .controller('AppsCtrl', AppsCtrl);

    AppsCtrl.$inject = ['$location',
        '$window',
        '$q',
        '$timeout',
        '$mdToast',
        '$document',
        'CredentialsService',
        'Apps',
        'Users'
        ];

    function AppsCtrl($location, $window, $q, $timeout, $mdToast, $document, CredentialsService, Apps, Users) {
        // Sets proper timezone for 'asCalendar' filter
        moment.tz.setDefault(Users.getUser().timezone)

        var self = this;

        self.topDirections = ['left', 'up'];
        self.bottomDirections = ['down', 'right'];
        self.isOpen = false;
        self.availableModes = ['md-fling', 'md-scale'];
        self.selectedMode = 'md-fling';

        self.selected = [];
        self.removed = [];

        self.query = {order: '-modified', limit: 10, page: 1};

        // Create or open and app
        self.open = function(appId) {
            if (appId) {
                console.debug('open appId ->' + appId);
                $window.location.href = 'app.html#app/editor?appId=' + appId;
            } else {
                console.debug('Create new app');
                 $window.location.href = 'app.html#/editor';
            }
        }

        getApps();

        self.remove = function() {
            self.selected.forEach(function(app) {
                Apps.remove({appId: app.id}).$promise
                .then(function(data) {
                    findAndRemove(self.apps.apps, 'id', app.id);
                }).catch(function(error) {
                    console.log(JSON.stringify(error));
                });
            });
            self.removed = self.selected;
            self.selected = [];
            removeToast("Removed " + self.removed.length + " app/s", 7000);
        }

        self.onPageChange = function(page, limit) {
            var deferred = $q.defer();

            $timeout(function () {
              deferred.resolve();
            }, 2000);

            return deferred.promise;
        };

        self.onOrderChange = function(order) {
            var deferred = $q.defer();

            $timeout(function () {
              deferred.resolve();
            }, 2000);

            return deferred.promise;
        };

        function getApps() {
            Apps.get().$promise
            .then(function(result) {
                self.apps = result;
            })
            .catch(function(error) {
                console.log(JSON.stringify(error));
            });
        }

        function findAndRemove(array, property, value) {
          array.forEach(function(result, index) {
            if(result[property] === value) {
              //Remove from array
              array.splice(index, 1);
            }
          });
        }

        function removeToast(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                    .position('bottom right')
                    .content(msg)
                    .action("Undo")
                    .highlightAction(true)
                    .hideDelay(hideDelay))
            .then(function(response) {
                if ( response == 'ok' ) {
                    self.removed.forEach(function(app) {
                        // This selected apps are in their original status
                        Apps.save(app).$promise
                        .then(function(data) {
                            getApps();
                        }).catch(function(error) {
                            console.error(error);
                        });
                    });
                    $location.path("/apps");
                }
            });
        }
    };

    function config($stateProvider) {
        $stateProvider.state('apps', {
            url: '/apps',
            templateUrl: 'app/components/apps/apps.tpl.html',
            controller: 'AppsCtrl'
        });
    }

})();