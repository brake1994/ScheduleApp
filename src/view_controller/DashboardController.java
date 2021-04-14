package view_controller;

import Model.Appointment;
import Model.AppointmentDatabase;
import Utils.AlertMessages;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Initial screen after log-in.
 * Display today's appointments and navigation options to other screens.
 */
public class DashboardController implements Initializable {

    /**
     * Initialization method for dashboard controller.
     * Lambda is used to sort through table rows and check for past appointments.
     * Past appointments are highlighted in red in the GUI
     * @param url Unused
     * @param rb Unused
     */
    @Override
    public void initialize(URL url, ResourceBundle rb){
        LocalDate today = LocalDate.now();
        appointmentsTable.setItems(AppointmentDatabase.filterAppointmentsByDay(today));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));

        appointmentsTable.setRowFactory(row -> new TableRow<>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    for (Appointment a : AppointmentDatabase.filterAppointmentsByDay(LocalDate.now())) {
                        if (AppointmentDatabase.appointmentInPast(a)) {
                            Appointment b = getTableView().getItems().get(getIndex());

                            if (a.getAppointmentID() == b.getAppointmentID()) {
                                setStyle("-fx-background-color: #FF0035");
                            }
                        }
                    }
                }
            }
        });

    }

    //FXIDs
    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableColumn<Appointment, String> startTime;
    @FXML
    private TableColumn<Appointment, String> endTime;
    @FXML
    private TableColumn<Appointment, String> title;
    @FXML
    private TableColumn<Appointment, String> type;
    @FXML
    private TableColumn<Appointment, Integer> appointmentID;

    /**
     * Loads customer database screen
     * @param event ActionEvent
     */
    @FXML
    void customerDatabaseButton(ActionEvent event)  {
        loadScreen(event, "CustomerDatabase.fxml");
    }

    /**
     * Loads report screen
     * @param event ActionEvent
     */
    @FXML
    void reportsButton(ActionEvent event) {
        loadScreen(event, "Reports.fxml");
    }

    /**
     * Loads All appointments screen.
     * @param event ActionEvent
     */
    @FXML
    void scheduleAppointmentButton(ActionEvent event) {
        loadScreen(event, "AllAppointments.fxml");

    }

    /**
     * Loads Update Appointment screen
     * @param event Actionevent
     */
    @FXML
    void updateAppointment(ActionEvent event) {
        if(appointmentsTable.getSelectionModel().isEmpty()){
            AlertMessages.information("No appointment selected.");
        }
        else{
            Appointment a = appointmentsTable.getSelectionModel().getSelectedItem();
            UpdateAppointmentController.setSelectedAppointment(a);
            loadScreen(event, "UpdateAppointment.fxml");
        }
    }

    /**
     * Method used to load screens throughout the application.
     * @param event ActionEvent
     * @param fxmlName FXML file to be loaded
     */
    // Loads fxmlName passed in. Created to reduce redundancy
    public static void loadScreen(Event event, String fxmlName){
       try {
           Parent parent = FXMLLoader.load(DashboardController.class.getResource(fxmlName));
           Scene scene = new Scene(parent);
           Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
           stage.setScene(scene);
           stage.setResizable(false);
           stage.show();
       }catch (IOException e){
           e.printStackTrace();
       }
    }

}
