package com.yunmel.extreme.db;

import java.util.List;
import java.util.Set;

public interface QueryFactory {
	public <T> Set<Object> queryList(String sql, Class<T> clazz, Object[] params);

	public <T> T queryOne(String sql, Class<T> clazz, Object[] params);

	public void delete(String sql, Object[] params);

	public void update(String sql, Object[] params);

	public void deleteBatch(String sql, List<Object[]> list);

	public void updateBatch(String sql, List<Object[]> list);

	public <T> void insertBatch(List<Object> list);

	public long size(String sql, Object[] params);

	public <T> Set<Object> pageNo(String sql, Class<T> clazz, Object[] params, int next);

	public <T> Set<Object> pageNo(Class<T> clazz, int next);
}
