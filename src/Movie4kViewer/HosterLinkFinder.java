package Movie4kViewer;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HosterLinkFinder {
	private String start_url = "";
	private String hoster_name = "";
	private ArrayList<String> movie_urls = new ArrayList<String>();
	private ArrayList<String> hoster_urls = new ArrayList<String>();
	private ArrayList<String> valid_hoster_urls = new ArrayList<String>();

	public HosterLinkFinder(String url) {
		this.start_url = url;

		if (Settings.use_proxy) {
			System.setProperty("http.proxyHost", Settings.proxy_host);
			System.setProperty("http.proxyPort", Settings.proxy_port + "");
		}

		if (Settings.use_proxy && Settings.use_proxy_auth) {
			Authenticator.setDefault(new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(Settings.proxy_user, Settings.proxy_pass.toCharArray());
				}
			});
		}
	}

	public String get_valid_movie_url() {
		if (!this.valid_hoster_urls.isEmpty()) {
			return this.valid_hoster_urls.get(0);
		} else
			return null;
	}

	public boolean online_hoster_found() {
		return (!this.valid_hoster_urls.isEmpty());
	}

	public boolean open_hoster_url_in_browser() {
		if (this.valid_hoster_urls.size() > 0) {
			try {
				open_browser(get_valid_movie_url());
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public void open_hoster_url_in_jdownloader() {
		if (this.get_valid_movie_url() != null) {
			try {
				Jsoup.connect("http://127.0.0.1:9666/flash/add").data("urls", this.get_valid_movie_url()).post();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void open_browser(String url) throws IOException, URISyntaxException {
		if (java.awt.Desktop.isDesktopSupported()) {
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

			if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
				java.net.URI uri = new java.net.URI(url);
				desktop.browse(uri);
			}
		}
	}

	private boolean get_movie_urls() {
		Document doc = null;

		String document_root = this.start_url.split("/")[2];

		try {
			doc = HosterFunctions.download_webpage(this.start_url);
			Elements select_values = doc.select("#details select option");

			String first_hoster_link = doc.select("#maincontent5 div iframe").first().attr("src");
			int link_count=1;
			while (first_hoster_link.contains("facebook.com") || first_hoster_link.contains("adobe.com")) {
				first_hoster_link = doc.select("#maincontent5 a[target=_blank]").get(link_count).attr("href");
				link_count++;
			}
			
			this.hoster_name = first_hoster_link.split("/")[2];

			if (select_values.size() < 1) {
				this.movie_urls.add(this.start_url);
			} else {
				for (Element value : select_values) {
					String found_url = "http://" + document_root + "/" + value.attr("value");
					this.movie_urls.add(found_url);
				}
			}

		} catch (IOException | NullPointerException e) {
			return false;
		}
		return true;
	}

	public boolean run_for_movies() {
		EventHandler.status("-- Getting Movie-URLs");
		this.get_movie_urls();
		
		int current_movie = 0;
		int movie_sum = this.movie_urls.size();
		Collections.shuffle(this.movie_urls);

		EventHandler.status("    Found " + movie_sum + " URLs");
		
		EventHandler.status("-- Checking Online-Status");
		EventHandler.status("    Filter: " + this.hoster_name);
		

		for (String movie_page : this.movie_urls) {
			current_movie++;

			String validated = this.validate_single_hoster_url(this.get_single_hoster_url(movie_page));
			
			if (validated == "NO_FILTER_AVAILABLE"){
				EventHandler.status("-- No Filter available!");
				return false;
			} else if (validated != null) {
				this.valid_hoster_urls.add(validated);
				EventHandler.status("    URL " + current_movie + "/" + movie_sum + " online!");
				EventHandler.status("-- Online Hoster-URL found!");
				return true;
			} else {
				EventHandler.status("    URL " + current_movie + "/" + movie_sum + " not online..");
			}
		}

		EventHandler.status("-- No Hoster-URL online!");
		return false;

	}

	private String get_single_hoster_url(String movie4k_url) {
		Document doc = null;
		try {

			doc = HosterFunctions.download_webpage(movie4k_url);
			String single_hoster_link = doc.select("#maincontent5 div iframe").first().attr("src");
			if (single_hoster_link.contains("facebook.com")) {
				int link_count=1;
				while (single_hoster_link.contains("facebook.com") || single_hoster_link.contains("adobe.com")) {
					single_hoster_link = doc.select("#maincontent5 a[target=_blank]").get(link_count).attr("href");
					link_count++;
				}	
			}
			this.hoster_urls.add(single_hoster_link);
			
			return single_hoster_link;
		} catch (IOException e) {}
		return "";
	}

	private String validate_single_hoster_url(String hoster_url) {
		String validated = "";

		switch (this.hoster_name) {
			case "bitshare.com":
				validated = HosterFunctions.validate_bitshare(hoster_url);
				break;
			case "streamcloud.eu":
				validated = HosterFunctions.validate_streamcloud(hoster_url);
				break;
			case "www.sockshare.com":
				validated = HosterFunctions.validate_sockshare(hoster_url);
				break;
			case "www.putlocker.com":
				validated = HosterFunctions.validate_putlocker(hoster_url);
				break;
			case "embed.movshare.net":
				validated = HosterFunctions.validate_movshare(hoster_url);
				break;
			case "shared.sx":
				validated = HosterFunctions.validate_sharedsx(hoster_url);
				break;
			case "hostingbulk.com":
				validated = HosterFunctions.validate_hostingbulk(hoster_url);
				break;
			case "embed.nowvideo.sx":
			case "embed.nowvideo.eu":
				validated = HosterFunctions.validate_nowvideo(hoster_url);
				break;
			default:
				return "NO_FILTER_AVAILABLE";
		}
		return validated;
	}

}
