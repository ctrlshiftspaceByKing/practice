package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author W
 * @version 1.0
 * @description com.pinyougou.manager.controller
 * @date 2018/6/4
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference(timeout = 50000)
    private BrandService brandService;

    @RequestMapping("/findAll")
//    @ResponseBody
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 返回全部列表
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page,int rows){
        return brandService.findPage(page,rows);
    }

    /**
     * 增加品牌
     * @RequestBody 把所有请求参数作为json解析
     * @param tbBrand
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {
            brandService.add(tbBrand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }

}
