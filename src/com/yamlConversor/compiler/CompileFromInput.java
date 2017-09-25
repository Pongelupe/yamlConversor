package com.yamlConversor.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
		filesToBeCompiled.forEach(this::prepareFile);
	}

	public String compile() {
		Path dest = Paths.get("bin");

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
					System.exit(0);
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Yipe");
			System.out.println("Arquivos compilados, recome�e a aplica��o");
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

	private void prepareFile(File file) {

		try {
			File temp = File.createTempFile(file.getPath().replace(".java", "temp.java"), "");
			temp.deleteOnExit();
			BufferedReader br = new BufferedReader(new FileReader(file));
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));

			setPackage(br, bw);
			setClass(br, bw);
			setFields(br, bw);

			br.close();
			bw.flush();
			bw.close();

			Files.move(temp.toPath(), file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setClass(BufferedReader br, BufferedWriter bw) throws IOException {
		String line;
		String clazzLine = "";
		boolean flagClass = true;
		while ((line = br.readLine()) != null && flagClass) {
			flagClass = !line.contains("class");
			if (!flagClass) {
				if (line.contains("implements")) {
					clazzLine = line.substring(0, line.indexOf("implements"));
					bw.write(clazzLine + "{\n");
				} else
					bw.write(line + "\n");
			}
		}

	}

	private void setFields(BufferedReader br, BufferedWriter bw) throws IOException {
		String line;
		while (((line = br.readLine()) != null)) {
			if ((line.contains("new") || line.contains("private"))
					&& (!line.contains("{") || !line.contains("}") || !line.contains("return") || !line.contains("()")))
				bw.write(line + "\n");
		}
		bw.write("}");
	}

	private void setPackage(BufferedReader br, BufferedWriter bw) throws IOException {
		String line;
		boolean flagPackage = true;

		while (((line = br.readLine()) != null) && flagPackage) {
			flagPackage = !line.contains("package");
			if (!flagPackage)
				bw.write("package " + packageClasses + ";\n");
		}
	}

}
