package com.brandslink.cloud.common.service.impl;

import com.github.pagehelper.PageInfo;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.List;


/**
 * 基础服务实现类
 * */
public abstract class BaseServiceImpl<T> implements BaseService<T> {
	
	@Autowired
	private BaseMapper<T> baseMapper;


	@Override
	public int deleteByPrimaryKey(Long primaryKey) {
		return baseMapper.deleteByPrimaryKey(primaryKey);
	}

	@Override
	public int insert(T t) {
		return baseMapper.insert(t);
	}

	@Override
	public int insertSelective(T t) {
		return baseMapper.insertSelective(t);
	}

	@Override
	public T selectByPrimaryKey(Long primaryKey) {
		return baseMapper.selectByPrimaryKey(primaryKey);
	}

	@Override
	public int updateByPrimaryKeySelective(T t) {
		try {
			Class cl = t.getClass();
			//获取id
			Field f=cl.getDeclaredField("id");
			f.setAccessible(true);
			Long id=(Long)f.get(t);
			//获取版本号version
			Field f1 = cl.getDeclaredField("version");
			f1.setAccessible(true);
			T old = selectByPrimaryKey(id);
			if (old == null) return 0;
			Class c2 = old.getClass();
			Field f2 = c2.getDeclaredField("version");
			f2.setAccessible(true);
			Long version = (Long)f2.get(old);
			f1.set(t, version);
			return baseMapper.updateByPrimaryKeySelective(t);
		} catch (Exception e) {
			return baseMapper.updateByPrimaryKeySelective(t);
		}
	}

	@Override
	public int updateByPrimaryKey(T t) throws NoSuchFieldException, IllegalAccessException {
		Class cl = t.getClass();
		//获取id
		Field f=cl.getDeclaredField("id");
		f.setAccessible(true);
		Long id=(Long)f.get(t);
		//获取版本号version
		Field f1 = cl.getDeclaredField("version");
		f1.setAccessible(true);
		T old = selectByPrimaryKey(id);
		if (old == null) return 0;
		Class c2 = old.getClass();
		Field f2 = c2.getDeclaredField("version");
		f2.setAccessible(true);
		Long version = (Long)f2.get(old);
		f1.set(t, version);
		return baseMapper.updateByPrimaryKey(t);
	}

	@Override
	public Page<T> page(T t) {
		List<T> list = baseMapper.page(t);
		return new Page(list);
	}

}
