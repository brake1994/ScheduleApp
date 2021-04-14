package view_controller;

import Model.Appointment;
import Model.AppointmentDatabase;
import Model.Customer;
import Model.CustomerDatabase;
import Utils.AlertMessages;
import Utils.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for add appointment screen.
 */
public class AddAppointmentController implements Initializable {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL url, ResourceBundle rb){
        customerTable.setItems(CustomerDatabase.getAllCustomersFromDatabase());
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactIdComboBox.setItems(AppointmentDatabase.getContactIDList());
        appointmentIdTextfield.setText(String.valueOf(AppointmentDatabase.generateAppointmentID()));
        appointmentIdTextfield.setDisable(true);
    }

    //table related FXIDs
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, Integer> contactIdColumn;
    @FXML
    private TableColumn<Appointment, Integer> customerIdColumnAppointmentTable;
    @FXML
    private TableColumn<Appointment, String> startTimeColumn;
    @FXML
    private TableColumn<Appointment, String> endTimeColumn;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;
    @FXML
    private TableColumn<Customer, String> customerNameColumn;

    //textfield FXIDs
    @FXML
    private TextField appointmentIdTextfield;
    @FXML
    private TextField titleTextfield;
    @FXML
    private TextField descriptionTextfield;
    @FXML
    private TextField locationTextfield;
    @FXML
    private ComboBox<Integer> contactIdComboBox;
    @FXML
    private TextField typeTextfield;
    @FXML
    private DatePicker startDateDatepicker;
    @FXML
    private TextField startTimeTextfield;
    @FXML
    private DatePicker endDateDatepicker;
    @FXML
    private TextField endTimeTextfield;
    @FXML
    private TextField customerIdTextfield;
    @FXML
    private TextField userIdTextfield;

    /**
     * Filter table by day.
     * Allows user to see which time slots are filled.
     */
    @FXML
    void filterAvailableTimeTable() {
        LocalDate date = datePicker.getValue();
        appointmentTable.setItems(AppointmentDatabase.filterAppointmentsByDay(date));
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        customerIdColumnAppointmentTable.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        contactIdColumn.setCellValueFactory(new PropertyValueFactory<>("contactID"));

    }

    /**
     * Cancel button event handler.
     * If users accepts, return to all appointments screen
     * @param event ActionEvent
     */
    @FXML
    void onActionCancelButton(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Return to main appointment screen?");
        a.showAndWait();
        if(a.getResult() == ButtonType.OK) {
            DashboardController.loadScreen(event, "AllAppointments.fxml");
        }
        else{
            a.close();
        }
    }

    /**
     * Save appointment event handler.
     * If all inputs are valid the customer is saved to the database.
     * @param event ActionEvent
     */
    @FXML
    void onActionSaveAppointment(ActionEvent event) {
        try{
            //appointment fields
            int appointmentID = Integer.parseInt(appointmentIdTextfield.getText().trim());
            String title = titleTextfield.getText().trim();
            String description = descriptionTextfield.getText().trim();
            String location = locationTextfield.getText().trim();
            int contactID = contactIdComboBox.getSelectionModel().getSelectedItem();
            String type = typeTextfield.getText().trim();
            Timestamp startTimestamp = Timestamp.valueOf(startDateDatepicker.getValue().format(dateTimeFormatter) + " " + startTimeTextfield.getText().trim() + ":00");
            Timestamp endTimestamp = Timestamp.valueOf(endDateDatepicker.getValue().format(dateTimeFormatter) + " " + endTimeTextfield.getText().trim() + ":00");
            int customerID = Integer.parseInt(customerIdTextfield.getText().trim());
            int userID = Integer.parseInt(userIdTextfield.getText().trim());


            boolean inputsInvalid = false;

            //checking for valid times
            if(!AppointmentDatabase.appointmentDuringBusinessHours(startTimestamp, endTimestamp)){
                AlertMessages.warning("Appointment not within business hours: 8 am - 10 pm EST");
                inputsInvalid = true;
            }
            if(AppointmentDatabase.timestampsInvalid(startTimestamp, endTimestamp)){
                inputsInvalid = true;
            }
            if(!UpdateAppointmentController.validTimeEntries(startTimeTextfield.getText()) || !UpdateAppointmentController.validTimeEntries(endTimeTextfield.getText())){
                AlertMessages.warning("Invalid time entry.");
                inputsInvalid = true;
            }

            //check for valid customerID and userID
            if(CustomerDatabase.searchCustomer(customerID) == null){
                AlertMessages.warning("Customer does not exist.");
                inputsInvalid = true;
            }
            if(DatabaseConnection.getUsernameFromDatabase(userID).equals("")){
                AlertMessages.warning("User does not exist.");
                inputsInvalid = true;
            }

            //Customer already scheduled during time check
            if(AppointmentDatabase.checkAppointmentOverlap(customerID, startTimestamp, endTimestamp)){
                AlertMessages.warning("Customer has appointment scheduled during this time.");
                inputsInvalid = true;
            }

            //create appointment if validation check passes
            if(!inputsInvalid){
                Appointment a = new Appointment(appointmentID, title, description, type, location, startTimestamp, endTimestamp, customerID, userID, contactID);
                AppointmentDatabase.addAppointmentToDatabase(a);
                AppointmentDatabase.addAppointment(a);
                DashboardController.loadScreen(event, "AllAppointments.fxml");
            }

        }catch (IllegalArgumentException e){
            AlertMessages.error("Invalid input: " + e.getMessage());
        }
        catch (NullPointerException e){
            AlertMessages.error("Fill in all fields.");
        }
    }

    /**
     * Event handler for select customer button.
     * Selected customer is entered into the customerID textfield
     */
    @FXML
    void onActionSelectCustomer() {
        if(customerTable.getSelectionModel().getSelectedItem() != null){
            Customer c = customerTable.getSelectionModel().getSelectedItem();
            customerIdTextfield.setText(String.valueOf(c.getCustomerID()));
        }
        else{
            AlertMessages.warning("Select a customer.");
        }
    }



}
