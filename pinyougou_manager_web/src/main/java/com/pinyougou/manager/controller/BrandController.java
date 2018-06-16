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

    /**
     * 根据id查询实体 在页面回显数据
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }


    /**
     * 修改商品
     * @param tbBrand
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand );
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }

    /**
     * 根据id批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    /**
     * 查询分页
     * @param tbBrand
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand tbBrand ,int page,int rows){
        return brandService.findPage(tbBrand,page,rows);
    }

}
