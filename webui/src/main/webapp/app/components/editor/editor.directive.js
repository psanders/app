(function() {

    'use strict';

    angular.module('fnEditor').directive('editor', function() {
        return {
            restrict: 'E',
            templateUrl: 'app/components/editor/editor.tpl.html'
        };
    });

})();
