var app = angular.module('myApp', ['ngMaterial']);
app.constant('globalVar', {cardViewsPath: 'views/cards/'});

app.factory('remoteFactory', ['$http', function($http){
    var _url="http://localhost:8888/cards/";
    var service= {};

    service.getMetaForNew = function(param){
        var postData = {data: param};
        var promise = $http({
            method: 'POST',
            url: _url+'new/',
            data: postData,
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
    {title:'Akash Shrestha', desc:'A Description goes here', type:'summary_large_image'},
    {title:'Bkash Shrestha', desc:'B Description goes here', type:'gallery_card'},
    {title:'Ckash Shrestha', desc:'C Description goes here', type:'summary_card'},
    {title:'Dkash Shrestha', desc:'D Description goes here', type:'summary_large_image'}
    ];

    $scope.newButton = function(){
        remoteFactory.getMetaForNew('{uri: "http://app/petrol"}').then( function(data){
            console.log(data);
        }, function(data){
            console.log(data);
        });
    };
});
app.directive('hpCard', ['globalVar', function(globalVar) {
  return {
    restrict: 'E',
    scope: {
      card: '=card'
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