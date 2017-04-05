import * as moment from 'moment-timezone';

(function() {
    'use strict';

    angular.module('fnRecordings')
        .config(['$stateProvider', config])
        .controller('RecordingsCtrl', RecordingsCtrl);

    RecordingsCtrl.$inject = ['$window', '$q', '$timeout',  'Recordings', 'ngAudio', '$mdDialog', 'Users'];

    function RecordingsCtrl($window, $q, $timeout, Recordings, ngAudio, $mdDialog, Users) {
        var self = this;
        self.startDate = new Date();
        self.endDate = new Date();

        // Sets proper timezone for 'asCalendar' filter
        moment.tz.setDefault(Users.getUser().timezone)

        self.query = {
            order: '-modified',
            limit: 10,
            page: 1
        };

        self.load = function(r) {
            r.audio = ngAudio.load(r.uri + '?result=mp3');
        }

        self.play = function(r) {
            r.audio.play();
        }

        // Available?
        self.isAva = function(r) {
            if(!r || !r.audio || !r.audio.audio) return false;
            if(r.audio.audio.duration == 0) return false;
            return true;
        }

        // Using stop() or restart() is causing:
        // Uncaught (in promise) DOMException: The element has no supported sources.
        self.stop = function(r) {
            r.audio.pause();
            r.audio.currentTime = 0;
        }

        self.updateView = function() {
            if(!self.startDate || !self.endDate) return;

            var rRequest = {
                start: moment(self.startDate).format("YYYY-MM-DD"),
                end: moment(self.endDate).format("YYYY-MM-DD")
            };

            Recordings.get(rRequest).$promise
            .then(function(result) {
                self.recordings = result;
            })
            .catch(function(error) {
                console.log(JSON.stringify(error));
            });
            self.filter = false;
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

        self.updateView();
    }

    function config($stateProvider) {
        $stateProvider.state('recordings', {
            url: '/recordings',
            templateUrl: 'app/components/recordings/recordings.tpl.html',
            controller: 'RecordingsCtrl'
        });
    }

})();