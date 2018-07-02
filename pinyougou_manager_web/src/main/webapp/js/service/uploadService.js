//文件上传服务层
app.service("uploadService",function($http){
    this.uploadFile=function(){
        var formData=new FormData();
        formData.append("file",file.files[0]);
        //第一个参数：file和controller中的参数一致
        // 第二个参数：file 和input 中的id一致 这里支持多图片，现在只要一个所以就选一张即可.

        return $http({
            method:'POST',
            url:"../upload.do",
            data: formData,
            /**
             * anjularjs对于post和get请求默认的Content-Type header 是application/json。通过设置‘Content-Type’: undefined，这样浏览器会帮我们把Content-Type 设置为 multipart/form-data
             */
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        });
    }
});
