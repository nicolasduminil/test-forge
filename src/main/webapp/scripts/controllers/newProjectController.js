
angular.module('test-forge').controller('NewProjectController', function ($scope, $location, locationParser, flash, ProjectResource , MemberResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.project = $scope.project || {};
    
    $scope.membersList = MemberResource.queryAll(function(items){
        $scope.membersSelectionList = $.map(items, function(item) {
            return ( {
                value : item.id,
                text : item.firstName + " " + item.lastName
            });
        });
    });
    $scope.$watch("membersSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.project.members = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.project.members.push(collectionItem);
            });
        }
    });


    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The project was created successfully.'});
            $location.path('/Projects');
        };
        var errorCallback = function(response) {
            if(response && response.data) {
                flash.setMessage({'type': 'error', 'text': response.data.message || response.data}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        ProjectResource.save($scope.project, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Projects");
    };
});