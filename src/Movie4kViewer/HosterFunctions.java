package Movie4kViewer;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HosterFunctions {

	public static Document download_webpage(String url) throws IOException {
		Document result = null;
		try {
			result = Jsoup
				.connect(url)
				.userAgent(
						"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
				.timeout(20000).get();
		} catch (IllegalArgumentException e){
			result = new Document("");
		}
		return result;
	}

	public static String validate_bitshare(String hoster_url) {
		Document doc = null;
		try {

			doc = download_webpage(hoster_url);
			Element single_hoster_heading = doc.select("h1").first();
			if (!single_hoster_heading.html().contains("Fehler - Datei nicht")) {
				return hoster_url;
			}

		} catch (IOException e) {}
		return null;
	}

	public static String validate_streamcloud(String hoster_url) {
		Document doc = null;
		try {
			doc = download_webpage(hoster_url);
			Element single_hoster_heading = doc.select("div.header.page h1").first();
			if (single_hoster_heading.html().contains("Watch video:")
					|| single_hoster_heading.html().contains("Video anschauen:")) {
				return hoster_url;
			}
		} catch (Exception e) {}
		return null;
	}

	public static String validate_sockshare(String hoster_url) {
		Document doc = null;
		try {
			doc = download_webpage(hoster_url);
			Element deletion_message = doc.select("div#deleted").first();
			if (deletion_message == null) {
				return hoster_url.replace("embed", "file");
				//return "http://www.sockshare.com" + doc.select("div#file_title a").first().attr("href");
			}
		} catch (Exception e) {}
		return null;
	}

	public static String validate_putlocker(String hoster_url) {
		Document doc = null;
		try {
			doc = download_webpage(hoster_url);
			Element deletion_message = doc.select("div.sad_face_image").first();
			if (deletion_message == null) {
				return hoster_url;
			}
		} catch (Exception e) {}
		return null;
	}

	public static String validate_movshare(String hoster_url) {
		Document doc = null;
		try {
			String movshare_base_url = "http://www.movshare.net/video/";
			String movie_id = hoster_url.split("v=")[1].split("&")[0];
			String full_movshare_url = movshare_base_url + movie_id;
			doc = download_webpage(full_movshare_url);
			Element error_message = doc.select("p.error_message").first();
			if (error_message == null) {
				return hoster_url;
			}

		} catch (Exception e) {}
		return null;
	}

	public static String validate_nowvideo(String hoster_url) {
		Document doc = null;
		try {
			String nowvideo_base_url = "http://www.nowvideo.sx/video/";
			String movie_id = hoster_url.split("v=")[1].split("&")[0];
			String full_nowvideo_url = nowvideo_base_url + movie_id;
			doc = download_webpage(full_nowvideo_url);
			Element download_button = doc.select("div#content_player").first();
			if (download_button != null) {
				return hoster_url;
			}
		} catch (Exception e) {}
		return null;
	}
}
