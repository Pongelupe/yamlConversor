package com.yamlConversor.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CompileFromInput {

	private final String packageClasses = "com.yamlConversor.classes";
	private File file;

	public CompileFromInput(File file) {
		this.file = file;
	}

	public void compile() throws Exception {
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

		List<String> optionList = new ArrayList<String>();
		optionList.add("-classpath");
		optionList.add(System.getProperty("java.class.path") + ";dist/InlineCompiler.jar");

		Iterable<? extends JavaFileObject> compilationUnit = fileManager
				.getJavaFileObjectsFromFiles(Arrays.asList(file));
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null,
				compilationUnit);

		if (task.call()) {
			/**
			 * Load and execute
			 *************************************************************************************************/
			System.out.println("Yipe");
			System.out.println(getPackage());
			setPackage();
			System.out.println(getPackage());
			// Create a new custom class loader, pointing to the directory that
			// contains the compiled
			// classes, this should point to the top of the package structure!
			URLClassLoader classLoader = new URLClassLoader(
					new URL[] { new File(System.getProperty("user.dir")).toURI().toURL() });
			// Load the class from the classloader by name....
			Class<?> loadedClass = classLoader.loadClass(getPackage() + file.getName().replace(".java", ""));
			// Create a new instance...
			Object obj = loadedClass.newInstance();
			// Santity check
			System.out.println(obj);
			classLoader.close();

			/*************************************************************************************************
			 * Load and execute
			 **/
		} else {
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(),
						diagnostic.getSource().toUri());
			}
		}
		fileManager.close();

	}

	private String getPackage() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String packageName = br.readLine();
		br.close();
		packageName = packageName.replace("package", "").trim();
		return packageName.replace(";", ".");
	}

	private void setPackage() throws Exception {

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

		file.createNewFile();
		file.renameTo(temp);

	}

}
