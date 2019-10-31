package com.brandslink.cloud.common.entity;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 分页处理
 */
public class Page<T> implements Serializable {

	private PageInfo<T> pageInfo;

	private Page() {
	}

	public Page(PageInfo<T> pageInfo) {
		this.pageInfo = pageInfo;
	}

	public Page(List<T> list) {
		this.pageInfo = new PageInfo(list);
	}

	public PageInfo<T> getPageInfo() {
		return pageInfo;
	}

	public static void builder(String currPage, String row) {
		builder(currPage, row, true);
	}

	public static void builder(int currPage, int row) {
		builder(currPage, row, true);
	}

	/**
	 * 分页
	 * 
	 * @param currPage 当前页
	 * @param row      每页显示行数
	 * @param isCount  是否查询总条数
	 */
	public static void builder(String currPage, String row, boolean isCount) {
		try {
			int nowPage = Integer.valueOf(currPage) <= 0 ? 1 : Integer.valueOf(currPage);
			int rowNum = Integer.valueOf(row) <= 0 ? 1 : Integer.valueOf(row);
			startPage(nowPage, rowNum, isCount);
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
		}
	}

	/**
	 * 分页
	 * 
	 * @param currPage 当前页
	 * @param row      每页显示行数
	 * @param isCount  是否查询总条数
	 */
	public static void builder(int currPage, int row, boolean isCount) {
		int nowPage = currPage <= 0 ? 1 : currPage;
		int rowNum = row <= 0 ? 1 : row;
		startPage(nowPage, rowNum, isCount);
	}

	private static void startPage(int nowPage, int rowNum, boolean isCount) {
		PageHelper.startPage(nowPage, rowNum, isCount);
	}

	/**
	 * 分页
	 * 
	 * @param currPage 当前页
	 * @param row      每页显示行数
	 * @param orderBy  排序
	 */
	public static void startPage(String currPage, String row, String orderBy) {
		try {
			int nowPage = Integer.valueOf(currPage) <= 0 ? 1 : Integer.valueOf(currPage);
			int rowNum = Integer.valueOf(row) <= 0 ? 1 : Integer.valueOf(row);
			if (StringUtils.isNotBlank(orderBy))
				PageHelper.startPage(nowPage, rowNum, orderBy);
			else
				startPage(nowPage, rowNum, true);
		} catch (Exception e) {
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
		}
	}

}
