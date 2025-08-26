package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据, SQL语句写在xml里
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT) // 自动填充createTime, updateTime, createUser, updateUser,这样service层就不用补充这些数据了
    void insert(Dish dish);
}
