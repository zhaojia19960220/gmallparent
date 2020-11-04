package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品基础属性接口")
@RestController
@RequestMapping("admin/product")
//@CrossOrigin   //网关已经添加跨域配置类，不需要加注解了
public class BaseManageController {

    @Autowired
    private ManageService manageService;

    /**
     * 查询所有的一级分类信息
     * @return
     */
    @GetMapping("getCategory1")
    public Result<List<BaseCategory1>> getCategory1() {
        List<BaseCategory1> baseCategory1List = manageService.getCategory1();
        return Result.ok(baseCategory1List);
    }

    /**
     * 根据一级分类Id 查询二级分类数据
     * @param category1Id
     * @return
     */
    @GetMapping("getCategory2/{category1Id}")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable Long category1Id) {
        List<BaseCategory2> baseCategory2List = manageService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }

    /**
     * 根据二级分类Id 查询三级分类数据
     * @param category2Id
     * @return
     */
    @GetMapping("getCategory3/{category2Id}")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable Long category2Id) {
        List<BaseCategory3> baseCategory3List = manageService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }

    /**
     * 根据分类Id 获取平台属性数据
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> attrInfoList(@PathVariable("category1Id") Long category1Id,
                                                   @PathVariable("category2Id") Long category2Id,
                                                   @PathVariable("category3Id") Long category3Id) {
        List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrInfoList(category1Id, category2Id, category3Id);
        return Result.ok(baseAttrInfoList);
    }

    /**
     * 保存平台属性方法
     * 前台传递的是json格式数据，要转化为对象才能接收，所以要用@RequestBody，那么这个对象就是BaseAttrInfo
     * springMVC:
     *          ResponseBody{将java对象转化为Json字符串，直接将数据输入}，一般方法头或类头上
     *          RequestBody{将json字符串转化为Java Object}，一般放在参数列表中
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        // 前台数据都被封装到该对象中baseAttrInfo
        manageService.saveAttrInfo(baseAttrInfo) ;
        return Result.ok();
    }

    //  http://api.gmall.com/admin/product/getAttrValueList/{attrId}
    //  根据平台属性ID获取平台属性对象数据
    @GetMapping("getAttrValueList/{attrId}")
    public Result<List<BaseAttrValue>> getAttrValueList(@PathVariable Long attrId){
        //  attrId = base_attr_value.attr_id = base_attr_info.id
        //  先查询baseAttrInfo , 从baseAttrInfo 中获取到baseAttrValue 的集合
        BaseAttrInfo baseAttrInfo = manageService.getBaseAttrInfo(attrId);
        //  List<BaseAttrValue> baseAttrValueList = manageService.getAttrValueList(attrId);
        return Result.ok(baseAttrInfo.getAttrValueList());
    }
}
