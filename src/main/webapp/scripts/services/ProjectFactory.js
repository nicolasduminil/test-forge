angular.module('test-forge').factory('ProjectResource', function($resource){
    var resource = $resource('rest/projects/:ProjectId',{ProjectId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});