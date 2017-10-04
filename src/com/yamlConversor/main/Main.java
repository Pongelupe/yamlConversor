package com.yamlConversor.main;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.yamlConversor.compiler.CompileFromInput;
import com.yamlConversor.header.HeaderGenerator;
import com.yamlConversor.paths.YamlPathGenerator;

import net.lingala.zip4j.core.ZipFile;

public class Main {

	private static final String PATH_SRC = "src/com/yamlConversor/";

	public static void main(String... args) {
		try {
			// the first argument should be a path from compressed(zip) file
			String pathSourceZip = args[0];
			StringBuilder yaml = new StringBuilder();

			ExecutorService pool = Executors.newCachedThreadPool();
			Future<String> headerFuture = pool.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					return HeaderGenerator.generateHeader();
				}
			});

			unzip(pathSourceZip, PATH_SRC);

			File folder = new File(PATH_SRC + "classes");
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
			Future<String> pathsFuture = pool.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					StringBuilder pathBuilder = new StringBuilder();

					File pathsFile = files.stream().filter(f -> f.getName().endsWith(".txt")).findFirst()
							.orElseGet(null);
					pathsFile.deleteOnExit();

					Queue<String> generatedPaths = YamlPathGenerator.generatePaths(pathsFile.getPath());
					generatedPaths.forEach(pathBuilder::append);

					return pathBuilder.toString();
				}
			});

			Future<String> definitionsFuture = pool.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					CompileFromInput compiler = new CompileFromInput(
							files.stream().filter(f -> f.getName().endsWith(".java")).collect(Collectors.toList()));
					return compiler.compile();
				}
			});
			yaml.append(headerFuture.get());
			yaml.append(pathsFuture.get());
			yaml.append(definitionsFuture.get());

			generateYaml(yaml.toString());
			clean(files, folder);

			pool.shutdown();

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

	private static void clean(ArrayList<File> files, File folder) {
		files.forEach(f -> f.delete());
		folder.delete();

		File binFolder = new File("bin/com/yamlConversor/classes");
		ArrayList<File> binFiles = new ArrayList<File>(Arrays.asList(binFolder.listFiles()));
		binFiles.forEach(f -> f.delete());
		binFolder.delete();
	}
}
