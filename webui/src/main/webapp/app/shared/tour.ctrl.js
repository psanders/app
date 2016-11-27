(function() {
    'use strict';

    angular.module('fnTour', []);

    angular.module('fnTour').controller('TourCtrl',
        ['$window', '$state', '$location', function($window, $state, $location) {
        var self = this;
        var Tour = window.Tour.default;
        var mainTour = {
            canExit: true,
            showPrevious: false,
            nextText: 'Next',
            steps: [{
                target: '#console',
                content: "Welcome! Let's make your first call, shall we?",
                placement: ['right'],
                after: function() {
                  return new Promise(function(resolve, reject){
                    $state.go('apps');
                    resolve()
                })}
            },{
                target: '#apps-item',
                content: 'Here is where all your applications live.',
                placement: ['right', 'bottom']
            },{
                target: '#apps-widget',
                content: 'An application is a collection of scripts that controls the flow of a conversation.',
                placement: ['left', 'bottom']
            },{
                 target: '#add-app',
                 content: "Click to create your first application.",
                 placement: ['left', 'bottom']
            }]
        };

        var editorTour = {
            canExit: true,
            nextText: 'Next',
            steps: [{
                target: '#code-area',
                content: "Let's go over the basics, shall we?",
                placement: [ 'right', 'top', 'bottom', 'left' ]
            }, {
                target: '#code-area',
                content: 'Use this area to write your scripts using FonosterJS API.',
                placement: [ 'right', 'top', 'bottom', 'left' ]
            }, {
                target: '#script-list',
                content: "The 'main.js' script is the entry-point to your application.",
                placement: [ 'right', 'bottom']
            },{
                target: '#add-script',
                content: 'You may add more scripts to make your application more readable and maintainable.'
            },{
                target: '#app-id',
                content: 'This is the ID of your application. You will need this when using the RESTful API.'
            },{
                target: '#call-button',
                content: 'Click to test!',
            }],
        };

        init();

        function startTour(tourObj, name) {
            setTimeout(
                function() {
                    Tour.start(tourObj)
                    .then(function() {
                        completeTour(name, 'completed');
                    })
                    .catch(function () {
                        completeTour(name, 'interrupted');
                    });
            }, 1500);
        }

        function completeTour(name, status) {
            if (name == 'main-tour') {
                $window.localStorage.main_tour = status;
            } else if (name == 'editor-tour')  {
                $window.localStorage.editor_tour = status;
            }
        }

        function isTourDone(name) {
            if (name == 'main-tour' && $window.localStorage.main_tour !==undefined) {
                return true;
            } else if (name == 'editor-tour' && $window.localStorage.editor_tour !==undefined)  {
                return true;
            }
            return false;
        }

        function init() {
            var p = $location.path();

            if((p == '/dashboard'  ||
                p == '/numbers'    ||
                p == '/apps'       ||
                p == '/recordings' ||
                p == '/logs'       ||
                p == '/settings')
                && !isTourDone('main-tour')) {
                $state.go('dashboard');
                startTour(mainTour, 'main-tour');
            }

            if($location.path() == '/editor' && !isTourDone('editor-tour')) {
                startTour(editorTour, 'editor-tour');
            }
        }
    }]);
})();