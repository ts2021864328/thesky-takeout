package com.sky.controller.admin;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@Slf4j
@RequestMapping("/admin/dish")
@RestController
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation("添加菜品")
    public Result add(@RequestBody DishDTO dishDTO){
        log.info("添加菜品: {}", dishDTO);

        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}" , dishPageQueryDTO);

        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品：{}" , ids);
        dishService.deleteBatch(ids); // 批量删除
        return Result.success();
    }

    /**
     * 根据id查询菜品,查询回显菜品信息，包括封装在DishVO里的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品,查询回显菜品信息，包括封装在DishVO里的口味信息")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}" , id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}" , dishDTO);

        dishService.updateWithFlavor(dishDTO);

        return Result.success();
    }
}
