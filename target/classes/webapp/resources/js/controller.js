'use strict';

var app = angular.module('app', ['ngRoute','door3.css'])
.run(function($rootScope) {
    $rootScope.getResourceColor = function(resourceName) {
        if(resourceName == 'Sheep') return '#b9d544';
        if(resourceName == 'Brick') return '#dd5133';
        if(resourceName == 'Wood') return '#1e6b33';
        if(resourceName == 'Wheat') return '#f3c940';
        if(resourceName == 'Ore') return '#7e8a7c';
        return 'black';
    }
});

app.config(function($routeProvider) {
    $routeProvider

        .when('/board', {
            templateUrl : 'resources/js/gameboard/gameboard.html',
            controller  : 'Gameboard',
            css: ['resources/css/gameboard.css']
        })
        .when('/player-registration', {
            templateUrl : 'resources/js/player/player-registration.html',
            controller  : 'PlayerRegistration',
            css: ['resources/css/player.css']
        })
        .when('/player/:gameId', {
            templateUrl : 'resources/js/player/player.html',
            controller  : 'Player',
            css: ['resources/css/player.css']
        })
        .otherwise({
            redirectTo: '/player-registration'
        });

});

app.controller('Banner', function($scope) {
    $scope.title = "No Title";
});

app.directive('focusMe', function($timeout) {
    return {
        link: function(scope, element, attrs) {
            scope.$watch(attrs.focusMe, function(value) {
                if(value === true) {
                    console.log('value=',value);
                    //$timeout(function() {
                    element[0].focus();
                    scope[attrs.focusMe] = false;
                    //});
                }
            });
        }
    };
});

app.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                element[0].blur();
                event.preventDefault();
            }
        });
    };
});