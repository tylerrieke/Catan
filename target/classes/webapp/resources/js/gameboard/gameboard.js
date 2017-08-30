app.controller('Gameboard', ['$scope', '$http', '$timeout', '$location', '$rootScope',
                    function($scope, $http, $timeout, $location, $rootScope) {
    var vm = $scope;
    vm.xDiff = 77;
    vm.yDiff = 42;
    vm.state = "MENU";
    vm.gameId = "b";
    vm.players = [];
    vm.dice1 = 0;
    vm.dice2 = 0;
    vm.robberId = -1;

    vm.getGameboard = function(gameId) {
        vm.gameId = gameId;
        var onPage = ($location.path()=="/board");
        if(onPage) {
            $http.get("/catan/board?gameId="+gameId).success(function (response) {
                vm.gameboard = response.board;
                if(vm.state!="SETUP" || response.state != "OPEN") {
                    vm.state = response.state;
                }
                vm.players = response.players;
                vm.dice1 = response.dice1;
                vm.dice2 = response.dice2;
                vm.actions = response.actions;
                vm.robberId = response.robberId;
                $timeout(function(){vm.getGameboard(gameId)}, (onPage?501:2000));
            });
        }
    };


    vm.createGameboard = function() {
        $http.get("/catan/board/new").success(function (response) {
            vm.gameboard = response.board;
            if(vm.gameboard) {
                vm.state = response.state;
                vm.gameId = response.gameId;
                vm.getGameboard(vm.gameId);
            }
        });
    };

    vm.startGame = function() {
        var onPage = ($location.path()=="/board");
        if(onPage) {
            $http.get("/catan/board/start?gameId="+vm.gameId).success(function (response) {
                vm.gameboard = response.board;
                vm.state = response.state;
                vm.players = response.players;
            });
        }
    };

    vm.getColor = function(tile) {
        if(!tile) return 'blue';
        if(!tile.resourceName) return 'tan';
        return $rootScope.getResourceColor(tile.resourceName);
    }

    vm.getHarborColor = function(ratios) {
        var color = 'blue';
        var firstLoop = true;
        for (var ratio in ratios) {
            if (ratios.hasOwnProperty(ratio)) {
                if(firstLoop) {
                    firstLoop = false;
                    color = $rootScope.getResourceColor(ratio);
                } else {
                    return 'white';
                }
            }
        }
        return color;
    }

    vm.showCorner = function(tile, num) {
        return !!tile && tile.corners[num].tileIds[0]==tile.id && (vm.isCornerSelection(tile, num) || tile.corners[num].settlement);
    }

    vm.isCornerSelection = function(tile, num) {
        return tile.corners[num].selectable;
    }

    vm.getCornerColor = function(tile, num) {
        return (vm.isCornerSelection(tile,num)?"yellow":(tile.corners[num].settlement?tile.corners[num].settlement.playerColor:"black"));
    }

    vm.showEdge = function(tile, num) {
        return !!tile && tile.edges[num].tileIds[0]==tile.id && (vm.isEdgeSelection(tile, num) || tile.edges[num].road);
    }

    vm.isEdgeSelection = function(tile, num) {
        return tile.edges[num].selectable;
    }

    vm.getEdgeColor = function(tile, num) {
        return (vm.isEdgeSelection(tile,num)?"yellow":(tile.edges[num].road?tile.edges[num].road.playerColor:"black"));
    }

    vm.onCornerClick = function(tile, num) {
        var corner = tile.corners[num];
        if(corner.selectable) {
            $http.get("/catan/player/build_settlement?gameId="+vm.gameId+"&cornerId="+corner.id).success(function (response) {

            });
        }
    }

    vm.onEdgeClick = function(tile, num) {
        var edge = tile.edges[num];
        if(edge.selectable) {
            $http.get("/catan/player/build_road?gameId="+vm.gameId+"&edgeId="+edge.id).success(function (response) {

            });
        }
    }

    vm.getDiceBackground = function() {
        return (vm.actions && vm.actions.indexOf('ROLL') >= 0? 'red':'green');
    }

    vm.getNumberPieceColor = function(tile) {
        if(tile.id == vm.robberId) {
            return '#555555';
        } else if(tile.selectable) {
            return 'yellow';
        } else {
            return '#f4eecb';
        }
    }

    vm.onNumberClick = function(tile) {
        if(vm.actions && vm.actions.indexOf('ROBBER') >= 0 && tile.id != vm.robberId) {
            $http.get("/catan/player/robber?gameId="+vm.gameId+"&tileId="+tile.id).success(function (response) {

            });
        }
    }
}]);


