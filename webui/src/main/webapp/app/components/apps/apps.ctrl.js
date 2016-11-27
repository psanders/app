(function() {
    'use strict';

    angular.module('fnApps');

    angular.module('fnApps').config(['$stateProvider', function($stateProvider) {
      $stateProvider.state('apps', {
        url: '/apps',
        templateUrl: 'app/components/apps/apps.tpl.html',
        controller: 'AppsCtrl'
      });
    }]);

    angular.module('fnApps').controller('AppsCtrl', ['$location', '$window', '$scope', '$q', '$timeout', '$mdToast', '$document',
        'CredentialsService', 'Apps',
        function($location, $window, $scope, $q, $timeout, $mdToast, $document, CredentialsService, Apps) {

        $scope.topDirections = ['left', 'up'];
        $scope.bottomDirections = ['down', 'right'];
        $scope.isOpen = false;
        $scope.availableModes = ['md-fling', 'md-scale'];
        $scope.selectedMode = 'md-fling';

        $scope.selected = [];
        $scope.removed = [];

        $scope.query = {
            order: '-modified',
            limit: 10,
            page: 1
        };

        // Create or open and app
        $scope.open = function(appId) {
            if (appId) {
                console.debug('open appId ->' + appId);
                $window.location.href = 'app.html#app/editor?appId=' + appId;
            } else {
                console.debug('Create new app');
                 $window.location.href = 'app.html#/editor';
            }
        }

        var getApps = function() {
            Apps.get().$promise
            .then(function(result) {
                $scope.apps = result;
            })
            .catch(function(error) {
                console.log(JSON.stringify(error));
            });
        }

        getApps();

        $scope.remove = function() {
            $scope.selected.forEach(function(app) {
                Apps.remove({appId: app.id}).$promise
                .then(function(data) {
                    findAndRemove($scope.apps.apps, 'id', app.id);
                }).catch(function(error) {
                    console.log(JSON.stringify(error));
                });
            });
            $scope.removed = $scope.selected;
            $scope.selected = [];
            removeToast("Removed " + $scope.removed.length + " app/s", 7000);
        }

        $scope.onPageChange = function(page, limit) {
            var deferred = $q.defer();

            $timeout(function () {
              deferred.resolve();
            }, 2000);

            return deferred.promise;
        };

        $scope.onOrderChange = function(order) {
            var deferred = $q.defer();

            $timeout(function () {
              deferred.resolve();
            }, 2000);

            return deferred.promise;
        };

        var findAndRemove = function(array, property, value) {
          array.forEach(function(result, index) {
            if(result[property] === value) {
              //Remove from array
              array.splice(index, 1);
            }
          });
        }

        var removeToast = function(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                    .position('bottom right')
                    .content(msg)
                    .action("Undo")
                    .highlightAction(true)
                    .hideDelay(hideDelay))
            .then(function(response) {
                if ( response == 'ok' ) {
                    $scope.removed.forEach(function(app) {
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
    }]);

})();