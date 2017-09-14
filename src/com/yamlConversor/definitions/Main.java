package com.yamlConversor.definitions;

import java.io.File;

import com.yamlConversor.compiler.CompileFromInput;

public class Main {

	public static void main(String[] args) {
		try {
			CompileFromInput compiler = new CompileFromInput(
					new File("C:\\Projetos\\LDMBravos\\oficial\\LDMLib\\src\\linx\\lib\\model\\Filial.java"));
			compiler.compile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
