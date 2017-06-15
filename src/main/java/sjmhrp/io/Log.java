package sjmhrp.io;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public static void print(Object... s) {
		System.out.print("["+getDate()+"] [INFO]: " + s[0].toString());
	}

	public static void println(Object... s) {
		print(s[0]+"\n");
	}

	public static void warn(Object... s) {
		System.out.println("["+getDate()+"] [WARN]: " + s[0].toString());
	}

	public static void done() {
		System.out.print(" - Done\n");
	}

	public static void printError(Exception e) {
		e.printStackTrace();
	}

	static String getDate() {
		return dateFormat.format(new Date());
	}
}