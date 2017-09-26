package com.yamlConversor.paths;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;

public class YamlPathGenerator {

	private static final String yamlTab = "    ";
	private static Queue<Path> paths = new LinkedList<Path>();
	private static Queue<String> generatedPaths = new LinkedList<String>();

	public static Queue<String> generatePaths(String path) throws Exception {
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
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
		sb.append("\n" + path.getPath() + ":\n" + path.getRequestType() + ":\ndescription: \"\"\nconsumes: \n" + yamlTab
				+ "-application/json\nproduces: \n" + yamlTab + "-application/json\n"
				+ "parameters:\n-in: body\ndescription: \"\"\nrequeried: true\nschema: \n$ref: \"#/definitions/"
				+ path.getObjDefinitionRequest() + "\"\n");

		// response
		sb.append("responses:\n\"200\": \n$ref: \"#/definition/" + path.getObjDefinitionResponse() + "\"");

		return sb.toString();

	}

}
