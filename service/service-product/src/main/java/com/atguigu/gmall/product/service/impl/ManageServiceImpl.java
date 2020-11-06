package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ConnectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private  SkuAttrValueMapper skuAttrValueMapper;

    //查询所有的一级分类信息
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    //根据一级分类Id 查询二级分类数据
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        //  select * from base_category2 where category1_id = category1Id;
        //  查询哪个实体类或者数据库表，
        //  构建查询条件
        QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        wrapper.eq("category1_id",category1Id);
        List<BaseCategory2> baseCategory2List = baseCategory2Mapper.selectList(wrapper);
        return baseCategory2List;
    }
    
    //根据二级分类Id 查询三级分类数据
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        // select * from baseCategory3 where Category2Id = ?
        QueryWrapper queryWrapper = new QueryWrapper<BaseCategory3>();
        queryWrapper.eq("category2_id", category2Id);
        return baseCategory3Mapper.selectList(queryWrapper);
    }

    //根据分类Id 获取平台属性数据
    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
         //调用mapper：
        return baseAttrInfoMapper.selectBaseAttrInfoList(category1Id, category2Id, category3Id);
    }

    //保存平台属性方法
    @Override
    @Transactional  //添加事务，两个表插入要保证一致性
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 什么情况下 是添加，什么情况下是更新，修改 根据baseAttrInfo 的Id
        // baseAttrInfo如果有id是修改，没有id是保存
        if (baseAttrInfo.getId() != null) {
            // 修改数据
            baseAttrInfoMapper.updateById(baseAttrInfo);
        } else {
            // 新增
            // baseAttrInfo 插入数据
            baseAttrInfoMapper.insert(baseAttrInfo);
        }

        // baseAttrValue 平台属性值
        // 修改：通过先删除{baseAttrValue}，在新增的方式！
        // 删除条件：baseAttrValue.attrId = baseAttrInfo.id
        QueryWrapper queryWrapper = new QueryWrapper<BaseAttrValue>();
        queryWrapper.eq("attr_id", baseAttrInfo.getId());
        baseAttrValueMapper.delete(queryWrapper);

        // 获取页面传递过来的所有平台属性值数据
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (attrValueList != null && attrValueList.size() > 0) {
            // 循环遍历
            for (BaseAttrValue baseAttrValue : attrValueList) {
                // 获取平台属性Id 给attrId
                baseAttrValue.setAttrId(baseAttrInfo.getId()); // ?
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }
    }

    @Override
    public BaseAttrInfo getBaseAttrInfo(Long attrId) {
        //  attrId = base_attr_value.attr_id = base_attr_info.id
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);
        //  判断平台属性不为空！
        if (baseAttrInfo!=null){
            //  baseAttrInfo 没有属性值集合字段
            //  给attrValueList 赋值！因为控制器要调用平台属性值集合！
            //  select * from base_attr_value where attr_id =  attrId;
            baseAttrInfo.setAttrValueList(this.getAttrValueList(attrId));
        }
        //  返回数据！
        return baseAttrInfo;
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        //  select * from base_attr_value where attr_id =  attrId;
        return baseAttrValueMapper.selectList(new QueryWrapper<BaseAttrValue>().eq("attr_id",attrId));
    }

    @Override
    public IPage<SpuInfo> getSpuInfoPage(Page<SpuInfo> pageParam, SpuInfo spuInfo) {
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", spuInfo.getCategory3Id());
        queryWrapper.orderByDesc("id");
        return spuInfoMapper.selectPage(pageParam, queryWrapper);
    }


    //查询所有的销售属性数据
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    //保存spu
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        /*
        * 1：spuInfo 商品表
        * 2：spuImage 商品图片表
        * 3：spuSaleAttr 销售属性表
        * 4：spuSaleAttrValue 销售属性值表
        * */
        //保存spuInfo 商品表数据
        spuInfoMapper.insert(spuInfo);
        //保存spuImage 商品图片表
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        //判断
        if(spuImageList != null && spuImageList.size() > 0){
            //循环遍历
            for (SpuImage spuImage : spuImageList) {
                //因为返回的数据没有spu_id,所以要先添加id
                spuImage.setSpuId(spuInfo.getId());
                //保存数据
                spuImageMapper.insert(spuImage);
            }
        }

        //spuSaleAttr 销售属性表
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                //spuSaleAttrValue 销售属性值表
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList != null && spuSaleAttrValueList.size() > 0) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }
    }


    //根据spuId获取销售属性
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    //添加sku
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        /*
            skuInfo 库存单元表 --- spuInfo！
            skuImage 库存单元图片表 --- spuImage!
            skuSaleAttrValue sku销售属性值表{sku与销售属性值的中间表} --- skuInfo ，spuSaleAttrValue
            skuAttrValue sku与平台属性值的中间表 --- skuInfo ，baseAttrValue
        */

        //1:保存skuInfo 库存单元表
        skuInfoMapper.insert(skuInfo);

        //2；保存 skuImage 库存单元图片表
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        //判断
        //if(skuImageList != null && skuImageList.size()>0)//这个两个判断条件都可以
        if(CollectionUtils.isEmpty(skuImageList)){
            //循环遍历
            for (SkuImage skuImage : skuImageList) {
                //数据补全
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }
        //保存skuSaleAttrValue sku销售属性值表{sku与销售属性值的中间表}
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        //判断
        if(!CollectionUtils.isEmpty(skuSaleAttrValueList)){
            //循环遍历
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                //补全数据
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }

        //保存skuAttrValue sku与平台属性值的中间表
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        //判断
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            //循环遍历
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                //补全数据
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
    }

    //获取sku分页列表
    @Override
    public IPage<SkuInfo> getPage(Page<SkuInfo> skuInfoPage) {
        QueryWrapper<SkuInfo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(skuInfoPage, wrapper);
        return skuInfoIPage;
    }

    //商品上架
    @Override
    public void onSale(Long skuId) {
        //  update sku_info set is_sale = 1 where id = skuId
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    //商品下架
    @Override
    public void cancelSale(Long skuId) {
        // update sku_info set is_sale = 0 where id = skuId
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }
}
