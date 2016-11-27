(function() {

    'use strict';

    var directives = angular.module('fnDirectives',[]);

    // Deprecated
    directives.directive('audios', function($sce) {
        return {
            restrict: 'A',
            scope: { code:'=' },
            replace: true,
            template: '<audio ng-src="{{url}}" controls></audio>',
            link: function (scope) {
               scope.$watch('code', function (newVal, oldVal) {
                  if (newVal !== undefined) {
                      // Can we improve this?
                      scope.url = $sce.trustAsResourceUrl(newVal + '?result=mp3');
                  }
               });
            }
        };
    });

    directives.directive('ngEnter', function () {
        return function (scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                if(event.which === 13) {
                    scope.$apply(function (){
                        scope.$eval(attrs.ngEnter);
                    });

                    event.preventDefault();
                }
            });
        };
    });

    directives.directive('markdown', function () {
        var converter = new showdown.Converter()
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var htmlText = converter.makeHtml(element.text());
                element.html(htmlText);
            }
        };

    });

    directives.directive("compareTo", function() {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=compareTo"
            },
            link: function(scope, element, attributes, ngModel) {

                ngModel.$validators.compareTo = function(modelValue) {
                    return modelValue == scope.otherModelValue;
                };

                scope.$watch("otherModelValue", function() {
                    ngModel.$validate();
                });
            }
        };
    });

})();
