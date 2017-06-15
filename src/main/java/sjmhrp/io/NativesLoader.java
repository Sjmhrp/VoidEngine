package sjmhrp.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class NativesLoader {
	private static File tmp;
	    
    public static String extractNatives() {
        try {
            tmp = File.createTempFile("Void","Engine");
            tmp.delete();
            tmp.mkdir();
            tmp.deleteOnExit();
            String os = System.getProperty("os.name").toLowerCase();
            if(os.contains("win")) {
            	if(System.getProperty("sun.arch.data.model").equals("64")) {
	            	extractNative("lwjgl64.dll",tmp);
                    extractNative("OpenAL64.dll",tmp);
                    extractNative("jinput-dx8_64.dll",tmp);
                    extractNative("jinput-raw_64.dll",tmp);
                } else {
                	extractNative("lwjgl.dll",tmp);
                    extractNative("OpenAL32.dll",tmp);
                    extractNative("jinput-dx8.dll",tmp);
                    extractNative("jinput-raw.dll",tmp);	                    	
                }
            } else if(os.contains("mac")) {
            	extractNative("liblwjgl.dylib",tmp);
                extractNative("openal.dylib",tmp);
                extractNative("libjinput-osx.dylib",tmp);
            } else if(os.contains("nix")||os.contains("nux")||os.contains("aix")) {
            	if(System.getProperty("sun.arch.data.model").equals("64")) {
            		extractNative("liblwjgl64.so",tmp);
                    extractNative("libopenal64.so",tmp);
                    extractNative("libjinput-linux64.so",tmp);
            	} else {
            		extractNative("liblwjgl.so",tmp);
                    extractNative("libopenal.so",tmp);
                    extractNative("libjinput-linux.so",tmp);
            	}
            }
            return tmp.getAbsolutePath();
        } catch (Exception e) {
            Log.printError(e);
        }
        return null;
    }

    private static void extractNative(String path, File dir) {
    	InputStream is = Class.class.getResourceAsStream("/"+path);
    	String[] parts = ("/"+path).replaceAll("\\\\", "/").split("/");
    	String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
    	try {
    		File tmp = new File(dir, filename);
    		tmp.deleteOnExit();
    		FileOutputStream os = new FileOutputStream(tmp);
    		byte[] buffer = new byte[1024];
    		int readBytes;	
    		try {
    			while ((readBytes = is.read(buffer)) != -1) {
    				os.write(buffer, 0, readBytes);
    			}
    		} finally {
    			os.close();
    			is.close();
    		}
    	} catch (Exception e) {
    		Log.printError(e);
    	}
    }
    
    public static void deleteNatives() {
    	for (File f : tmp.listFiles()) {
    		f.delete();
    	}
    	tmp.delete();
    }    
}