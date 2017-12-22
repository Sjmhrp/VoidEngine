package sjmhrp.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;

import sjmhrp.io.Log;
import sjmhrp.io.NativesLoader;

public class Launcher {

	public static void main(String[] args) {
		String natives = NativesLoader.extractNatives();
		try {
			String mainClass = "sjmhrp.core.Main";
			ArrayList<String> arguments = new ArrayList<String>();
			String javaPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
			arguments.add(javaPath);
			for (int i = 0; i < args.length; i++) {
				arguments.add(args[i]);
			}
			arguments.add("-cp");
			arguments.add(System.getProperty("java.class.path"));
			arguments.add("-Djava.library.path="+natives);
			arguments.add(mainClass);
			ProcessBuilder processBuilder = new ProcessBuilder(arguments);
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			writeConsoleOutput(process);
			process.waitFor();
		} catch(Exception e) {
			Log.printError(e);
		} finally {
			NativesLoader.deleteNatives();
		}
	}
	
	public static void writeConsoleOutput(Process process) throws Exception {
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}
	
	public static File getCodeSourceLocation() {
		try {
			return new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			Log.printError(e);
		}
		return null;
	}
}