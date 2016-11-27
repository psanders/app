(function() {
    'use strict';

    angular.module('fnEditor')
        .config(config)
        .filter('keyboardShortcut', keyboardShortcut)
        .controller('EditorCtrl', EditorCtrl);

    EditorCtrl.$inject = ['$scope',
        '$window',
        '$mdMedia',
        '$mdSidenav',
        '$mdDialog',
        '$interval',
        '$mdToast',
        '$location',
        '$timeout',
        '$document',
        'Apps',
        'Calls',
        'Numbers',
        'Users',
        'ConfigService'];

    function EditorCtrl($scope, $window, $mdMedia, $mdSidenav, $mdDialog, $interval, $mdToast, $location, $timeout,
        $document,  Apps, Calls, Numbers , Users, ConfigService) {

        var self = this;
        var cm;
        var window = angular.element($window);
        var oldAppName;
        var removedScript;
        var editors = [];
        var starterScript = "// Go to https://goo.gl/3Q7ktJ for more examples\nsay(\"Hello World!\", {voice: 'allison'});\n";
        var oPhone;

        init();

        // TODO: Make this more the same for the demo or old user
        self.call = function(event) {
            // Temporal feature
            if (Users.getUser().email == "john@doe.com" && getPgndPhone() === undefined) {
                self.chgPgndPhone(function(){
                    call(getPgndPhone());
                }, event);
            } else if (Users.getUser().email == "john@doe.com" && getPgndPhone() !== undefined) {
                call(getPgndPhone());
            } else {
                call();
            }
        };

        self.select = function(script) {
            addEditor(script);
            self.currentScript = script;

            if (script.source == "" || script.source === undefined) script.source = starterScript;

            var e = getEditorById(getCurrentScript().name);
            e.setValue(getCurrentScript().source);

            resizeEditor(e);
        }

        self.save = function(force) {
            if (force === undefined) {
                force = false;
            }

            var newScript = false;

            if (!self.app) {
                self.app = {name: 'Untitled App'};
                newScript = true;
            }

            if (getCurrentScript() !== undefined && !force) {
                var e = getEditorById(getCurrentScript().name);
                e.save();
                if (getCurrentScript().source == e.getValue() && !force) return;
                self.currentScript.source = e.getValue();
            }

            self.loading = true;

            Apps.save(self.app).$promise
            .then(function(result){
                if (newScript) {
                    self.app = result;
                    self.currentScript = getScriptByName("main.js");
                }

                // This is split second is enought to allow the GUI to render
                $timeout(function() {
                    self.select(self.currentScript);
                }, 1);

                $timeout(function() {
                    self.loading = false;
                }, 1500);
            })
            .catch(function(error) {
                simpleToast('Unable to save app.');
            });
        }

        self.add = function(event) {
            // Appending dialog to document.body to cover sidenav in docs app
            var confirm = $mdDialog.prompt()
              .title('What would you name your script?')
              .textContent('Lower case with .js extension is recommended')
              .placeholder('script.js')
              .ariaLabel('script.js')
              .targetEvent(event)
              .ok('Create')
              .cancel('Cancel');

            $mdDialog.show(confirm).then(function(result) {
              if (result === undefined) {
                simpleToast("Script name can't be empty", 5000);
                return;
              }

              var unique = true;

              self.app.scripts.forEach(function(script) {
                if (script.name == result) {
                    simpleToast("Script name must be unique", 5000);
                    unique = false;
                }
              })

              if (!unique) return;

              var script = {name: result, source: "// Type your code here"};

              self.app.scripts.push(script);
              self.save(true);
              self.select(script);
            }, function() {
                //
            });
        };

        self.renameApp = function(event) {
            // Appending dialog to document.body to cover sidenav in docs app
            var confirm = $mdDialog.prompt()
              .title('What would you name your app?')
              .placeholder('Untitled App')
              .ariaLabel('Untitled App')
              .targetEvent(event)
              .ok('rename')
              .cancel('Cancel');

            $mdDialog.show(confirm).then(function(result) {
              if (result === undefined) {
                simpleToast("App name can't be empty", 5000);
                return;
              }
              self.app.name = result;
              self.save(true);
            }, function() {
                //
            });
        };

        self.renameScript = function(event, script) {
            // Appending dialog to document.body to cover sidenav in docs app
            var confirm = $mdDialog.prompt()
              .title('What would you name your script?')
              .textContent('Lower case with .js extension is recommended')
              .placeholder('script.js')
              .ariaLabel('script.js')
              .targetEvent(event)
              .ok('rename')
              .cancel('Cancel');

            $mdDialog.show(confirm).then(function(result) {
              if (result === undefined) {
                simpleToast("Script name can't be empty", 5000);
                return;
              }

              editors.forEach(function(editor) {
                if (editor.name == script.name) editor.name = result;
              })

              script.name = result;

              self.save(true);
            }, function() {
                //
            });
        };

        self.removeScript = function(index) {
            removedScript = self.app.scripts[index];
            self.app.scripts.splice(index, 1);

            Apps.save(self.app).$promise
            .then(function(result){
                removeEditor(removedScript.name);
                self.select(getScriptByName("main.js"));
                actionToast("Removed script!", 5000);
            })
            .catch(function(error) {
                simpleToast('Unable to save app.');
            });
        }

        self.verticalNavOpen = function() {
            return $mdSidenav('verticalNav').isLockedOpen();
        }

        // Horrible implementation :s
        self.close = function() {
            setTimeout(function() {
                $window.close();
            },2);
        }

        self.toggleVerticalNavigation = function() {
            $mdSidenav('verticalNav').toggle()
            .then(function () {
                //console.log("close LEFT is done");
            });
        };

        self.changeTheme = function(theme) {
            ConfigService.setTheme(theme);
            getEditorById(getCurrentScript().name).setOption('theme', theme);
        }

        self.getLoading = function() {
            return mode;
        }

        self.undo = function() {
            getEditorById(getCurrentScript().name).undo();
        }

        self.redo = function() {
            getEditorById(getCurrentScript().name).redo();
        }

        self.replace = function() {
            getEditorById(getCurrentScript().name).execCommand("replace");
        }

        self.chgPgndPhone = function(func, event) {
            // Appending dialog to document.body to cover sidenav in docs app
            var confirm = $mdDialog.prompt()
              .title("What's your phone number?")
              .textContent('Ensure format is E.164')
              .placeholder('+18092333232')
              .ariaLabel('+18092333232')
              .targetEvent(event)
              .ok('Save')
              .cancel('Cancel');
            $mdDialog.show(confirm).then(function(phone) {
                if (!isValidPhone(phone)) {
                    simpleToast("Not a valid phone. It must be in E.164 format", 5000);
                } else {
                    setPgndPhone(phone);
                    if (func) func();
                }
            }, function() {
                //
            });
        }

        function init() {
            self.loading = false;
            angular.element(document).ready(function() {
                setApp();
                preferredNumber();
                return;
            });

            window.bind('resize', function() {
                resizeEditor();
            });
        }

        function call(phone) {
            var from = oPhone.number;
            var to = Users.getUser().phone;
            var record = true;

            if (phone !== undefined) to = phone;

            if (!to) {
                alertMe("Before calling, you must add a phone to your profile", "Got it");
                return;
            }

            var callRequest = {appId: self.app.id, from: from, to: to, record: record, billable: false};
            console.debug("call.request = " + JSON.stringify(callRequest));

            var monitorId;

            simpleToast("Calling number " + to);

            self.save(true);

            Calls.save(callRequest).$promise
            .then(function(cdr) {
               $scope.connected = true;
               $scope.callId = cdr.callId;
               // Show toast here
               // Check every 5 seconds for change on the call status
               var monitorId;
               monitorId = $interval(function() {
                   if(!$scope.callId) {
                        $interval.cancel(monitorId);
                        return;
                   }

                   // Getting status
                   Calls.get({callId: $scope.callId}).$promise
                   .then(function(cRecord) {

                       if (cRecord.status == 'FAILED'    ||
                           cRecord.status == 'COMPLETED' ||
                           (cRecord.logs && cRecord.logs.length > 0)) {

                           if (cRecord.logs && cRecord.logs.length > 0) {
                                $mdToast.show($mdToast.simple()
                                    .position('top right')
                                    .content("We've found an error in your script")
                                    .action("Bug")
                                    .parent($document[0].querySelector('.CodeMirror'))
                                    .hideDelay(9000))
                                .then(function() {
                                    cRecord.logs.forEach(function(log) {
                                        alertMe(log.level.concat(": ").concat(log.message));
                                    });
                                });
                           }

                           $interval.cancel(monitorId);
                           return;
                       }

                       if ($scope.oldStatus != cRecord.status) {
                           simpleToast(getStatus(cRecord.status), 7000);
                           $scope.oldStatus = cRecord.status;
                       }
                   }).catch(function(error) {
                        $interval.cancel(monitorId);
                        simpleToast("Unable to reach server. Try again later. Code #0001");
                   });
               }, 5000);
            }).catch(function(error) {
                if (error.data.message === undefined) {
                    simpleToast("Unable to reach gateway. Try again later. Code #0002");
                } else {
                    simpleToast(error.data.message);
                }
            });
        }

        function setApp() {
            console.log('appId ' + $location.search().appId);
            var appId = $location.search().appId;
            if (appId) {
                Apps.get({appId: appId}).$promise
                .then(function(result){
                    self.app = result;
                    self.currentScript = getScriptByName("main.js");
                    setTimeout(function() {
                       self.select(self.currentScript);
                    },1000);
                })
                .catch(function(error){
                    simpleToast('Unable to open app');
                    $location.url("apps");
                });
            } else {
                self.save();
            };
        }

        function addEditor(script) {
            if (getEditorById(script.name) != undefined) return;

            var e = CodeMirror.fromTextArea(document.getElementById(script.name), {
                mode: 'text/javascript',
                theme: ConfigService.getTheme(),

                indentUnit: 4,
                smartIndent: true,
                tabSize: 4,
                indentWithTabs: true,
                electricChar: true,

                extraKeys: {"Ctrl-Space": "autocomplete"},
                lineWrapping: true,
                lineNumbers: true,

                gutters: [
                    "CodeMirror-lint-markers",
                    "CodeMirror-linenumbers",
                    "CodeMirror-foldgutter"
                    ],
                foldGutter: true,
                matchBrackets: true,
                autoCloseBrackets: true,
                styleActiveLine: true,

                fullscreen: true,
                lint: true,
            });

            e.setOption('theme', ConfigService.getTheme());
            e.setValue('');
            setTimeout(function() {
                e.refresh();
            },1);

            editors.push({id:script.name, editor: e});
        }

        function removeEditor(id) {
            var cnt = 0;
            editors.forEach(function(e) {
                if (e.id == id) {
                    return;
                }
                cnt = cnt + 1;
            });
            editors.splice(cnt, 1);
        }

        function getEditorById(id) {
            var editor;
            editors.forEach(function(e) {
                if (e.id == id) {
                    editor = e.editor;
                }
            });
            return editor;
        }

        function getScriptByName(name) {
            var script;
            self.app.scripts.forEach(function(s) {
                if(s.name == name)
                    script = s;
            });
            return script;
        }

        function getCurrentScript() {
            return self.currentScript;
        }

        function preferredNumber() {
            Numbers.getPreferredResource().get().$promise
            .then(function(result) {
                oPhone = result;
            }).catch(function(error) {
                if (error.data === undefined) {
                    simpleToast("Unable to find test number. First available will be used.");
                } else {
                    simpleToast(error.data.message);
                }
            });
        }

        function resizeEditor(editor) {
            if (editor === undefined) {
                return;
            }

            setTimeout(function() {
                var browserHeight = document.documentElement.clientHeight;

                if ($mdMedia('xs'))  {
                    editor.getWrapperElement().style.height = (browserHeight - 56) + 'px';
                } else if ($mdMedia('sm')) {
                    editor.getWrapperElement().style.height = (browserHeight - 87) + 'px';
                } else {
                    editor.getWrapperElement().style.height = (browserHeight - 104) + 'px';
                }

                editor.refresh();
            },1);
        };

        function getStatus(status) {
           switch (status) {
                case "QUEUED": return "Queue.";
                case "FAILED": return "Failed. Try again!";
                case "COMPLETED": return "Completed";
                case "RINGING": return "Ringing.";
                case "IN_PROGRESS": return "In-progress";
           }
        }

        function setPgndPhone(phone) {
            $window.localStorage.pgndPhone = phone;
        }

        function getPgndPhone() {
            return $window.localStorage.pgndPhone;
        }

        function simpleToast(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('bottom right')
                .content(msg)
                .hideDelay(hideDelay))
            .then(function() {
                    // Nothing to do
            });
        }

        function actionToast(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('bottom right')
                .content(msg)
                .action("Undo")
                .highlightAction(true)
                .hideDelay(hideDelay))
            .then(function(response) {
                if ( response == 'ok' ) {
                    self.app.scripts.push(removedScript);
                    self.save(true);
                    self.select(removedScript);
                }
            });
        }

        function alertMe(msg) {
            // Appending dialog to document.body to cover sidenav in docs app
            // Modal dialogs should fully cover application
            // to prevent interaction outside of dialog
            $mdDialog.show(
              $mdDialog.alert()
                .parent(angular.element(document.querySelector('#popupContainer')))
                .clickOutsideToClose(true)
                .title("Attention!")
                .content(msg)
                .ariaLabel('Attention')
                .ok('Got it!')
            );
        };

        function isValidPhone(phone) {
            var pattern = new RegExp("^\\+(?:[0-9] ?){6,14}[0-9]$");
            return pattern.test(phone);
        }

        function getTheme() {
            return theme;
        }
    }

    function keyboardShortcut($window) {
        return function(str) {
            if (!str) return;
            var keys = str.split('-');
            var isOSX = /Mac OS X/.test($window.navigator.userAgent);
            var seperator = (!isOSX || keys.length > 2) ? '+' : '';
            var abbreviations = {
                M: isOSX ? '⌘' : 'Ctrl',
                A: isOSX ? 'Option' : 'Alt',
                S: 'Shift'
            };
            return keys.map(function(key, index) {
                var last = index == keys.length - 1;
                return last ? key : abbreviations[key];
            }).join(seperator);
        };
    }

    function config($stateProvider) {
        $stateProvider.state('/editor', {
            url: '/editor',
            templateUrl: 'app/components/editor/editor.tpl.html',
            controller: 'EditorCtrl'
        });
    }
})();