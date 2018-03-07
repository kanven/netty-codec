package com.kanven.netty.utils;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * Properties属性值获取工具类
 * 
 * @author kanven
 *
 */
public class PropertiesHandler {

	private Properties properties;

	public PropertiesHandler(Properties properties) {
		this.properties = properties;
	}

	public String getString(String key, String defaultValue) {
		return getString(properties, key, defaultValue);
	}

	public String getString(String key) {
		return getString(key, "");
	}

	public int getInt(String key, int defaultValue) {
		return getInt(properties, key, defaultValue);
	}

	public int getInt(String key) {
		return getInt(properties, key);
	}

	public float getFloat(String key) {
		return getFloat(properties, key);
	}

	public float getFloat(String key, float defaultValue) {
		return getFloat(properties, key, defaultValue);
	}

	public double getDouble(String key) {
		return getDouble(key);
	}

	public double getDouble(String key, double defaultValue) {
		return getDouble(key, defaultValue);
	}

	public static double getDouble(Properties properties, String key) {
		String v = getString(properties, key);
		return Double.parseDouble(v);
	}

	public static double getDouble(Properties properties, String key, double defaultValue) {
		try {
			return getDouble(properties, key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float getFloat(Properties properties, String key) {
		String v = getString(properties, key);
		return Float.parseFloat(v);
	}

	public static float getFloat(Properties properties, String key, float defalutValue) {
		try {
			return getFloat(properties, key);
		} catch (NullPointerException e) {
			return defalutValue;
		}
	}

	public static int getInt(Properties properties, String key) {
		String v = getValue(properties, key);
		return Integer.parseInt(v);
	}

	public static int getInt(Properties properties, String key, int defaultValue) {
		try {
			return getInt(properties, key);
		} catch (NullPointerException e) {
			return defaultValue;
		}
	}

	public static String getString(Properties properties, String key) {
		return getValue(properties, key);
	}

	public static String getString(Properties properties, String key, String defaultValue) {
		try {
			return getValue(properties, key);
		} catch (NullPointerException e) {
			return defaultValue;
		}
	}

	private static String getValue(Properties properties, String key) {
		String v = properties.getProperty(key);
		if (StringUtils.isBlank(v)) {
			throw new NullPointerException(String.format("the value of the key(%s) is null", key));
		}
		return v;
	}

}
