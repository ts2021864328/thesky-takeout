package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    /**
     * 菜品分页查询, 动态SQL写在xml里
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品和口味数据
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据id删除菜品
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据ids批量删除菜品,动态SQL写在xml里
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id修改菜品, 动态SQL语句写在xml里
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE) //  自动填充updateTime, updateUser,这样service层就不用补充这些数据了
    void update(Dish dish);

    /**
     * 根据条件查询菜品数据, 动态SQL写在xml里
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);
}
