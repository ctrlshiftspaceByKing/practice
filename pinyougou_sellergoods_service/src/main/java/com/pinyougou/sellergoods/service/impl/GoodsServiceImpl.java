package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.service.GoodsService;
import entity.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional//所有的方法都被事务控制
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper sellerMapper;


	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");//设置未申请状态
		goodsMapper.insert(goods.getGoods());
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置ID
		goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展数据

		//判断
		if ("1".equals(goods.getGoods().getIsEnableSpec())){

				for(TbItem item:goods.getItemList()){
				//标题
				String title = goods.getGoods().getGoodsName();
				Map<String,Object> specMap = JSON.parseObject(item.getSpec());
				for (String key:specMap.keySet()){
					title+=""+specMap.get(key);
				}
				item.setTitle(title);
				item.setGoodsId(goods.getGoods().getId());//商品spu编号
				item.setSellerId(goods.getGoods().getSellerId());//商家编号
				item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
				item.setCreateTime(new Date());//创建日期
				item.setUpdateTime(new Date());//修改日期
				//品牌名称
				TbBrand brand  = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
				item.setBrand(brand.getName());
				//分类名称
				TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
				item.setCategory(itemCat.getName());
				//商家名称
				TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
				item.setSeller(seller.getNickName());
				//图片地址(取spu的第一个图片)
				List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
				if (imageList.size()>0){
					//get("url") url 是事前定义好图片存在服务器上的地址
					item.setImage((String) imageList.get(0).get("url"));
				}
				itemMapper.insert(item);
			}

		}else{
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
			item.setPrice( goods.getGoods().getPrice() );//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			setItemValus(goods,item);
			itemMapper.insert(item);
		}

	}

	private void setItemValus(Goods goods, TbItem item) {
		item.setGoodsId(goods.getGoods().getId());//商品SPU编号
		item.setSellerId(goods.getGoods().getSellerId());//商家编号
		item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//修改日期

		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());

		//商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());

		//图片地址（取spu的第一个图片）
		List<Map>imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
		if(imageList.size()>0){
			item.setImage ( (String)imageList.get(0).get("url"));
		}

	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		TbGoods tbGoods = goods.getGoods();
		tbGoods.setAuditStatus("0");//商家去调用的，要更新商品 都需要审核，所以每一次更新都是将状态更新成0
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		//1.更新tbgoods
		goodsMapper.updateByPrimaryKey(tbGoods);
		//2.更新tbgoodesc
		goodsDescMapper.updateByPrimaryKey(goodsDesc);


		//3.更新SKU列表  List<tbitem> 先删除 原来的SKU列表  再创建最新的SKU列表

		//delete from tbitem where goods_id=1

		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(tbGoods.getId());
		itemMapper.deleteByExample(example);

		//insert


		if("1".equals(tbGoods.getIsEnableSpec())) {
			//如果规格被启用 生成很多个SKU

			//拼接商品的标题
			List<TbItem> itemList = goods.getItemList();

			String itemImages = goodsDesc.getItemImages();//[{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWsOAPwNYAAjlKdWCzvg742.jpg"}]

			for (TbItem item : itemList) {
				String title=tbGoods.getGoodsName();
				String spec = item.getSpec();//{"网络":"移动3G","机身内存":"16G"}
				Map<String,Object> map = JSON.parseObject(spec, Map.class);
				for (String key : map.keySet()) {
					title+=" "+map.get(key).toString();
				}
				item.setTitle(title);//拼接商品的标题

				//获取goodsDesc的图片地址

				if(StringUtils.isNotBlank(itemImages)) {
					List<Map> mapList = JSON.parseArray(itemImages, Map.class);
					Map mapimge = mapList.get(0);
					String url = mapimge.get("url").toString();
					item.setImage(url);
				}

				//三级分类的ID 和名称

				TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());

				item.setCategoryid(tbGoods.getCategory3Id());
				item.setCategory(tbItemCat.getName());

				item.setCreateTime(new Date());
				item.setUpdateTime(item.getCreateTime());
				//设置SPU的ID
				item.setGoodsId(tbGoods.getId());

				//设置商家的ID和店铺名称
				TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());

				item.setSellerId(seller.getSellerId());
				item.setSeller(seller.getNickName());

				//设置item的品牌名称
				TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
				item.setBrand(tbBrand.getName());

				itemMapper.insert(item);

			}
		}else {
			//如果规格没有被启用生成一个SKU  单品
			TbItem item = new TbItem();

			item.setTitle(tbGoods.getGoodsName());
			item.setPrice(tbGoods.getPrice());
			item.setNum(9999);

			String itemImages = goodsDesc.getItemImages();
			//获取goodsDesc的图片地址
			if(StringUtils.isNotBlank(itemImages)) {
				List<Map> mapList = JSON.parseArray(itemImages, Map.class);
				Map mapimge = mapList.get(0);
				String url = mapimge.get("url").toString();
				item.setImage(url);
			}

			//三级分类的ID 和名称

			TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());

			item.setCategoryid(tbGoods.getCategory3Id());
			item.setCategory(tbItemCat.getName());


			item.setCreateTime(new Date());
			item.setUpdateTime(item.getCreateTime());

			//设置SPU的ID
			item.setGoodsId(tbGoods.getId());

			//设置商家的ID和店铺名称
			TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());

			item.setSellerId(seller.getSellerId());
			item.setSeller(seller.getNickName());

			//设置item的品牌名称
			TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
			item.setBrand(tbBrand.getName());


			itemMapper.insert(item);

		}

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
        Goods goods = new Goods();
        //获取tbgoods的数据
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        //获取tbgoodsdesc的数据
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        //获取SKU列表
		TbItemExample itemExample = new TbItemExample();
		itemExample.createCriteria().andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(itemExample);
		goods.setItemList(tbItems);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		TbGoods goods = new TbGoods();
		goods.setIsDelete(true);

		TbGoodsExample example = new TbGoodsExample();//update tbgoods set is_delete=1 where id in (ids);
		example.createCriteria().andIdIn(Arrays.asList(ids));
		goodsMapper.updateByExampleSelective(goods,example);
//		for(Long id:ids){
//			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
//			tbGoods.setIsDelete("1");
//			goodsMapper.updateByPrimaryKey(tbGoods);
//		}
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		if(goods!=null){
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
//				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(!goods.getIsDelete()){
				criteria.andIsDeleteEqualTo(false);
			}
			if(goods.getIsDelete()){
				criteria.andIsDeleteEqualTo(true);
			}
			if(goods.getIsDelete()==null){
				criteria.andIsDeleteEqualTo(false);
			}


		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for(Long id:ids){
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

}
