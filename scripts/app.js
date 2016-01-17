var app = angular.module('myApp', ['ngMaterial', 'xeditable', 'ngFileUpload', 'textAngular']);
app.constant('globalVar', {cardViewsPath: 'views/cards/'});

app.factory('remoteFactory', ['$http', function ($http) {
    // var _url="http://27.34.8.132:8888/cards/";
    var _url = "http://localhost:8888/cards/";
    var service = {};

    service.getCards = function () {
        var promise = $http({
            method: 'POST',
            url: _url + 'getAll/',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        });
        promise = promise.then(function (response) {
            if (response.status == 200)
                return response.data;
        }, function (response) {
            return response.data
        });
        return promise;
    };

    service.getMetaForNew = function (param) {
        var promise = $http({
            method: 'POST',
            url: _url + 'meta/',
            data: 'data=' + escape(param),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        });
        promise = promise.then(function (response) {
            if (response.status == 200)
                return response.data;
        }, function (response) {
            return response.data
        });
        return promise;
    };

    service.saveCard = function (card) {
        console.log('Saving ' + card);
        var promise = $http({
            method: 'POST',
            url: _url + 'new/',
            data: 'data=' + escape(card),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        });
        return promise;
    };

    service.deleteCard = function (card) {
        var promise = $http({
            method: 'POST',
            url: _url + 'delete/',
            data: 'data=' + escape(card),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        });
        return promise;
    };

    return service;

}]);

