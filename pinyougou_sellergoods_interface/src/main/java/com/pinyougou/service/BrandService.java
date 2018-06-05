package com.pinyougou.service;

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
}
