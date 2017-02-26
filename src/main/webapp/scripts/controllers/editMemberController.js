

angular.module('test-forge').controller('EditMemberController', function($scope, $routeParams, $location, flash, MemberResource , ProjectResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.member = new MemberResource(self.original);
            ProjectResource.queryAll(function(items) {
                $scope.projectsSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id,
                        name: item.name,
                        description: item.description
                    };
                    var labelObject = {
                        value : item.id,
                    	text: item.name + " " + item.description
                    };
                    if($scope.member.projects){
                        $.each($scope.member.projects, function(idx, element) {
                            if(item.id == element.id) {
                                $scope.projectsSelection.push(labelObject);
                                $scope.member.projects.push(wrappedObject);
                            }
                        });
                        self.original.projects = $scope.member.projects;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The member could not be found.'});
            $location.path("/Members");
        };
        MemberResource.get({MemberId:$routeParams.MemberId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.member);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The member was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.member.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Members");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The member was deleted.'});
            $location.path("/Members");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.member.$remove(successCallback, errorCallback);
    };
    
    $scope.projectsSelection = $scope.projectsSelection || [];
    $scope.$watch("projectsSelection", function(selection) {
        if (typeof selection != 'undefined' && $scope.member) {
            $scope.member.projects = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.member.projects.push(collectionItem);
            });
        }
    });
    
    $scope.get();
});