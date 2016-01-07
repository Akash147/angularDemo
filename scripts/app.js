var app = angular.module('myApp', ['ngMaterial']);
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
    $scope.cards = [
    {type: 'empty'},
    {title:'Akash Shrestha', description:'A Description goes here', type:'summary_large_image'},
    {title:'Bkash Shrestha', description:'B Description goes here', type:'gallery_card'},
    {title:'Ckash Shrestha', description:'C Description goes here', type:'summary_card'},
    {title:'Dkash Shrestha', description:'D Description goes here', type:'summary_large_image'}
    ];

    $scope.newButton = function(){
        remoteFactory.getMetaForNew('{uri: "https://www.youtube.com/watch?v=DV0TJZ7Kp40"}').then( function(data){
            console.log(data);
            var newCard= {type: 'summary_large_image'};
            if(typeof data.title != 'undefined')
                newCard.title = data.title;
            if(typeof data.description != 'undefined')
                newCard.description = data.description;
            if(typeof data.image != 'undefined')
                newCard.image = data.image;
            if(typeof data.link != 'undefined')
                newCard.link = data.link;
            $scope.cards.unshift(newCard);
        }, function(data){
            console.log(data);
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
    template: '<div ng-include="contentUrl"></div>'
  };
}]);
app.config(function($mdIconProvider) {
    $mdIconProvider
      .icon('menu:more', 'images/svg/moreButton.svg',24);
  });