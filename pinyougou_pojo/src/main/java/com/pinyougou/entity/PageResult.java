package com.pinyougou.entity;


import java.io.Serializable;
import java.util.List;

/**
 * @author W
 * @version 1.0 分页结果串行化 封装对象 在系统间传递不能直接传递java对象必须序列化
 * @description com.pinyougou.entity
 * @date 2018/6/15
 */

public class PageResult implements Serializable{

    private long total;//总记录数

    private List rows;//当前页结果

    public PageResult(long total,List rows){
        super();
        this.total=total;
        this.rows=rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
