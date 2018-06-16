package com.pinyougou.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

/**
 * @author W
 * @version 1.0
 * @description com.pinyougou.service
 * @date 2018/6/4
 */
public interface BrandService {
    /**
     * Brand 商标,品牌
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 返回分页列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult findPage(int pageNum,int pageSize);

    /**
     * 新增商品
     */
    public void add(TbBrand tbBrand);

    /**
     * 根据id获得实体
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 修改数据
     * @param tbBrand
     */
    public void update(TbBrand tbBrand);

    /**
     * 根据id批量删除
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     *  条件查询功能，输入品牌名称、首字母后查询，并分页
     * @param tbBrand 搜索条件
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbBrand tbBrand ,int pageNum,int pageSize);
}
