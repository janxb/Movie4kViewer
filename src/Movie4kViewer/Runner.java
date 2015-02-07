package Movie4kViewer;

public class Runner implements Runnable {
	 
  public void run() {
  	EventHandler.linkFinder.run_for_movies();
  	EventHandler.updateButtons();
  }
}