package com.percy.interview.othelloe.main.utils;

public class ReflectionUtils {
	
	
	public static <E> E getInstanceofInterface(String clazz, Class<E> type) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> cls = Class.forName(clazz);
		if(!type.isAssignableFrom(cls)) {
			throw new IllegalArgumentException();
		}
		return (E) cls.newInstance();
	}
}
