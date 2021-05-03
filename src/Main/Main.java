package Main;

import Model.AppointmentDatabase;
import Utils.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This application accesses a database and schedules appointments for customers.
 * All times and dates displayed are based on the user's default computer settings.
 * The log-in screen supports both French and English.
 *
 * @author Tanner Brake
 * @version 1.0
 * @since 3/13/2021
 */
public class Main extends Application {

    /**
     * Loads Initial Screen ( Log-in Screen)
     * @param stage Log-in
     * @throws Exception Exception
     */
    @Override
    public void start(Stage stage) throws Exception{
        Parent parent = FXMLLoader.load(getClass().getResource("/view_controller/LoginScreen.fxml"));
        //build scene
        Scene scene = new Scene(parent);

        //display
        stage.setTitle("Scheduling Application");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

    }

    /**
     * Main method of the application.
     * Connects to database upon start-up and disconnects upon close.
     * Methods from Appointment Database class are called to generate various lists for GUI
     * @param args Unused
     */
    public static void main(String[] args) {
        //DatabaseConnection.startDatabaseConnection();

//        AppointmentDatabase.createWeekList();
//        AppointmentDatabase.parseWeekForFilter();
//        AppointmentDatabase.createMonthList();
//        AppointmentDatabase.getAllAppointmentsFromDatabase();

        launch(args);

        //DatabaseConnection.closeConnection();
    }
}
