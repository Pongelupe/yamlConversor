package com.yamlConversor.main;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.stream.Collectors;

import com.yamlConversor.compiler.CompileFromInput;
import com.yamlConversor.header.HeaderGenerator;
import com.yamlConversor.paths.YamlPathGenerator;

import net.lingala.zip4j.core.ZipFile;

public class Main {

	public static void main(String... args) {
		try {
			// the first argument should be a path from compressed(zip) file
			String pathSourceZip = args[0];

			StringBuilder yaml = new StringBuilder();
			String header = HeaderGenerator.generateHeader();
			yaml.append(header);
			unzip(pathSourceZip, "src/com/yamlConversor/");

			File folder = new File("src/com/yamlConversor/classes");
			folder.deleteOnExit();
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
			CompileFromInput compiler = new CompileFromInput(
					files.stream().filter(f -> f.getName().endsWith(".java")).collect(Collectors.toList()));

			File pathsFile = files.stream().filter(f -> f.getName().endsWith(".txt")).findFirst().orElseGet(null);
			pathsFile.deleteOnExit();

			Queue<String> generatedPaths = YamlPathGenerator.generatePaths(pathsFile.getPath());
			generatedPaths.forEach(yaml::append);

			yaml.append(compiler.compile());

			generateYaml(yaml.toString());

			System.out.println(yaml.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void unzip(String source, String dest) throws Exception {
		ZipFile zipFile = new ZipFile(source);
		if (zipFile.isEncrypted())
			throw new RuntimeException("O arquivo zip está criptografado");
		zipFile.extractAll(dest);
	}

	private static void generateYaml(String yaml) throws Exception {

		PrintWriter writer = new PrintWriter(new File("yaml/swagger.yaml"), "UTF-8");
		writer.println(yaml);
		writer.close();

		System.out.println(yaml);
	}

}
