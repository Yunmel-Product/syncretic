package com.yunmel.extreme.db;

import java.io.Serializable;

public interface Factory {

	public <T> T get(Serializable id, Class<T> clazz);

	public <T> Object save(Object obj);

	public <T> void delete(Serializable id, Class<T> clazz);

	public void update(Serializable id, Object obj);

	public <T> long size(Class<T> clazz);
}