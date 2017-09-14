package com.yamlConversor.paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class YamlPathGenerator {

	private static Queue<String> paths = new LinkedList<String>();

	public static void generatePaths(Class<?> clazz) {
		ArrayList<Object> enums = new ArrayList<Object>(Arrays.asList(clazz.getEnumConstants()));

		for (Object object : enums) {
			System.out.println(object.toString());
			paths.add(object.toString());
		}
	}

	public static void generatePath(String path, String requestType, String objDefinitionRequest,
			String objDefinitionResponse) {
		StringBuilder sb = new StringBuilder();
		// Request
		sb.append(path + ":\n" + requestType
				+ ":\ndescription:\"\"\nconsumes:\n-application/json\nproduces:\n-application/json\n"
				+ "parameters:\n-in: body\ndescription:\"\"\nrequeried: true\nschema:\n$ref:\"#/definitions/"
				+ objDefinitionRequest + "\"\n");

		// response
		sb.append("responses:\n\"200\":\n$ref:\"#/definition/" + objDefinitionResponse + "\"");

		System.out.println(sb.toString());

	}

}
