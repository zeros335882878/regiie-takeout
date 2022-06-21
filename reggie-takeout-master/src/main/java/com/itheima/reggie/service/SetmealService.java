package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.vo.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 添加套餐
     *
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);


    public void removeWithDish(List<Long> ids);
}