package com.yamlConversor.header;

public class HeaderGenerator {

	public static String generateHeader() {
		StringBuilder sb = new StringBuilder();

		sb.append("swagger: 2.0\ninfo:\ndescription:\"\"\nversion:1.0.0\ntitle:Your project\nbasePath: /v2\npaths:");

		return sb.toString();

	}

}
