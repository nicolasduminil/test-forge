

angular.module('test-forge').controller('EditMemberController', function($scope, $routeParams, $location, flash, MemberResource , ProjectResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.member = new MemberResource(self.original);
            ProjectResource.queryAll(function(items) {
                $scope.projectSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.member.project){
                        $.each($scope.member.project, function(idx, element) {
                            if(item.id == element.id) {
                                $scope.projectSelection.push(labelObject);
                                $scope.member.project.push(wrappedObject);
                            }
                        });
                        self.original.project = $scope.member.project;
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
    
    $scope.projectSelection = $scope.projectSelection || [];
    $scope.$watch("projectSelection", function(selection) {
        if (typeof selection != 'undefined' && $scope.member) {
            $scope.member.project = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.member.project.push(collectionItem);
            });
        }
    });
    
    $scope.get();
});