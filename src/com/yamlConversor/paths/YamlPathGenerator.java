package com.yamlConversor.paths;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;

import com.yamlConversor.util.TabHelper;

public class YamlPathGenerator implements TabHelper {

	private static Queue<Path> paths = new LinkedList<Path>();
	private static Queue<String> generatedPaths = new LinkedList<String>();

	public static Queue<String> generatePaths(String path) throws Exception {
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.isEmpty())
				paths.offer(getPath(line));
		}
		br.close();

		paths.forEach(p -> generatedPaths.offer(generatePath(p)));

		generatedPaths.offer("\ndefinitions: \n");

		return generatedPaths;

	}

	private static Path getPath(String line) {
		String[] splitedPathObj = line.split("\\s+");
		String path = splitedPathObj[0];
		String request = splitedPathObj[1];
		String response = splitedPathObj[2];

		return new Path(path, request, response);
	}

	public static String generatePath(Path path) {
		StringBuilder sb = new StringBuilder();
		// Request

		sb.append("\n" + yamlFullTab + path.getPath() + ":\n" + repeat(4) + path.getRequestType() + ":\n" + repeat(6)
				+ "description: \"\"\n" + repeat(6) + "consumes: \n" + repeat(7) + "- application/json\n" + repeat(6)
				+ "produces: \n" + repeat(7) + "- application/json\n" + repeat(6) + "parameters:\n" + repeat(7)
				+ "- in: body\n" + repeat(8) + "name: body\n" + repeat(8) + "schema: \n" + repeat(10)
				+ "$ref: \"#/definitions/" + firstLetterToUpperCase(path.getObjDefinitionRequest()) + "\"\n");

		// response
		sb.append(repeat(6) + "responses:\n" + repeat(8) + "\"200\": \n" + repeat(9) + "description: \"\"\n" + repeat(9)
				+ "schema: \n" + repeat(10) + "$ref: \"#/definitions/"
				+ firstLetterToUpperCase(path.getObjDefinitionResponse()) + "\"");

		return sb.toString();

	}

	private static String firstLetterToUpperCase(String str) {
		char firstChar = Character.toUpperCase(str.charAt(0));
		return firstChar + str.substring(1);
	}

	private static String repeat(int count) {
		return TabHelper.repeat(count);
	}

}
