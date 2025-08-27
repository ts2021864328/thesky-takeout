package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端的店铺管理
 * 因为店铺状态要么是1，要么是0，而数据形式是key-value, 所以使用Redis数据库，就没必要使用MySQL数据库（表格）
 */
@Slf4j
@RequestMapping("/user/shop")
@RestController("userShopController") //取名为userShopcontroller，用于和admin的controller区分
@Api(tags = "店铺相关接口")
public class ShopController {

    public static final String KEY  = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

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
