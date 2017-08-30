app.controller('Player', ['$scope', '$http', '$timeout', '$routeParams', function($scope, $http, $timeout, $routeParams) {
    $scope.title = "Catan";
    $scope.name = "";
    $scope.display = "";
    $scope.errorMessage = "";
    $scope.nameEditing = false;
    $scope.displayEditing = false;
    $scope.state = "MENU";
    $scope.gameId = $routeParams.gameId;
    $scope.discards = {};
    $scope.robbable = [];
    $scope.buildable = [];
    $scope.dcs = [];
    $scope.tradeable = [];
    $scope.exchanges = {};
    $scope.availableTrades = 0;
    $scope.trading = false;

    var getPlayer = function() {
        $http.get("/catan/player?gameId="+$scope.gameId).success(function (response) {
            var player = response.player;
            $scope.state = response.state;
            $scope.name = player.name;
            $scope.display = player.display;
            $scope.confirm = response.confirm;
            $scope.player = player;
            $scope.actions = response.actions;
            $scope.active = response.active;
            $scope.discardCount = response.discardCount;
            $scope.canCancel = response.canCancel;
            if($scope.actions.indexOf('TRADE') < 0) {
                $scope.trading = false;
            }
            if($scope.active && $scope.actions.indexOf('ROB_PLAYER') >= 0 && $scope.robbable.length == 0) {
                $http.get("/catan/player/robbable?gameId="+$scope.gameId).success(function (response) {
                    $scope.robbable = response.robbable;
                });
            }
        });
        $timeout(getPlayer,501);
    };

    $scope.getTradeable = function() {
        $http.get("/catan/player/tradeable?gameId="+$scope.gameId).success(function (response) {
            $scope.tradeable = response.tradeable;
            $scope.trading = true;
        });
    }

    $scope.getBuildable = function() {
        $http.get("/catan/player/buildable?gameId="+$scope.gameId).success(function (response) {
            $scope.buildable = response.buildable;
        });
    }

    $scope.getDCs = function() {
        $http.get("/catan/player/dcs?gameId="+$scope.gameId).success(function (response) {
            $scope.dcs = response.dcs;
        });
    }

    $scope.playDC = function(type) {
        $http.get("/catan/player/play_dc?gameId="+$scope.gameId+"&type="+type).success(function (response) {
            $scope.dcs = [];
        });
    }

    $scope.getBuildSelection = function(type) {
        $http.get("/catan/player/build_selection?gameId="+$scope.gameId+"&type="+type).success(function (response) {
            $scope.buildable = [];
        });
    }

    $scope.ok = function(ok) {
        $http.get("/catan/player/confirm?gameId="+$scope.gameId+"&ok="+ok).success(function (response) {
            var player = response.player;
            $scope.state = response.state;
            $scope.name = player.name;
            $scope.display = player.display;
            $scope.confirm = response.confirm;
        });
    }

    $scope.cancelAction = function() {
        $scope.buildable = [];
        $scope.dcs = [];
        $scope.discards = {};
        $scope.exchanges = {};
        $scope.availableTrades = 0;
        $scope.trading = false;
        $scope.discardCount = 0;
    }

    $scope.cancelTurn = function(ok) {
        $scope.buildable = [];
        $http.get("/catan/player/cancel?gameId="+$scope.gameId).success(function (response) {
            var player = response.player;
            $scope.state = response.state;
            $scope.name = player.name;
            $scope.display = player.display;
            $scope.confirm = response.confirm;
            $scope.canCancel = response.canCancel;
        });
    }

    $scope.roll = function() {
        $http.get("/catan/player/roll?gameId="+$scope.gameId).success(function (response) {

        });
    }

    $scope.endTurn = function() {
        $scope.cancelAction();
        $http.get("/catan/player/end_turn?gameId="+$scope.gameId).success(function (response) {

        });
    }

    $scope.rob = function(player) {
        $scope.robbable = [];
        $http.get("/catan/player/rob?gameId="+$scope.gameId+"&robbedId="+player.id).success(function (response) {

        });
    }

    $scope.changeDiscardCount = function(resourceName, delta) {
        var multiple = ($scope.isTrading() ? $scope.tradeable[resourceName] : 1);
        if($scope.discards[resourceName]==undefined) {
            $scope.discards[resourceName] =(delta*multiple);
        } else {
            $scope.discards[resourceName] +=(delta*multiple);
        }
        if($scope.isTrading()) {
            if(delta > 0) {
                $scope.availableTrades++;
            } else {
                $scope.availableTrades--;
            }
        }
    }

    $scope.changeExchangeCount = function(resourceName, delta) {
        if(delta > 0) {
            $scope.availableTrades--;
        } else {
            $scope.availableTrades++;
        }
        if($scope.exchanges[resourceName]==undefined) {
            $scope.exchanges[resourceName] = delta;
        } else {
            $scope.exchanges[resourceName] += delta;
        }
    }

    $scope.submitDiscards = function() {
        $http.post("/catan/player/exchange?gameId="+$scope.gameId,
                {discard:$scope.discards,receive:$scope.exchanges}).success(function (response) {
            $scope.cancelAction();
        });
    }

    $scope.getCountDiscarded = function() {
        var count = 0;
        for(var prop in $scope.discards) {
            if($scope.discards.hasOwnProperty(prop)) {
                count+=$scope.discards[prop];
            }
        }
        return count;
    }

    $scope.showMinusResource = function(resource) {
        return !!$scope.discards[resource] && ($scope.isTrading()?$scope.availableTrades>0:true);
    }

    $scope.showPlusResource = function(resource) {
        if($scope.isTrading()) {
            return $scope.player.resourceCounts[resource] - (!!$scope.discards[resource]?$scope.discards[resource]:0) - $scope.tradeable[resource] >=0;
        } else {
            return ($scope.discardCount - $scope.getCountDiscarded() > 0)
                    && !($scope.discards[resource] >= $scope.player.resourceCounts[resource]);
        }
    }

    getPlayer($scope.gameId);

    $scope.isTrading = function() {
        return $scope.trading;
    }

    $scope.isBuilding = function() {
        return $scope.buildable.length > 0;
    }

    $scope.isPlayingDC = function() {
        return $scope.dcs.length > 0;
    }

    $scope.isActing = function() {
        return $scope.isTrading() || $scope.isBuilding() || $scope.isPlayingDC();
    }

}]);