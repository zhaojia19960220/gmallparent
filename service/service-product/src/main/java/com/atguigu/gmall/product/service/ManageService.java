package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.util.List;

public interface ManageService {

    //查询所有的一级分类信息
    List<BaseCategory1> getCategory1();

    //根据一级分类Id 查询二级分类数据
    //List<BaseCategory2> getCategory2(Long category1Id);
    List<BaseCategory2> getCategory2(Long category1Id);

    //根据二级分类Id 查询三级分类数据
    List<BaseCategory3> getCategory3(Long category2Id);

    /**
     * 根据分类Id 获取平台属性数据
     * 接口说明：
     *      1，平台属性可以挂在一级分类、二级分类和三级分类
     *      2，查询一级分类下面的平台属性，传：category1Id，0，0；   取出该分类的平台属性
     *      3，查询二级分类下面的平台属性，传：category1Id，category2Id，0；
     *         取出对应一级分类下面的平台属性与二级分类对应的平台属性
     *      4，查询三级分类下面的平台属性，传：category1Id，category2Id，category3Id；
     *         取出对应一级分类、二级分类与三级分类对应的平台属性
     */
    List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id);

    //保存平台属性 && 修改平台属性
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    //  根据平台属性Id 查询平台属性对象
    //  选中准修改数据 ， 根据该attrId 去查找AttrInfo，该对象下 List<BaseAttrValue> ！
    //  所以在返回的时候，需要返回BaseAttrInfo
    BaseAttrInfo getBaseAttrInfo(Long attrId);

    //  根据属性Id 查询平台属性值集合数据
    List<BaseAttrValue> getAttrValueList(Long attrId);



}