app.controller('SimpleController', function ($scope, remoteFactory) {
    $scope.showMaskingProgress = true;
    $scope.newCardProcessing = false;
    $scope.cards = [];

    $scope.init = function () {
        $scope.showMaskingProgress = true;
        remoteFactory.getCards().then(function (data) {
            $scope.cards = data.cards;
            $scope.showMaskingProgress = false;
            dataTypeConvert();
            console.log($scope.cards);
        }, function (response) {
            $scope.showMaskingProgress = false;
        });

        dataTypeConvert = function () {
            for (var i = 0; i < $scope.cards.length; i++) {
                // $scope.cards[i].meta.published = Boolean($scope.cards[i].meta.published==='true');
                if ($scope.cards[i].meta.startTime != undefined)
                    $scope.cards[i].meta.startTime = new Date($scope.cards[i].meta.startTime);
                if ($scope.cards[i].meta.endTime != undefined)
                    $scope.cards[i].meta.endTime = new Date($scope.cards[i].meta.endTime);
            }
            ;
        };
    };

    $scope.filterCards = function (card) {
        if (card.blockEdit)
            return true;
        return ((card.meta.published + '').toLowerCase() == ($scope.publishedOnlySwitch + '').toLowerCase());
    };

    $scope.newButton = function () {
        $scope.newCardProcessing = true;
        var url = $scope.newCardURL;
        if (typeof url == 'undefined')
            return;
        remoteFactory.getMetaForNew('{uri: "' + url + '"}').then(function (data) {
            $scope.newCardProcessing = false;
            var newCard = {type: 'summary_large_image', blockEdit: true, sandboxed: true, callToAction: 'View', meta: {published: true}};
            if (typeof data.title != 'undefined')
                newCard.title = data.title;
            if (typeof data.description != 'undefined')
                newCard.description = data.description;
            if (typeof data.image != 'undefined')
                newCard.image = data.image;
            if (typeof data.url != 'undefined')
                newCard.link = data.url;
            if (typeof data.site != 'undefined')
                newCard.site = data.site;
            $scope.cards.unshift(newCard);
            $scope.newCardURL = '';
        }, function (data) {
            $scope.newCardProcessing = false;
        });
    };

    $scope.deleteCard = function (index) {
        if (index < 0 || index > $scope.cards.length - 1)
            return;
        var card = $scope.cards[index];
        if (card.sandboxed) { //new card not in db
            $scope.cards.splice(index, 1);
        }
        else {
            $scope.showMaskingProgress = true;
            console.log('remote call to delete');
            remoteFactory.deleteCard(JSON.stringify(card)).then(function(response){
                if(response.status==200){
                    $scope.init();
                }
            },function(response){
                console.log(response);
                $scope.showMaskingProgress = false;
            });
        }
        $scope.init();
    };

    $scope.saveCard = function (index) {
        if (index < 0 || index > $scope.cards.length - 1)
            return;
        var card = $scope.cards[index];
        var promise = remoteFactory.saveCard(JSON.stringify(card));
        promise.then(function (response) {
            $scope.init();
            return response;
        }, function (response) {
            $scope.init();
            return response;
        });
        return promise;
    };

    $scope.init();
});
app.directive('hpCard', ['globalVar', '$timeout', function (globalVar, $timeout) {
    return {
        restrict: 'E',
        scope: {
            card: '=card',
            deleteCard: '&',
            saveCard: '&'
        },
        link: {
            pre: function (scope, element, attrs) {
                attrs.$observe("type", function (v) {
                    scope.contentUrl = globalVar.cardViewsPath + 'card_' + v + '.html';
                });
                // console.log(element.find('img'));
            },
            post: function (scope, element, attrs) {
                $timeout(function () {
                    element.find('img').watch("ngSrc", function (v) {
                        element.find('img').addClass('progress');
                    });
                    element.find('img').bind('load', function (event) {
                        console.log(event);
                        element.find('img').removeClass('progress');
                    });
                });
            }
        },
        template: '<div ng-include="contentUrl"></div>',
        controller: ['$scope', '$mdBottomSheet', '$mdDialog', '$mdMedia', function ($scope, $mdBottomSheet, $mdDialog, $mdMedia) {
            $scope.callToActionOptions = [
                'Download',
                'View',
                'Play',
                'Watch'
            ];
            $scope.cardTypes = ['summary_large_image', 'summary_card', 'gallery_card'];
            $scope.enableEdit = function ($event) {
                $scope.backUpCard = JSON.parse(JSON.stringify($scope.card));
                $scope.card.blockEdit = !$scope.card.blockEdit;
            };
            $scope.cancelEdit = function () {
                if ($scope.backUpCard != undefined)
                    $scope.card = JSON.parse(JSON.stringify($scope.backUpCard));
                $scope.card.blockEdit = false;
                if ($scope.card.sandboxed)
                    $scope.deleteCard();
            };
            $scope.changeImage = function (imageDestination) {
                if (!$scope.card.blockEdit) return;
                var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'));
                $mdDialog.show({
                    controller: FileDialogController,
                    templateUrl: 'views/fileSelectTemplate.html',
                    parent: angular.element(document.body),
                    //targetEvent: ev,
                    clickOutsideToClose: true,
                    fullscreen: useFullScreen
                })
                    .then(function (answer) {
                        console.log(answer);
                        $scope.card[imageDestination] = answer[0];
                    }, function () {
                    });

            };
            $scope.save = function ($event) {
                console.log('Save ');
                $scope.saveCard().then(function (response) {
                    $scope.backUpCard = null;
                    $scope.card.blockEdit = false;
                    console.log(response);
                }, function (response) {
                    console.log(response);
                });
            };
            $scope.onDateToggle = function (dateOptionsEnabled, $event) {
                console.log(dateOptionsEnabled + ' changed');
                if (dateOptionsEnabled) {
                    $scope.card.meta.startTime = new Date();
                    $scope.card.meta.endTime = new Date();
                }
                else {
                    delete $scope.card.meta.startTime;
                    delete $scope.card.meta.endTime;
                }
            };

            $scope.deleteIt = function($event) {
                var confirm = $mdDialog.confirm()
                      .title('Are you sure to delete this card?')
                      .targetEvent($event)
                      .ok('Yes, delete it!')
                      .cancel('Abort');
                $mdDialog.show(confirm).then(function() {
                    console.log('You decided to get rid of your debt.');
                    $scope.deleteCard();
                }, function() {
                    console.log('You decided to keep your debt.');
                });
            };
        }]
    };
}]);
app.config(function ($mdIconProvider) {
    $mdIconProvider
        .icon('menu:more', 'images/svg/moreButton.svg', 24);
});