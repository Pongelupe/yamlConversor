package com.yamlConversor.util;

public interface TabHelper {

	public static final String yamlFullTab = "    ";
	public static final String yamlHalfTab = "  ";

	public static String repeat(int count, String with) {
		return new String(new char[count]).replace("\0", with);
	}

	public static String repeat(int count) {
		return TabHelper.repeat(count, yamlHalfTab);
	}
}
