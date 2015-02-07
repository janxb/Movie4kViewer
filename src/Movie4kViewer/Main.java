package Movie4kViewer;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.pivot.wtk.DesktopApplicationContext;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        new Main().run(args);
    }

    private void run(String[] args) throws IOException, URISyntaxException {

        DesktopApplicationContext.main(ApplicationWindow.class, new String[]{
        	"--width="+Settings.window_width, 
        	"--height="+Settings.window_height, 
        	"--resizable=false", 
        	"--center=true"});

    }

}
