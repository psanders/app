(function() {
    'use strict';

    angular.module('fnSIPNet').config(['$stateProvider', config]);
    angular.module('fnSIPNet').controller('SIPNetAgentsCtrl', SIPNetAgentsCtrl);
    angular.module('fnSIPNet').controller('SIPNetDomainsCtrl', SIPNetDomainsCtrl);

    SIPNetAgentsCtrl.$inject = ['$location', '$q', '$timeout', '$mdToast', 'Agents', 'Domains'];
    SIPNetDomainsCtrl.$inject = ['$scope', '$location', '$q', '$timeout', '$mdToast', 'Domains', 'Numbers'];

    function SIPNetAgentsCtrl($location, $q, $timeout, $mdToast, Agents, Domains) {
        var self = this;
        self.view = 'LIST'
        self.selected = [];
        self.removed = [];

        self.query = {
            order: '-modified',
            limit: 10,
            page: 1
        };

        self.changeView = function(view) {
            self.view = view;
            init();
        }

        self.openEditView = function(agent) {
            self.agent = agent;
            self.selectedDomain = '';
            self.agent.name = agent.metadata.name;
            self.view = 'EDIT';
        }

        self.isView = function(view) {
            return view == self.view;
        }

        function init() {
            self.agent = {};
            self.selectedDomain = '';

            Agents.get().$promise
            .then(function(result) {
                self.agents = result;
            })
            .catch(function(error) {
                console.log(JSON.stringify(error));
            });

            Domains.get().$promise
            .then(function(result) {
                self.domains = result;
            })
            .catch(function(error) {
                console.log(JSON.stringify(error));
            });
        }

        self.save = function(a) {
            self.agent = {};
            self.agent.id = a.id;
            self.agent.metadata = {name: a.name};
            self.agent.spec = {};
            self.agent.spec.credentials = {username: a.username, secret: a.secret};
            self.agent.spec.domains = [];

            // This will only apply for agent update
            if(!!self.selectedDomain) {
                self.agent.spec.domains[0] = self.selectedDomain.spec.context.domainUri;
            }

            Agents.save(self.agent).$promise
            .then(function(result) {
                toastMe('Done!');
                self.changeView('LIST');
            })
            .catch(function(error) {
                toastMe(error.data.message);
            });
        }

        self.remove = function() {
            self.selected.forEach(function(agent) {
                Agents.remove({agentId: agent.id}).$promise
                .then(function(data) {
                    findAndRemove(self.agents.agents, 'id', agent.id);
                }).catch(function(error) {
                    console.log(JSON.stringify(error));
                });
            });
            self.removed = self.selected;
            self.selected = [];
            removeToast("Removed " + self.removed.length + " agent/s", 7000);
        }

        function findAndRemove(array, property, value) {
          array.forEach(function(result, index) {
            if(result[property] === value) {
              //Remove from array
              array.splice(index, 1);
            }
          });
        }

        self.onOrderChange = function(order) {
            var deferred = $q.defer();

            $timeout(function () {
              deferred.resolve();
            }, 2000);

            return deferred.promise;
        }

        self.onPageChange = function(page, limit) {
            var deferred = $q.defer();

            $timeout(function () {
              deferred.resolve();
            }, 2000);

            return deferred.promise;
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
                    self.removed.forEach(function(agent) {
                        // This selected agents are in their original status
                        Agents.save(agent).$promise
                        .then(function(data) {
                            init();
                        }).catch(function(error) {
                            console.error(error);
                        });
                    });
                    $location.path("/sipnet_agents");
                }
            });
        }

        function toastMe(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('bottom right')
                .content(msg)
                .hideDelay(hideDelay))
            .then(function() {
                // Nothing to do
            });
        }

        init();
    }

    function SIPNetDomainsCtrl($scope, $location, $q, $timeout, $mdToast, Domains, Numbers) {
        var self = this;
        self.view = 'LIST'
        self.selected = [];
        self.removed = [];

        self.query = {
            order: '-modified',
            limit: 10,
            page: 1
        };

        self.changeView = function(view) {
            self.view = view;
            init();
        }

        self.openEditView = function(domain) {
            self.domain = domain;
            self.domain.name = domain.metadata.name;
            self.selectedDIDNumber = '';
            self.didNumbers.didnumbers.forEach(function(did) {
                if (domain.spec.context.egressPolicy.didRef ==
                    did.metadata.ref) {
                    self.selectedDIDNumber = did;
                }
            });
            self.view = 'EDIT';
        }

        self.save = function(d) {
            self.domain = {};
            if (self.isView('EDIT')) {
                d.uri = d.spec.context.domainUri;
                self.domain.id = d.spec.context.domainUri;
            }
            self.domain.metadata = { name: d.name };
            self.domain.spec = {
                context: {
                    domainUri: d.uri,
                    egressPolicy: {
                        rule: ".*",
                        didRef: self.selectedDIDNumber.metadata.ref
                    }
                }
            };

            Domains.save(self.domain).$promise
            .then(function(result) {
                toastMe('Done!');
                self.changeView('LIST');
            })
            .catch(function(error) {
                toastMe(error.data.message);
            });
        }

        self.isView = function(view) {
            return view == self.view;
        }

        function init() {
            self.domain = {};
            self.selectedDIDNumber = '';

            Domains.get().$promise
            .then(function(result) {
                self.domains = result;
            })
            .catch(function(error) {
                console.error(JSON.stringify(error));
            });

            Numbers.getResource().get().$promise
            .then(function(result) {
                self.didNumbers = result;
            })
            .catch(function(error) {
                console.error(JSON.stringify(error));
            });
        }

        self.remove = function() {
            $scope.hasError = false;
            self.selected.forEach(function(domain) {
                Domains.remove({domainUri: domain.spec.context.domainUri}).$promise
                .then(function(data) {
                    findAndRemove(self.domains.domains, 'id', domain.spec.context.domainUri);
                }).catch(function(error) {
                    toastMe(error.data.message, 7000);
                    $scope.hasError = true;
                });
            });

            // This prevents the remove message to be call if a prior error exist...
            $timeout(function () {
                if (!$scope.hasError) {
                    self.removed = self.selected;
                    self.selected = [];
                    removeToast("Removed " + self.removed.length + " domain/s", 7000);
                }
            }, 1200);
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
                    self.removed.forEach(function(domain) {
                        // This selected domain are in their original status
                        Domains.save(domain).$promise
                        .then(function(data) {
                            init();
                        }).catch(function(error) {
                            console.error(error);
                        });
                    });
                    $location.path("/sipnet_domains");
                }
            });
        }

        function toastMe(msg, hideDelay) {
            if (!hideDelay) hideDelay = 2000;
            $mdToast.show($mdToast.simple()
                .position('bottom right')
                .content(msg)
                .hideDelay(hideDelay))
            .then(function() {
                // Nothing to do
            });
        }

        init();
    }

    function config($stateProvider) {
        $stateProvider.state('sipnet_agents', {
            url: '/sipnet_agents',
            templateUrl: 'app/components/sipnet/agents.tpl.html',
            controller: 'SIPNetAgentsCtrl'
        });

        $stateProvider.state('sipnet_domains', {
            url: '/sipnet_domains',
            templateUrl: 'app/components/sipnet/domains.tpl.html',
            controller: 'SIPNetDomainsCtrl'
        });
    }
})();