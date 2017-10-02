package com.yamlConversor.header;

import com.yamlConversor.util.TabHelper;

public class HeaderGenerator implements TabHelper {

	public static String generateHeader() {
		StringBuilder sb = new StringBuilder();

		sb.append("swagger: \"2.0\"\ninfo:\n" + yamlFullTab + "description: \"\"\n" + yamlFullTab + "version: 1.0.0\n"
				+ yamlFullTab + "title: Your project\nbasePath: /\nschemes:\n" + yamlHalfTab + "- http\npaths: ");

		return sb.toString();

	}

}
