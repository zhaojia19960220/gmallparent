package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    //根据spuId获取销售属性
    List<SpuSaleAttr> selectSpuSaleAttrList(Long spuId);
}