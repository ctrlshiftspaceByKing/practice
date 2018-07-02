 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         //totalItems: 10,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	}; 
	
	$scope.selectIds=[];//选中的ID集合 

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}

    /**
	 *
     * @param json   [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
     * @param key    text/其他的属性名
	 *
	 * obj.id=1
	 * obj['id']=1
     */
	$scope.jsonToString=function (json,key) {
		//需要转换成JSON对象
		var fromJson = angular.fromJson(json);
		var str="";
        for (var i=0;i<fromJson.length;i++) {
			str+=fromJson[i][key]+",";
        }

        if(str.length>0){
            str=str.substring(0,str.length-1);
		}
		return str;
    }

    //从集合中按照key查询对象
    $scope.searchObjectByKey=function(list,key,keyValue){
        for(var i=0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }



});	