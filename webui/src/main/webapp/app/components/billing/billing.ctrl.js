(function() {
    'use strict';

    var app = angular.module('fnBilling');

    app.controller('BillingCtrl', ['$sce', '$scope', '$document', '$mdToast', 'Billing', 'Users',
        function($sce, $scope, $document, $mdToast, Billing, Users) {

        // Min default
        $scope.funds = 5;
        $scope.curPntInfo = {};
        $scope.curPntInfo;
        $scope.newPntInfo = {};
        $scope.newPntInfo.method = {};
        $scope.pntNote = '';
        $scope.card = {};

        var cView = 'PNT_DETAIL';
        var editCard = false;
        var token;

        $scope.savePntInfo = function() {

            var client = new braintree.api.Client({clientToken: token});

            if($scope.newPntInfo.method.type == 'CREDIT_CARD') {
                client.tokenizeCard({
                  number: $scope.card.number,
                  expirationDate: $scope.card.expDate,
                }, function (err, nonce) {
                    var cardType = $scope.card.type;
                    cardType = cardType[0].toUpperCase() + cardType.slice(1)
                    $scope.newPntInfo.method.description = cardType + ' ' + $scope.card.number.substring(0,1)
                        + '*** **** **** ' + $scope.card.number.substring(12) + ' Exp:' + $scope.card.expDate;

                    Billing.getResource().save({
                        type: $scope.newPntInfo.method.type,
                        description: $scope.newPntInfo.method.description,
                        nonce: nonce
                    }).$promise
                    .then(function(ok){
                        toastMe("Payment method successfully added.");
                        $scope.getPntInfo();
                    }).catch(function(error) {
                        toastMe('Unable to validate your credit card. Please verify your information. Code #0008');
                    });
                });
            }

            var pmntRequest = { type: $scope.newPntInfo.method.type,
                description: $scope.newPntInfo.method.description,
                nonce: $scope.newPntInfo.nonce};

            console.debug(JSON.stringify(pmntRequest));

            if($scope.newPntInfo.method.type == 'PAYPAL') {
                Billing.getResource().save(pmntRequest).$promise
                .then(function(ok){
                    // Please toast me
                    toastMe("Payment method successfully added.");
                    $scope.getPntInfo();
                }).catch(function(error) {
                    console.error(JSON.stringify(error));
                    toastMe(JSON.stringify('Unable to add paypal method. Code #0007'));
                });
            }
        };

        $scope.autopay = function(autopay) {
            Billing.getAutopayResource().save({autopay: autopay}).$promise
            .then(function(ok){
                 status = 'off';
                 if (autopay) status = 'on';
                 toastMe("Autopay turned " + status);
                 $scope.getPntInfo();
            }).catch(function(error) {
                toastMe(error.data.message);
            });
        };

        $scope.getPntInfo = function() {
            Billing.getResource().get().$promise
            .then(function(pntInfo){
                if(pntInfo.method){
                    cView = 'PNT_PREVIEW';
                    $scope.curPntInfo = pntInfo;
                    $scope.newPntInfo = $scope.curPntInfo;
                    editCard = false;
                } else {
                    cView = 'PNT_DETAIL';
                    $scope.newPntInfo.method.type = 'PAYPAL';
                }

                if(pntInfo.lastTrans != 0) {
                    $scope.funds = pntInfo.lastTrans;
                }

                $scope.curPntInfo.balance = pntInfo.balance;

                updatePntNote();
            }).catch(function(error) {
                toastMe(error.data.message);
            });
        };

        $scope.fund = function(amount) {
            Billing.getFundsResource().save({amount: amount}).$promise
            .then(function(ok){
                // Please toast me
                toastMe("Successfully added funds to your account");
                var user = Users.getUser();
                user.pmntInfo.balance = user.pmntInfo.balance + amount;
                Users.setUser(user);
                $scope.getPntInfo();
            }).catch(function(error) {
                toastMe(error.data.message);
            });
        };

        // Loading payment info
        $scope.init = function() {
            Billing.getBraintreeResource().get().$promise
            .then(function(t){

                token = t.token;

                braintree.setup(t.token, "paypal", {
                    container: "paypal-container",
                    singleUse: false,
                    onPaymentMethodReceived: function(obj) {
                        $scope.newPntInfo = {};
                        $scope.newPntInfo.method = {};
                        $scope.newPntInfo.method.type = 'PAYPAL';
                        $scope.newPntInfo.nonce = obj.nonce;
                        $scope.newPntInfo.method.description = obj.details.email;
                        $scope.savePntInfo();
                }});
            }).catch(function(error) {
                toastMe('Unable to retrieve payment information. Code #0006', 5000);
            });


            $scope.getPntInfo();
        }

        $scope.init();

        // Support methods

        $scope.validateCard = function() {
            $scope.card.type = getCreditCardType($scope.card.number);
        }

         $scope.hasPntMethod = function() {
             if(!$scope.curPntInfo.method || $scope.curPntInfo.method == {}) return false;
             return true;
         }

         $scope.isView = function(view) {
             if(cView == view) return true;
             return false;
         }

         $scope.setView = function(view) {
             if($scope.hasPntMethod() == false) {
                 cView = 'PNT_DETAIL';
                 $scope.card = {};
             } else {
                 cView = view;
             }
             $scope.chgPntType($scope.curPntInfo.method.type);
         }

         var updatePntNote = function() {
             var note = 'Not payment method has been found. Please select one';

             if ($scope.isCurPntType('PAYPAL')) {
                 note = 'You are using paypal account <strong>' + $scope.curPntInfo.method.description
                     + '</strong>. Use the paypal button to change your account or use a credit card.';

             } else if ($scope.isCurPntType('CREDIT_CARD')) {
                 note = 'You are using your <strong>' + $scope.curPntInfo.method.description
                     + '</strong> as payment method. You may change your credit card or use paypal at any time.';

             }

             $scope.pntNote = $sce.trustAsHtml(note);
         }

         $scope.isCurPntType = function(type) {
             if($scope.curPntInfo.method && $scope.curPntInfo.method.type == type) {
                 return true;
             } else {
                 return false;
             }
         }

         $scope.isPntType = function(type) {
             if($scope.newPntInfo.method && $scope.newPntInfo.method.type == type) {
                 return true;
             } else {
                 return false;
             }
         }

         $scope.chgPntType = function(type) {
             $scope.newPntInfo.method.type = type;
             updatePntNote();
         }

         var toastMe = function(msg, hideDelay) {
             if (!hideDelay) hideDelay = 2000;
                $mdToast.show($mdToast.simple()
                     .position('top right')
                     .parent($document[0].querySelector('#content'))
                     .content(msg)
                     .hideDelay(hideDelay))
                 .then(function() {
                     // Nothing to do
             });
         }
    }]);

    // This must be extended to support more cards
    function getCreditCardType(accountNumber) {

      //start without knowing the credit card type
      var result = "unknown";

      //first check for MasterCard
      if (/^5[1-5]/.test(accountNumber)) {
        result = "mastercard";
      }

      //then check for Visa
      else if (/^4/.test(accountNumber)) {
        result = "visa";
      }

      //then check for AmEx
      else if (/^3[47]/.test(accountNumber)) {
        result = "amex";
      }

      return result;
    }

})();