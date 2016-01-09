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
app.directive('hpCard', ['globalVar', function(globalVar) {
  return {
    restrict: 'E',
    scope: {
      card: '=card',
      action: '&'
    },
    link: function(scope, element, attrs) {
        attrs.$observe("type",function(v){
            scope.contentUrl = globalVar.cardViewsPath + 'card_' + v + '.html';
        });
    },
    template: '<div ng-include="contentUrl"></div>',
    controller: ['$scope','$mdBottomSheet', '$mdDialog', '$mdMedia', function($scope,$mdBottomSheet, $mdDialog, $mdMedia) {
        $scope.enableEdit = function(card, $event){
            card.blockEdit = !card.blockEdit;
        };
        $scope.disableEdit = function(card){
            card.blockEdit = false;
        };
        $scope.changeImage = function(card, imageDestination){
            if(!card.blockEdit) return;
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
                card[imageDestination]=answer[0];
            }, function() {
                
            });  
        };
    }]
  };
}]);
app.config(function($mdIconProvider) {
    $mdIconProvider
      .icon('menu:more', 'images/svg/moreButton.svg',24);
  });