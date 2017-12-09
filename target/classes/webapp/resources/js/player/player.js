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
    $scope.resourceSelection = {};
    $scope.resourceSelectionCount = 0;
    $scope.resourcesLeftToSelect = 0;
    $scope.availableTrades = 0;
    $scope.exchanging = false;
    $scope.playerTrading = false;

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
            $scope.tradeRequest = response.tradeRequest;
            $scope.tradeResponses = response.tradeResponses;
            $scope.canCancel = response.canCancel;
            if($scope.actions.indexOf('TRADE') < 0) {
                $scope.exchanging = false;
                $scope.playerTrading = false;
            }
            if($scope.active && $scope.actions.indexOf('ROB_PLAYER') >= 0 && $scope.robbable.length == 0) {
                $http.get("/catan/player/robbable?gameId="+$scope.gameId).success(function (response) {
                    $scope.robbable = response.robbable;
                });
            }
            if($scope.active && $scope.actions.indexOf('SELECT_CARDS') >= 0 && angular.equals($scope.resourceSelection,{})) {
                $scope.getResourceSelectable();
            }
        });
        $timeout(getPlayer,501);
    };

    getPlayer($scope.gameId);

    $scope.getTradeable = function(withPlayer) {
        $http.get("/catan/player/tradeable?gameId="+$scope.gameId).success(function (response) {
            $scope.tradeable = response.tradeable;
            $scope.exchanging = !withPlayer;
            $scope.playerTrading = !!withPlayer;
        });
    }

    $scope.getBuildable = function() {
        $http.get("/catan/player/buildable?gameId="+$scope.gameId).success(function (response) {
            $scope.buildable = response.buildable;
        });
    }

    $scope.getResourceSelectable = function() {
        $http.get("/catan/player/card_selection?gameId="+$scope.gameId).success(function (response) {
            $scope.resourceSelection = response.selection.resourceSelection;
            $scope.resourceSelectionCount = response.selection.count;
            $scope.resourcesLeftToSelect = response.selection.count;
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
        $scope.resourceSelection = {};
        $scope.resourceSelectionCount = 0;
        $scope.availableTrades = 0;
        $scope.exchanging = false;
        $scope.playerTrading = false;
        $scope.discardCount = 0;
        $scope.tradeResponses = null;
        $scope.tradeRequest = null;
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
        var multiple = ($scope.exchanging ? $scope.tradeable[resourceName] : 1);
        if($scope.discards[resourceName]==undefined) {
            $scope.discards[resourceName] =(delta*multiple);
        } else {
            $scope.discards[resourceName] +=(delta*multiple);
        }
        if($scope.exchanging) {
            if(delta > 0) {
                $scope.availableTrades++;
            } else {
                $scope.availableTrades--;
            }
        }
    }

    $scope.changeExchangeCount = function(resourceName, delta) {
        if(delta > 0 && !$scope.playerTrading) {
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

    $scope.submitTradeRequest = function() {
        $http.post("/catan/player/init_trade?gameId="+$scope.gameId,
                {discard:$scope.discards,receive:$scope.exchanges}).success(function (response) {
            $scope.cancelAction();
        });
    }

    $scope.answerTrade = function(answer) {
        $scope.active = false;
        $http.get("/catan/player/answer_trade?gameId="+$scope.gameId+"&answer="+answer).success(function (response) {

        });
    }
    $scope.acceptPlayerTrade = function(player) {
        $scope.cancelAction();
        $http.get("/catan/player/accept_trade?gameId="+$scope.gameId+"&acceptedId="+player.id).success(function (response) {

        });
    }

    $scope.changeCardSelectionCount = function(resourceName, delta) {
        if(delta > 0) {
            $scope.resourcesLeftToSelect--;
        } else {
            $scope.resourcesLeftToSelect++;
        }
        if($scope.resourceSelection[resourceName]==undefined) {
            $scope.resourceSelection[resourceName] = delta;
        } else {
            $scope.resourceSelection[resourceName] += delta;
        }
    }

    $scope.submitCardSelection = function(resource) {
        if(resource) {
            $scope.changeCardSelectionCount(resource, 1);
        }
        $http.post("/catan/player/card_selection?gameId="+$scope.gameId,
                $scope.resourceSelection).success(function (response) {
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
        return !!$scope.discards[resource] && ($scope.exchanging?$scope.availableTrades>0:true);
    }

    $scope.showPlusResource = function(resource) {
        if($scope.isTrading()) {
            return $scope.player.resourceCounts[resource] - (!!$scope.discards[resource]?$scope.discards[resource]:0) - ($scope.exchanging?$scope.tradeable[resource]:1) >=0;
        } else {
            return ($scope.discardCount - $scope.getCountDiscarded() > 0)
                    && !($scope.discards[resource] >= $scope.player.resourceCounts[resource]);
        }
    }

    $scope.isAnsweringTrade = function() {
        return !$scope.active && !$scope.tradeResponses && !!$scope.tradeRequest;
    }

    $scope.isTrading = function() {
        return $scope.exchanging || $scope.playerTrading;
    }

    $scope.isSelectingCards = function() {
        return !angular.equals($scope.resourceSelection,{});
    }

    $scope.isBuilding = function() {
        return $scope.buildable.length > 0;
    }

    $scope.isPlayingDC = function() {
        return $scope.dcs.length > 0;
    }

    $scope.isTradingPlayer = function() {
        return $scope.tradeResponses && !angular.equals($scope.tradeResponses,{});
    }

    $scope.isActing = function(canCancel) {
        return $scope.isTrading() || $scope.isBuilding() || $scope.isPlayingDC() || (!canCancel && $scope.isSelectingCards());
    }

    $scope.canAfford = function(resources) {
        for(var resource in resources) {
            if(resources.hasOwnProperty(resource)) {
                if(!$scope.player.resourceCounts[resource] || $scope.player.resourceCounts[resource] < resources[resource]) {
                    return false;
                }
            }
        }
        return true;
    }

}]);