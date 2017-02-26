

angular.module('test-forge').controller('EditProjectController', function($scope, $routeParams, $location, flash, ProjectResource , MemberResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.project = new ProjectResource(self.original);
            MemberResource.queryAll(function(items) {
                $scope.membersSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id,
                        firstName: item.firstName,
                        lastName: item.lastName,
                        address: item.address,
                        emailAddress: item.emailAddress
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.firstName + " " + item.lastName
                    };
                    if($scope.project.members){
                        $.each($scope.project.members, function(idx, element) {
                            if(item.id == element.id) {
                                $scope.membersSelection.push(labelObject);
                                $scope.project.members.push(wrappedObject);
                            }
                        });
                        self.original.members = $scope.project.members;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The project could not be found.'});
            $location.path("/Projects");
        };
        ProjectResource.get({ProjectId:$routeParams.ProjectId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.project);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The project was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.project.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Projects");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The project was deleted.'});
            $location.path("/Projects");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.project.$remove(successCallback, errorCallback);
    };
    
    $scope.membersSelection = $scope.membersSelection || [];
    $scope.$watch("membersSelection", function(selection) {
        if (typeof selection != 'undefined' && $scope.project) {
            $scope.project.members = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.project.members.push(collectionItem);
            });
        }
    });
    
    $scope.get();
});