app.service('contentService',function ($http) {
    this.findContentListByCatId=function (categoryId) {
        return $http.get('/content/findContentListByCatId.do?categoryId='+categoryId);
    }
})