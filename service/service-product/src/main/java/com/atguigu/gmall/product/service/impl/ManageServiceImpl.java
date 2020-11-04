package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
