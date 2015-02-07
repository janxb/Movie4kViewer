package Movie4kViewer;

import org.apache.pivot.util.Base64;

public class Settings {
	
	public static int window_width = 430;
	public static int window_height = 500;
	
	/* Proxy */
	public static boolean use_proxy = false;
	public static boolean use_proxy_auth = true;

	public static String proxy_host = "127.0.0.1";
	public static int proxy_port = 8080;
	public static String proxy_user = "janxb";
	public static String proxy_pass = "ddd";

	public static String loginstring = new String(Base64.encode((proxy_user + ":" + proxy_pass).getBytes()));
}
