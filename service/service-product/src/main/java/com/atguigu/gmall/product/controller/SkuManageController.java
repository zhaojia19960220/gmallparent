package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "sku 数据接口")
@RestController
@RequestMapping("admin/product")
public class SkuManageController {
    @Autowired
    private ManageService manageService;

    //添加sku
    //http://api.gmall.com/admin/product/saveSkuInfo
    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        // 调用服务层
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    //获取sku分页列表
    //http://api.gmall.com/admin/product/list/{page}/{limit}
    @GetMapping("list/{page}/{limit}")
    public Result getSkuInfoList(@PathVariable Long page,
                       @PathVariable Long limit){
        //创建page对象
        Page<SkuInfo> skuInfoPage = new Page<>(page, limit);
        IPage<SkuInfo> skuInfoIPage = manageService.getPage(skuInfoPage);
        return Result.ok(skuInfoIPage);
    }

    //商品上架
    //http://api.gmall.com/admin/product/onSale/{skuId}
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        manageService.onSale(skuId);
        return Result.ok();
    }

    //商品下架
    //http://api.gmall.com/admin/product/cancelSale/{skuId}
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        manageService.cancelSale(skuId);
        return Result.ok();

    }
}
