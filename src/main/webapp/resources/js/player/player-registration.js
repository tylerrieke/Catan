app.controller('PlayerRegistration', ['$scope', '$http', '$timeout', '$location', function($scope, $http, $timeout, $location) {

    $scope.title = "Catan";
    $scope.name = "";
    $scope.display = "";
    $scope.editing = false;
    $scope.errorMessage = "";
    $scope.errorCount = 0;
    $scope.editingCount = 0;
    $scope.nameEditing = false;
    $scope.displayEditing = false;
    $scope.maxNameLength = 20;
    $scope.maxDisplayLength = 4;
    $scope.minDisplayLength = 2;
    $scope.state = "MENU";
    $scope.gameId = "";

    $scope.getGameboard = function(gameId) {
        var onPage = ($location.path()=="/player-registration");
        if(onPage) {
            $http.get("/board?gameId="+gameId).success(function (response) {
                $scope.state = response.state;
                $scope.gameId = gameId;
                $timeout(getPlayer, (onPage?501:2000));
            });
        }
    };

    var getPlayer = function() {
        var onPage = ($location.path()=="/player-registration");

        if(onPage) {
            $http.get("/player?gameId="+$scope.gameId+"&full=false").success(function (response) {
                var player = response.player;
                $scope.state = response.state;
                if(!$scope.editing) {
                    $scope.name = player.name;
                    $scope.display = player.display;
                }

                if ($scope.state === "SETUP" || $scope.state === "ACTIVE") {
                    $scope.active = false;
                    window.location = '/#/player/'+$scope.gameId;
                    return;
                }
            });
        }
        $timeout(getPlayer, (onPage?501:2000));
    };

    $scope.updatePlayer = function(field, value) {
        if(field=='name') {
            $scope.name = value;
            if($scope.showNameLengthError()) {
                return;
            }
            $scope.nameEditing = false;
        } else {
            $scope.display = value;
            if($scope.showDisplayLengthError()) {
                return;
            }
            $scope.displayEditing = false;
        }
        var editingNum = $scope.editingCount;
        $http.get("/player/update?name="+$scope.name+"&display="+$scope.display+"&gameId="+$scope.gameId).success(function (response) {
            if(response.error) {
                $scope.errorMessage = response.error;
                $scope.errorCount++;
                var errorNum = $scope.errorCount;
                $timeout(function(){clearError(errorNum)}, 3000);
            }
            $scope.editing = (editingNum != $scope.editingCount);
        }).error(function() {
            $scope.editing = (editingNum != $scope.editingCount);
        });
    };

    var clearError = function(errorNum) {
        if($scope.errorCount==errorNum) {
            $scope.errorMessage = "";
        }
    };

    $scope.setEditing = function(field) {
        if(field=='name') {
            $scope.nameEditing = true;
        } else {
            $scope.displayEditing = true;
        }

        $scope.editingCount++;
        $scope.editing = true;
    };

    $scope.doBlur = function($event){
        var target = $event.target;
        target.blur();
    }

    $scope.showNameLengthError = function() {
        return !$scope.name || $scope.name.length>$scope.maxNameLength;
    }

    $scope.showDisplayLengthError = function() {
        return !$scope.display || $scope.display.length>$scope.maxDisplayLength || $scope.display.length<$scope.minDisplayLength;
    }
}]);