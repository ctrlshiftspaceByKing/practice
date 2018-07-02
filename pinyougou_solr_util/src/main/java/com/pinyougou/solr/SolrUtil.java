package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author W
 * @version 1.0
 * @description com.pinyougou
 * @date 2018/6/30
 */
public class SolrUtil {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private TbItemMapper tbItemMapper;

    public void importData(){
        //获取solrtemplate
        //调用mapper查询所有的数据
        TbItemExample example = new TbItemExample();
        //一定是 状态正常的
        example.createCriteria().andStatusEqualTo("1");
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        //调用solrtemplate.savebeans方法

        for (TbItem tbItem : tbItems) {
            //循环遍历将spec的字符数据 转成Map 然后赋予 动态域
            String spec = tbItem.getSpec();
            Map map = JSON.parseObject(spec, Map.class);
            //给带注解的字段赋值
            tbItem.setSpecMap(map);
            System.out.println(tbItem.getTitle());
        }

        solrTemplate.saveBeans(tbItems);
        //提交
        solrTemplate.commit();;
        System.out.println("===结束===");
    }

    public static void main(String[] args) {
        //1.初始化spring容器
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        //2.获取SolrUtil的实例
        SolrUtil bean = context.getBean(SolrUtil.class);
        //3.调用importData的方法
        bean.importData();
    }


}
