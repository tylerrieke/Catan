app.controller('Gameboard', ['$scope', '$http', '$timeout', '$location', '$rootScope',
                    function($scope, $http, $timeout, $location, $rootScope) {
    var vm = $scope;
    vm.xDiff = 77;
    vm.yDiff = 42;
    vm.state = "MENU";
    vm.gameId = "";
    vm.players = [];
    vm.dice1 = 0;
    vm.dice2 = 0;
    vm.robberId = -1;
    vm.message = "";
    vm.robberFriendly = true;

    vm.cornerSvgSize = 30;
    vm.cornerPoints = [[107,10],[84,52],[32,52],[8,10],[32,-32],[84,-32]];

    vm.getGameboard = function(gameId) {
        vm.gameId = gameId;
        var onPage = ($location.path()=="/board");
        if(onPage) {
            $http.get("/board?gameId="+gameId).success(function (response) {
                vm.gameboard = response.board;
                if(response.state == "OVER" && vm.state != "OVER") {
                    alert(response.currentPlayer+" wins!!");
                }
                if(vm.state!="SETUP" || response.state != "OPEN") {
                    vm.state = response.state;
                }
                vm.players = response.players;
                vm.dice1 = response.dice1;
                vm.dice2 = response.dice2;
                vm.actions = response.actions;
                vm.robberId = response.robberId;
                vm.robberFriendly = response.robberFriendly;
                vm.message = decodeURIComponent(response.message);
                vm.pieceCosts = response.pieceCosts;
                var rolls = [];
                vm.totalRolls = 0;
                angular.forEach(response.rollMap, (count) => { vm.totalRolls+=count; });
                angular.forEach(response.rollMap, (count, num) => {
                    rolls.push({
                        num: new Number(num),
                        countText: vm.calcRollText(count),
                        probability: vm.calcRollProb(num)
                    });
                });
                vm.rollStats = rolls;
                if(vm.state != "OVER") {
                    $timeout(function(){vm.getGameboard(gameId)}, (onPage?501:2000));
                }
            });
        }
    };

    vm.startCreatingGameboard = function() {
        vm.settings = {maxPlayers:4,friendly:true,buildTurn:false,numberOfTiles:19,victoryPoints:10};
        vm.state = "CREATING";
    };

    vm.createGameboard = function() {
        $http.post("/board/new", vm.settings).success(function (response) {
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
            $http.get("/board/start?gameId="+vm.gameId).success(function (response) {
                vm.gameboard = response.board;
                vm.state = response.state;
                vm.players = response.players;
            });
        }
    };

    vm.makeFirst = function(name) {
        var onPage = ($location.path()=="/board");
        if(onPage) {
            $http.get("/board/playerFirst?gameId="+vm.gameId+"&playerName="+name).success(function (response) {
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
            $http.get("/player/build_settlement?gameId="+vm.gameId+"&cornerId="+corner.id).success(function (response) {

            });
        }
    }

    vm.onEdgeClick = function(tile, num) {
        var edge = tile.edges[num];
        if(edge.selectable) {
            $http.get("/player/build_road?gameId="+vm.gameId+"&edgeId="+edge.id).success(function (response) {

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
        if(vm.actions && vm.actions.indexOf('ROBBER') >= 0 && tile.id != vm.robberId && tile.selectable) {
            $http.get("/player/robber?gameId="+vm.gameId+"&tileId="+tile.id).success(function (response) {

            });
        }
    }

    vm.calcRollText = function(count) {
        var percent = vm.totalRolls ? Math.round(count/vm.totalRolls * 100) : 0;
        return `${count}${count ? ' (' + percent +'%)' : ''}`;
    }

    vm.calcRollProb = function(num) {
        return Math.round((6 - Math.abs(7 - num))/36 * 100);
    }
}]);


