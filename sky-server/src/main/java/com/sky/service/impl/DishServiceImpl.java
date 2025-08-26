package com.sky.service.impl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDTO
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish); // 拷贝属性

        // 向菜品表插入一条数据
        dishMapper.insert(dish);

        //获取insert语句产生的菜品主键值id
        Long dishId = dish.getId();

        // 向口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){

            // 批量赋值菜品id给每个flavor
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });

            dishFlavorMapper.insertBatch(flavors); //批量插入
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        // 判断：当前菜品能否删除：菜品状态不能是启售状态
        for(Long id : ids){
            Dish dish = dishMapper.getById(id); //遍历ids集合中的每个id，查询对应的菜品数据

            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE); // 启售中的菜品不能被删除，提示信息返回给前端
            }
        }

        // 判断：当前菜品能否删除：菜品不能关联套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);  //获取所有被删除的菜品关联的套餐id

        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL); //如果不为空，说明被删除的菜品存在关联的套餐，
                                                                                               // 则该菜品不能被删除，提示信息返回给前端
        }

//        // 删除菜品表中的数据
//        for(Long id : ids){
//            dishMapper.deleteById(id);
//            dishFlavorMapper.deleteByDishId(id); //删除菜品关联的口味数据
//        }

        //优化：根据菜品id批量删除菜品数据
        dishMapper.deleteByIds(ids);
        // 优化：根据菜品id批量删除菜品关联的口味数据
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @Transactional
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 根据id查询菜品数据
        Dish dish = dishMapper.getById(id);

        // 根据id查询对应的口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //将查询到的所有数据封装到VO,并拷贝dish数据到VO,再设置VO的菜品口味数据
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO); // dish -> dishVO
        dishVO.setFlavors(dishFlavors); // dishFlavors -> dishVO.flavors

        return dishVO;
    }

    /**
     * 修改菜品，也可能修改口味数据
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {

        // 拷贝属性到Dish对象，用于实现修改菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 修改菜品基本信息
        dishMapper.update(dish);

        //口味修改：先实现删除口味操作，再实现添加口味操作
        dishFlavorMapper.deleteByDishId(dishDTO.getId()); //先删除

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId()); // 如果flavor有关联的菜品id，则设置菜品id
            });
        }
        dishFlavorMapper.insertBatch(flavors); // 再批量添加
    }
}
