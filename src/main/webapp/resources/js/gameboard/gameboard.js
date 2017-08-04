app.controller('Gameboard', ['$scope', '$http', '$timeout', '$location', function($scope, $http, $timeout, $location) {
    var vm = $scope;
    vm.xDiff = 77;
    vm.yDiff = 42;

    var getGameboard = function() {
        var onPage = ($location.path()=="/board");

        if(onPage) {
            $http.get("/catan/board").success(function (response) {
                vm.gameboard = response.board;

            });
        }
        $timeout(getGameboard, (onPage?501:2000));
    };

    vm.getColor = function(tile) {
        if(!tile) return 'blue';
        if(!tile.resourceName) return 'tan';
        if(tile.resourceName == 'Sheep') return '#b9d544';
        if(tile.resourceName == 'Brick') return '#dd5133';
        if(tile.resourceName == 'Wood') return '#1e6b33';
        if(tile.resourceName == 'Wheat') return '#f3c940';
        if(tile.resourceName == 'Ore') return '#7e8a7c';
        return black;
    }

    vm.getHarborColor = function(ratios) {
        var color = 'blue';
        var firstLoop = true;
        for (var ratio in ratios) {
            if (ratios.hasOwnProperty(ratio)) {
                if(firstLoop) {
                    firstLoop = false;
                    if(ratio == 'Sheep') color = '#b9d544';
                    else if(ratio == 'Brick') color = '#dd5133';
                    else if(ratio == 'Wood') color = '#1e6b33';
                    else if(ratio == 'Wheat') color = '#f3c940';
                    else if(ratio == 'Ore') color = '#7e8a7c';
                } else {
                    return 'white';
                }
            }
        }
        return color;
    }

    getGameboard();
}]);


