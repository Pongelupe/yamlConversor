package com.yamlConversor.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Queue;

import com.yamlConversor.header.HeaderGenerator;
import com.yamlConversor.paths.YamlPathGenerator;

public class Main {

	public static void main(String[] args) {
		try {
			// CompileFromInput compiler = new CompileFromInput(
			// new
			// File("C:\\Projetos\\LDMBravos\\oficial\\LDMLib\\src\\linx\\lib\\model\\Filial.java"));
			// compiler.compile();
			System.out.println(HeaderGenerator.generateHeader());
			// Path path = new Path("/buscarCliente",
			// "buscarClienteCheckinChamada", "buscarClienteCheckinResposta");
			// System.out.println(YamlPathGenerator.generatePath(path));
			File tempFile = File.createTempFile("test", "txt");
			tempFile.deleteOnExit();
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
			bw.write("/fazerLogin fazerLoginChamada fazerLoginResposta\n/buscar buscarChamada buscarResposta");
			bw.close();
			Queue<String> generatedPaths = YamlPathGenerator.generatePaths(tempFile.getPath());
			for (String string : generatedPaths) {
				System.out.println(string);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
