var app = angular.module('myApp', ['ngMaterial', 'xeditable','ngFileUpload','textAngular']);
app.constant('globalVar', {cardViewsPath: 'views/cards/'});

app.factory('remoteFactory', ['$http', function($http){
    var _url="http://localhost:8888/cards/";
    var service= {};

    service.getMetaForNew = function(param){
        var postData = {data: param};
        var promise = $http({
            method: 'POST',
            url: _url+'meta/',
            data: 'data='+param,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        });
        promise = promise.then(function (response) {
            return response.data;
        },function(response){
          return response.data
        });
        return promise;
      }
    return service;

}])

app.controller('SimpleController', function($scope,remoteFactory) {
    $scope.showMaskingProgress = false;
    $scope.cards = [
    {title:'Ckash Shrestha', description:'C Description goes here', image:'images/3.jpg', type:'summary_card'},
    {title:'Akash Shrestha', description:'A Description goes here', image:'images/2.jpg', type:'summary_large_image'},
    {title:'Bkash Shrestha', description:'B Description goes here', image:'images/1.jpg', type:'gallery_card'},
    {title:'Dkash Shrestha', description:'D Description goes here', image:'images/4.jpg', type:'summary_large_image'}
    ];

    $scope.newButton = function(){
        $scope.showMaskingProgress = true;
        var url = $scope.newCardURL;
        if(typeof url == 'undefined')
            return;
        remoteFactory.getMetaForNew('{uri: "'+url+'"}').then( function(data){
            console.log(data);
            $scope.showMaskingProgress = false;
            var newCard= {type: 'summary_large_image', blockEdit:true};
            if(typeof data.title != 'undefined')
                newCard.title = data.title;
            if(typeof data.description != 'undefined')
                newCard.description = data.description;
            if(typeof data.image != 'undefined')
                newCard.image = data.image;
            if(typeof data.url != 'undefined')
                newCard.link = data.url;
            if(typeof data.site != 'undefined')
                newCard.site = data.site;
            $scope.cards.unshift(newCard);
        }, function(data){
            console.log(data);
            $scope.showMaskingProgress = false;
        });
    };
});
app.directive('hpCard', ['globalVar','$timeout', function(globalVar,$timeout) {
  return {
    restrict: 'E',
    scope: {
      card: '=card',
      action: '&'
    },
    link: {
        pre: function(scope, element, attrs) {
            attrs.$observe("type",function(v){
                scope.contentUrl = globalVar.cardViewsPath + 'card_' + v + '.html';
            });
            // console.log(element.find('img'));
        },
        post: function(scope, element, attrs) {
            
            // element.find('img').each().on('load', function() {
            //   element.addClass('in');
            // }).on('error', function() {
            //   //
            // });
            // element.find('img').$watch('ngSrc', function(newVal) {
            //   element.removeClass('in');
            // });
            // console.log(angular.element(element).find('img'));
            $timeout(function () {
                    element.find('img').bind('load', function (event) {
                        console.log(event);
                        // $(this).addClass('no-progress');
                        // console.log(this);
                    })
                });
        }
    },
    template: '<div ng-include="contentUrl"></div>',
    controller: ['$scope','$mdBottomSheet', '$mdDialog', '$mdMedia', function($scope,$mdBottomSheet, $mdDialog, $mdMedia) {
        $scope.enableEdit = function($event){
            $scope.backUpCard = JSON.parse(JSON.stringify($scope.card));
            $scope.card.blockEdit = !$scope.card.blockEdit;
        };
        $scope.cancelEdit = function() {
            $scope.card = JSON.parse(JSON.stringify($scope.backUpCard));
            $scope.card.blockEdit = false;
        };
        $scope.changeImage = function(imageDestination){
            if(!$scope.card.blockEdit) return;
            var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'));
            $mdDialog.show({
                controller: FileDialogController,
                templateUrl: 'views/fileSelectTemplate.html',
                parent: angular.element(document.body),
                //targetEvent: ev,
                clickOutsideToClose:true,
                fullscreen: useFullScreen
            })
            .then(function(answer) {
                console.log(answer);
                $scope.card[imageDestination]=answer[0];
            }, function() {
                
            });  
        };
        $scope.save = function($event) {
            $scope.backUpCard = null;
            $scope.card.blockEdit = false;
        };
    }]
  };
}]);
app.config(function($mdIconProvider) {
    $mdIconProvider
      .icon('menu:more', 'images/svg/moreButton.svg',24);
  });