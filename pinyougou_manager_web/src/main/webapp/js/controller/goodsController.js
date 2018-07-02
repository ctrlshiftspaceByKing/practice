//控制层
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,itemCatService,typeTemplateService){

    $controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll=function(){
        goodsService.findAll().success(
            function(response){
                $scope.list=response;
            }
        );
    }

    //分页
    $scope.findPage=function(page,rows){
        goodsService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne=function(){
        var goodsId = $location.search()['id'];
        if(goodsId!=undefined){
            goodsService.findOne(goodsId).success(
                function(response){
                    $scope.entity= response;
                    //设置商品的介绍数据
                    editor.html($scope.entity.goodsDesc.introduction);

                    //转换字符数据 为 JSON   图片
                    $scope.entity.goodsDesc.itemImages=angular.fromJson($scope.entity.goodsDesc.itemImages);

                    //转换成JSON     扩展属性
                    $scope.entity.goodsDesc.customAttributeItems=angular.fromJson($scope.entity.goodsDesc.customAttributeItems);

                    //循环遍历获取到的SKU的列表 转换SKU的对象中的SPEC为JSON对象
                    for(var i=0;i<$scope.entity.itemList.length;i++){
                        $scope.entity.itemList[i].spec=angular.fromJson($scope.entity.itemList[i].spec);
                    }
                    //转换被之前选中的那些规格以及规格选项为JSON对象

                    $scope.entity.goodsDesc.specificationItems=angular.fromJson($scope.entity.goodsDesc.specificationItems);


                }
            );
        }

    }

    //保存
    $scope.save=function(){

        if($scope.entity.goods.id!=null){//如果有ID
            var introduction = editor.html();
            //2.将文本设置到entity中goodsdesc中introdcution字段中
            $scope.entity.goodsDesc.introduction=introduction;
            var serviceObject=goodsService.update( $scope.entity ); //修改
            serviceObject.success(
                function(response){
                    if(response.success){
                        //跳转到商品的管理主页
                        $scope.entity={};
                        editor.html('');
                        location.href="goods.html";
                    }else{
                        alert(response.message);
                    }
                }
            );
        }else{
            var serviceObject=goodsService.add( $scope.entity  );//增加

            //1.获取富文本编辑中的内容（包含html）
            var introduction = editor.html();
            //2.将文本设置到entity中goodsdesc中introdcution字段中
            $scope.entity.goodsDesc.introduction=introduction;

            var serviceObject=goodsService.add( $scope.entity  );//增加
            serviceObject.success(
                function(response){
                    if(response.success){
                        //清空
                        $scope.entity={};
                        editor.html('');
                    }else{
                        alert(response.message);
                    }
                }
            );
        }

    }




    //批量删除
    $scope.dele=function(){
        //获取选中的复选框
        goodsService.dele( $scope.selectIds ).success(
            function(response){
                if(response.success){
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity={};//定义搜索对象

    //搜索
    $scope.search=function(page,rows){
        goodsService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }



    $scope.add=function () {
        //1.获取富文本编辑中的内容（包含html）
        var introduction = editor.html();
        //2.将文本设置到entity中goodsdesc中introdcution字段中
        $scope.entity.goodsDesc.introduction=introduction;

        var serviceObject=goodsService.add( $scope.entity  );//增加
        serviceObject.success(
            function(response){
                if(response.success){
                    //清空
                    $scope.entity={};
                    editor.html('');
                }else{
                    alert(response.message);
                }
            }
        );
    }


    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};

    $scope.addImageEntity=function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }


    //写一个方法 获取一级分类的列表  页面初始化的时候就应该被调用了

    $scope.selectItemCat1List=function () {
        itemCatService.findByParentId(0).success(
            function (response) {//List
                $scope.itemCat1List=response;//列表
            }
        )
    }

    /**
     * 监控变量：entity.goods.category1I的变化 触发相关的业务逻辑处理  处理2级分类列表
     */
    $scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
        if(newValue!=undefined && newValue!=null){
            //清除3级 模板
            itemCatService.findByParentId(newValue).success(
                function (response) {//List
                    $scope.itemCat2List=response;//列表
                }
            )
        }

    })

    /**
     * 监控2及分类ID的值的变化
     */
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
        if(newValue!=undefined && newValue!=null){
            itemCatService.findByParentId(newValue).success(
                function (response) {//List
                    $scope.itemCat3List=response;//列表
                }
            )
        }

    })

    /**
     * 监控三级分类的ID的变化   查询商品分类对应的分类对象中的模板ID
     */
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
        if(newValue!=undefined && newValue!=null){
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.goods.typeTemplateId=response.typeId;
                }
            )
        }

    })


    /**
     * 监控模板的ID 的变化  获取模板的对象（获取对象中的品牌列表）
     */
    $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
        if(newValue!=undefined && newValue!=null){
            typeTemplateService.findOne(newValue).success(
                function (response) {//tbtypetemplate
                    $scope.typeTemplate=response;
                    //转换字符串为JSON对象
                    $scope.typeTemplate.brandIds=angular.fromJson($scope.typeTemplate.brandIds);

                    //表示要新增
                    if($location.search()['id']==undefined || $location.search()['id']==null) {
                        $scope.entity.goodsDesc.customAttributeItems = angular.fromJson($scope.typeTemplate.customAttributeItems);
                    }

                }
            );


            typeTemplateService.findSpecList(newValue).success(
                function (response) {//List<map>
                    $scope.specList=response;
                }
            )


        }

    })


    //$scope.entity.goodsDesc.specificationItems=[ {"attributeValue":["移动3G"],"attributeName":"网络"}]
    //写一个方法 ，在被点击复选框的时候去调用
    /**
     *  {"attributeValue":["移动3G","attributeName":"网络"}
     * @param specName 点击规格选项对应的那个规格的名称  网络
     * @param specValue  点击的规格选项的值   移动3G
     */
    $scope.updateSpecAttribute=function ($event,specName,specValue) {
        /*if(这个对象不存在){
            在变量中：直接添加对象$scope.entity.goodsDesc.specificationItems中添加  {"attributeValue":["移动3G"],"attributeName":"网络"}
        }else{
            从变量：$scope.entity.goodsDesc.specificationItems 获取这个对象 再获取对象中的attributeValue  再push
        }*/
        var specItems = $scope.entity.goodsDesc.specificationItems;
        var searchObject = $scope.searchObjectByKey(specItems,'attributeName',specName);

        if(searchObject==null){
            $scope.entity.goodsDesc.specificationItems.push({"attributeValue":[specValue],"attributeName":specName});
        }else{
            if($event.target.checked){
                //勾选 就要push
                searchObject.attributeValue.push(specValue);
            }else{
                //取消勾选
                searchObject.attributeValue.splice(searchObject.attributeValue.indexOf(specValue),1);
                //判断如果都不勾 就要删除整个对象
                if(searchObject.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(searchObject),1);
                }
            }

        }


    }


    /**
     *
     */
    $scope.createItemList=function () {
        //1.获取$scope.entity.goodsDesc.specificationItems=[{"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}]

        $scope.entity.itemList=[{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];

        var items = $scope.entity.goodsDesc.specificationItems;

        for (var i=0;i<items.length;i++){
            var attriObject = items[i];
            $scope.entity.itemList= addCoumn($scope.entity.itemList,attriObject.attributeName,attriObject.attributeValue);

        }
    }

    /**
     *
     * @param list  [{spec:{},price:0,num:9999,status:'0',isDefault:'0'}]
     * @param columnName  {"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}  --->网络
     * @param columnValues  {"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}--->["移动3G","移动4G"]
     */
    addCoumn=function (list,columnName,columnValues) {
        var newList=[];

        for (var i=0;i<list.length;i++){
            var oldRow=list[i];//{spec:{},price:0,num:9999,status:'0',isDefault:'0'}
            for(var j=0;j<columnValues.length;j++){
                //操作 将spec的key赋值  spec:{"网络":"移动3G"}
                var newRow = angular.fromJson(angular.toJson(oldRow));//{spec:{},price:0,num:9999,status:'0',isDefault:'0'}

                newRow.spec[columnName]=columnValues[j];
                newList.push(newRow);
            }
        }

        return newList;
    }



    $scope.status=['未审核','已审核','审核未通过','已关闭'];

    $scope.itemCatList=[];
    $scope.findAllItemCatList=function () {
        itemCatService.findAll().success(
            function (response) {//List<tbitemcat>
                for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        )
    }

    /**
     * 在复选框的checked属性中调用  目的是为了：从数据库中获取规格和规格选项的数据  和  页面展示规格选项列表 对比，如果匹配上就勾选
     * @returns {boolean}
     */
    $scope.checkAttributeValue=function (specName,specValue) {
        var items=$scope.entity.goodsDesc.specificationItems;
        var searchObject = $scope.searchObjectByKey(items,'attributeName',specName);

        if(searchObject!=null){
            for(var i=0;i<items.length;i++){
                if(searchObject.attributeValue.indexOf(specValue)!=-1){
                    return true;
                }
            }
        }

        return false;
    }


    /**
     *
     * @param status
     */
    $scope.updateStatus=function (status) {
        //1.当点击复选框的时候 将ID 存入到一个数组

        goodsService.updateStatus($scope.selectIds,status).success(
            function (response) {//result
                if(response.success){
                    $scope.selectIds=[];
                    $scope.reloadList();

                }else{
                    alert(response.message);
                }
            }
        )
    }
















});	
