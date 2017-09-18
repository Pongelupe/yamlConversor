package com.yamlConversor.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
			StringBuilder sb = new StringBuilder();
			// the first argument should be a path from compressed(zip) file
			// String pathSourceZip = args[0];
			String pathSourceZip = "C:\\classes.zip";

			// the second argument should be the path where Yaml Conversor is
			// String pathDestZip = args[1];
			String pathDestZip = "C:\\workspaces\\BravosMobile\\yamlConversor\\src\\com\\yamlConversor\\";

			String header = HeaderGenerator.generateHeader();
			sb.append(header);
			unzip(pathSourceZip, pathDestZip);

			File folder = new File("C:\\workspaces\\BravosMobile\\yamlConversor\\src\\com\\yamlConversor\\classes");
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles()));
			CompileFromInput compiler = new CompileFromInput(
					files.stream().filter(f -> f.getName().endsWith(".java")).collect(Collectors.toList()));

			File tempFile = File.createTempFile("test", "txt");
			tempFile.deleteOnExit();
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
			bw.write("/fazerLogin fazerLoginChamada fazerLoginResposta\n/buscar buscarChamada buscarResposta");
			bw.close();

			Queue<String> generatedPaths = YamlPathGenerator.generatePaths(tempFile.getPath());
			generatedPaths.forEach(sb::append);

			sb.append(compiler.compile());
			System.out.println(sb.toString());
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

}
