package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端的店铺管理
 * 因为店铺状态要么是1，要么是0，而数据形式是key-value, 所以使用Redis数据库，就没必要使用MySQL数据库（表格）
 */
@Slf4j
@RequestMapping("/admin/shop")
@RestController("adminShopcontroller") //取名为adminShopcontroller，用于和user的controller区分
@Api(tags = "店铺相关接口")
public class ShopController {

    public static final String KEY  = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺营业状态，营业中或打烊中
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态，营业中或打烊中")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态，{}",status == 1 ? "营业中":"打烊中");
        redisTemplate.opsForValue().set(KEY, status); // 在redis数据库中设置店铺营业状态，key="SHOP_STATUS", value=status
        return Result.success();
    }

    /**
     * 查询店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getStatus(){
        Integer status =(Integer)redisTemplate.opsForValue().get(KEY);// 在redis数据库中获取店铺营业状态，获取key="SHOP_STATUS"的value
        log.info("查询店铺营业状态为：{}", status == 1 ? "营业中":"打烊中");
        return Result.success(status); //返回给前端
    }

}
