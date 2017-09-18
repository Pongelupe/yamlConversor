package com.yamlConversor.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.yamlConversor.definitions.YamlObjectGenerator;

public class CompileFromInput {

	private final String packageClasses = "com.yamlConversor.classes";
	private final ArrayList<File> filesToBeCompiled = new ArrayList<File>();

	public CompileFromInput(List<File> files) {
		filesToBeCompiled.addAll(files);
		filesToBeCompiled.forEach(this::setPackage);
	}

	public String compile() {
		Path dest = Paths.get("C:\\workspaces\\BravosMobile\\yamlConversor\\bin");

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

		List<String> optionList = new ArrayList<String>();
		optionList.add("-classpath");
		optionList.add(System.getProperty("java.class.path") + ";dist/InlineCompiler.jar");
		optionList.add("-d");
		optionList.add(dest.toString());

		Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(filesToBeCompiled);
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
				compilationUnit);

		try {
			if (task.call()) {
				/**
				 * Load and execute
				 *************************************************************************************************/
				// Create a new custom class loader, pointing to the directory
				// that
				// contains the compiled
				// classes, this should point to the top of the package
				// structure!

				StringBuilder sb = new StringBuilder();

				URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File("").toURI().toURL() });
				for (File file : filesToBeCompiled) {
					Class<?> loadedClass = classLoader
							.loadClass(getPackage(file) + file.getName().replace(".java", ""));
					Object obj = loadedClass.newInstance();
					// Santity check
					sb.append(YamlObjectGenerator.generateDefinitionsYaml(obj) + "\n");
				}
				classLoader.close();
				fileManager.close();
				return sb.toString();

				/*************************************************************************************************
				 * Load and execute
				 **/
			} else {
				for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
					System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(),
							diagnostic.getSource().toUri());
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Yipe");
			System.out.println("Arquivos compilados, recomeçe a aplicação");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private String getPackage(File file) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String packageName = br.readLine();
		br.close();
		packageName = packageName.replace("package", "").trim();
		return packageName.replace(";", ".");
	}

	private void setPackage(File file) {

		try {
			File temp = File.createTempFile(file.getPath().replace(".java", "temp.java"), "");
			temp.deleteOnExit();
			BufferedReader br = new BufferedReader(new FileReader(file));
			br.readLine();
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
			bw.write("package " + packageClasses + ";");
			String line;
			while ((line = br.readLine()) != null) {
				bw.write(line + "\n");
			}
			br.close();
			bw.flush();
			bw.close();

			Files.move(temp.toPath(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
		}

	}

}
