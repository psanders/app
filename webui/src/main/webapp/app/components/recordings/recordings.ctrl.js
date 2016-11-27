(function() {
    'use strict';

    angular.module('fnRecordings');

    angular.module('fnRecordings').config(['$stateProvider', function($stateProvider) {
      $stateProvider.state('recordings', {
        url: '/recordings',
        templateUrl: 'app/components/recordings/recordings.tpl.html',
        controller: 'RecordingsCtrl'
      });
    }]);

    angular.module('fnRecordings').controller('RecordingsCtrl', ['$scope','$window', '$q', '$timeout',  'Recordings', 'ngAudio', '$mdDialog',
        function($scope, $window, $q, $timeout, Recordings, ngAudio, $mdDialog) {

        $scope.startDate = new Date();
        $scope.endDate = new Date();

        $scope.query = {
            order: '-modified',
            limit: 10,
            page: 1
        };

        $scope.load = function(r) {
            r.audio = ngAudio.load(r.uri + '?result=mp3');
        }

        $scope.play = function(r) {
            r.audio.play();
        }

        // Available?
        $scope.isAva = function(r) {
            if(!r || !r.audio || !r.audio.audio) return false;
            if(r.audio.audio.duration == 0) return false;
            return true;
        }

        // Using stop() or restart() is causing:
        // Uncaught (in promise) DOMException: The element has no supported sources.
        $scope.stop = function(r) {
            r.audio.pause();
            r.audio.currentTime = 0;
        }

        // Internal method
        function showAlert() {
          alert = $mdDialog.alert({
            title: 'Attention',
            content: 'This is an example of how easy dialogs can be!',
            ok: 'Close'
          });
          $mdDialog
            .show( alert )
            .finally(function() {
              alert = undefined;
            });
        }

        $scope.updateView = function() {
            if(!$scope.startDate || !$scope.endDate) return;

            var rRequest = {start: moment($scope.startDate).format("YYYY-MM-DD"), end: moment($scope.endDate).format("YYYY-MM-DD")};

            Recordings.get(rRequest).$promise
            .then(function(result) {
                $scope.recordings = result;
            })
            .catch(function(error) {
                console.log(JSON.stringify(error));
            });
            $scope.filter = false;
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

        $scope.filterBy = function(ev) {
            $mdDialog.show({
                controller: DialogController,
                templateUrl: '/app/components/recordings/filter_dialog.tpl.html',
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose:true
            })
            .then(function(request) {
                regenerate(request);
            }, function() {
                // Do nothing
            });
        };

        function DialogController($scope, $mdDialog) {
            $scope.hide = function() {
                $mdDialog.hide();
            };
            $scope.cancel = function() {
                $mdDialog.cancel();
            };
            $scope.regenerate = function(r) {
                $mdDialog.hide(r);
            };
        }

        $scope.updateView();
    }]);

})();