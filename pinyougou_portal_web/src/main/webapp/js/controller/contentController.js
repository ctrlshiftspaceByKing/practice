app.controller('contentController',function ($scope,contentService) {
    $scope.contentList=[];
    //根据分类（广告类型）的ID 查询分类下的内容（广告）列表
    $scope.findContentListByCatId=function (id) {
        contentService.findContentListByCatId(id).success(
            function (response) {//List<tbcontent>
                $scope.contentList[id]=response;
            }
        )
    }
})