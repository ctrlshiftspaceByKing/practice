package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate ;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		//清除缓存
		//  REDIS_PORTAL_CONTENT_LIST_KEY  1  ----2 个
		try {
			redisTemplate.boundHashOps("REDIS_PORTAL_CONTENT_LIST_KEY").delete(content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		contentMapper.insert(content);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//1.获取原来的广告的分类的ID
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		//2.获取更新后的广告的分类的ID
		Long newcategoryId = content.getCategoryId();
		//3.清空缓存
		try {
			if(newcategoryId!=categoryId.longValue()){//删除原来的分类的ID
				redisTemplate.boundHashOps("REDIS_PORTAL_CONTENT_LIST_KEY").delete(categoryId);
			}
			redisTemplate.boundHashOps("REDIS_PORTAL_CONTENT_LIST_KEY").delete(newcategoryId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbContent tbContent = contentMapper.selectByPrimaryKey(id);
			try {
				redisTemplate.boundHashOps("REDIS_PORTAL_CONTENT_LIST_KEY").delete(tbContent.getCategoryId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getContent()!=null && content.getContent().length()>0){
				criteria.andContentLike("%"+content.getContent()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbContent> findContentListByCatId(Long categoryId) {
		//使用缓存是需要注意:要保证正常的业务逻辑不能被影响
		//如果有缓存则从缓存获取列表
		try {
			List<TbContent> contentsList = (List<TbContent>) redisTemplate.boundHashOps("REDIS_PORTAL_CONTENT_LIST_KEY").get(categoryId);
			if (contentsList!=null&&contentsList.size()>0){
				System.out.println("有缓存");
				return contentsList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//如果没有缓存,执行查询数据库
		TbContentExample contentExample = new TbContentExample();
		Criteria criteria = contentExample.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		criteria.andStatusEqualTo("1");//开启状态
		contentExample.setOrderByClause("sort_order");//排序
		List<TbContent> contents = contentMapper.selectByExample(contentExample);

		//查询完成数据库之后,将查询到的列表存入到缓存中去
		try {
			redisTemplate.boundHashOps("REDIS_PORTAL_CONTENT_LIST_KEY").put(categoryId,contents);
			System.out.println("没有缓存");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return contents;
	}

}
