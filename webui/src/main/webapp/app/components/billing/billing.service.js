(function() {

    'use strict';

    var apps = angular.module('fnBilling', ['ngResource']);

    apps.service('Billing', function($resource, $window, $rootScope, CredentialsService, Users) {

        this.getResource = function() {
            return $resource($rootScope.apiUrl + '/billing/:email/payment_method/:nonce?result=json', {
                email: Users.getUser().email,
                nonce: "@nonce"
            });
        }

        this.getFundsResource = function() {
            return $resource($rootScope.apiUrl + '/billing/:email/funds/:amount?result=json', {
                email: Users.getUser().email,
                amount: "@amount"
            });
        }

        this.getAutopayResource = function() {
            return $resource($rootScope.apiUrl + '/billing/:email/autopay/:autopay?result=json', {
                email: Users.getUser().email,
                autopay: "@autopay"
            });
        }

        this.getBraintreeResource = function() {
            return $resource($rootScope.apiUrl + '/billing/braintree_token?result=json');
        }
    });

})();
