package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author W
 * @version 1.0
 * @description com.pinyougou.sellergoods.service.impl
 * @date 2018/6/4
 */
@Service(timeout = 50000)
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
       //PageHelper Mybatis分页工具
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page= (Page<TbBrand>) tbBrandMapper.selectByExample(null);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    /**
     * updateByPrimaryKey 根据主键更新应用
     * @param tbBrand
     */
    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id:ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);

        TbBrandExample example = new TbBrandExample();
        //创建 查询条件设置
        TbBrandExample.Criteria criteria = example.createCriteria();
        //如果搜索条件不为空
        if(tbBrand!=null){
            if(tbBrand.getName()!=null&&tbBrand.getName().length()>0){
                //如果前端转过来的名字不为空,添加搜索条件
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if(tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);

        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }
}
