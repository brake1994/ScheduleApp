package Utils;

import javafx.scene.control.Alert;

/**
 * Class to create alerts used throughout the application.
 */
public class AlertMessages {

    /**
     * Creates an error alert
     * @param message alert message
     */
    public static void error(String message){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(message);
        a.show();
    }

    /**
     * Creates an information alert
     * @param message alert message
     */
    public static void information(String message){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(message);
        a.show();
    }

    /**
     * Creates a warning alert
     * @param message alert message
     */
    public static void warning(String message){
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setContentText(message);
        a.show();
    }
}
