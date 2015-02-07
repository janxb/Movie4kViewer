package Movie4kViewer;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TablePane.Row;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Window;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class EventHandler extends Window implements Bindable {

	private static PushButton browserButton;
	private static PushButton downloadButton;
	private static PushButton convertButton;
	private static PushButton clipboardButton;
	private static Row statusRow;
	private static TextInput linkInput;
	private static TextArea statusLabel;
	public static HosterLinkFinder linkFinder;

	public static void status(final String print) {
		ApplicationContext.queueCallback(new Runnable() {
			@Override
			public void run() {
				statusLabel.setText(statusLabel.getText() + print + "\n");
			}
		});
	}

	public static void clearStatus() {
		ApplicationContext.queueCallback(new Runnable() {
			@Override
			public void run() {
				statusLabel.setText("");
			}
		});
	}

	public static boolean validate_url(String url) {
		URL u;
		try {
			u = new URL(url);
			u.toURI(); // does the extra checking required for validation of URI
		} catch (MalformedURLException | URISyntaxException e) {
			return false;
		}
		return true;
	}

	@Override
	public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
		browserButton = (PushButton) namespace.get("browserButton");
		downloadButton = (PushButton) namespace.get("downloadButton");
		convertButton = (PushButton) namespace.get("convertButton");
		clipboardButton = (PushButton) namespace.get("clipboardButton");
		linkInput = (TextInput) namespace.get("mainMovieLink");
		statusLabel = (TextArea) namespace.get("statusLabel");
		statusRow = (Row) namespace.get("statusRow");
		ApplicationContext.queueCallback(new Runnable() {
			@Override
			public void run() {
				statusRow.setHeight(Settings.window_height-150);
				ApplicationWindow.repaint();
			}
		});

		convertButton.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button button) {
				if (EventHandler.validate_url(linkInput.getText())) {
					linkFinder = new HosterLinkFinder(linkInput.getText());
					EventHandler.clearStatus();
					browserButton.setEnabled(false);
					downloadButton.setEnabled(false);
					clipboardButton.setEnabled(false);
					convertButton.setEnabled(false);
					new Thread(new Runner()).start();
				} else {
					EventHandler.clearStatus();
					EventHandler.status("-- Please enter Movie4k-URL..");
				}
			}
		});

		clipboardButton.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button button) {
				StringSelection stringSelection = new StringSelection(linkFinder.get_valid_movie_url());
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
			}
		});

		browserButton.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button button) {
				linkFinder.open_hoster_url_in_browser();
			}
		});

		downloadButton.getButtonPressListeners().add(new ButtonPressListener() {
			@Override
			public void buttonPressed(Button button) {
				linkFinder.open_hoster_url_in_jdownloader();
			}
		});
	}

	public static void updateButtons() {
		ApplicationContext.queueCallback(new Runnable() {
			@Override
			public void run() {
				convertButton.setEnabled(true);
				if (linkFinder.online_hoster_found()) {
					browserButton.setEnabled(true);
					downloadButton.setEnabled(true);
					clipboardButton.setEnabled(true);
				}
				ApplicationWindow.repaint();
			}
		});

	}
}
