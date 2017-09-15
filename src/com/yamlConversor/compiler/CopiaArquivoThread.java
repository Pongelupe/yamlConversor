package com.yamlConversor.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CopiaArquivoThread extends Thread {

	private File arquivo;
	private Path output;
	private File arquivoOutput;
	
	private static final CopyOption[] OPTIONS = new CopyOption[] {
			StandardCopyOption.REPLACE_EXISTING,
			StandardCopyOption.COPY_ATTRIBUTES };

	public CopiaArquivoThread(File arquivo, Path output) {
		this.arquivo = arquivo;
		this.output = output;
	}

	@Override
	public void run() {
		try {
			Path in = Paths.get(arquivo.toURI());
			
			Files.copy(in, output, OPTIONS);
			
			arquivoOutput = output.toFile();
			
			while(!arquivoOutput.exists() || arquivoOutput.length() < arquivo.length());
			
		} catch (IOException e) {
			System.out.println("Erro ao copiar arquivo");
		}
	}

	public File getArquivoOutput() {
		return arquivoOutput;
	}
}
