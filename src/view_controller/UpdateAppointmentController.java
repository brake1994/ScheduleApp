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
 * Controller for update appointment screen.
 */
public class UpdateAppointmentController implements Initializable {

    /**
     * Appointment selected on all appointments screen
     */
    private static int selectedAppointmentID;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL url, ResourceBundle rb){
            Appointment selectedAppointment = AppointmentDatabase.searchAppointment(selectedAppointmentID);
            //Can't be null. Selected object is required to load this page. getStartTimestamp() throws warning otherwise
            assert selectedAppointment != null;
            LocalDate selectedDate = selectedAppointment.getStartTimestamp().toLocalDateTime().toLocalDate();

            //populate tables
            appointmentTable.setItems(AppointmentDatabase.filterAppointmentsByDay(selectedDate));
            datePicker.setValue(selectedDate);
            startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
            endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
            appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            customerIdColumnAppointmentTable.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            contactIdColumn.setCellValueFactory(new PropertyValueFactory<>("contactID"));

            customerTable.setItems(CustomerDatabase.getAllCustomersFromDatabase());
            customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            //populate textFields except userID
            appointmentIdTextfield.setText(String.valueOf(selectedAppointment.getAppointmentID()));
            appointmentIdTextfield.setDisable(true);
            titleTextfield.setText(selectedAppointment.getTitle());
            descriptionTextfield.setText(selectedAppointment.getDescription());
            locationTextfield.setText(selectedAppointment.getLocation());
            typeTextfield.setText(selectedAppointment.getType());
            startDateDatepicker.setValue(selectedDate);
            startTimeTextfield.setText(selectedAppointment.getStartTime());
            endTimeTextfield.setText(selectedAppointment.getEndTime());
            endDateDatepicker.setValue(selectedDate);
            customerIdTextfield.setText(String.valueOf(selectedAppointment.getCustomerID()));
            contactIdComboBox.setItems(AppointmentDatabase.getContactIDList());
            contactIdComboBox.getSelectionModel().select(Objects.requireNonNull(AppointmentDatabase.getContactIDList()).indexOf(selectedAppointment.getContactID()));

    }

    //table FXIDs
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, String> startTimeColumn;
    @FXML
    private TableColumn<Appointment, String> endTimeColumn;
    @FXML
    private TableColumn<Appointment, Integer> contactIdColumn;
    @FXML
    private TableColumn<Appointment, Integer> customerIdColumnAppointmentTable;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
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
     * Displays a table that shows appointment times filtered by day
     */
    @FXML
    void filterAvailableTimeTable() {
        LocalDate date = datePicker.getValue();
        appointmentTable.setItems(AppointmentDatabase.filterAppointmentsByDay(date));
    }

    /**
     * Return to all appointments screen
     * @param event ActionEvent
     */
    @FXML
    void onActionCancelButton(ActionEvent event) {
        DashboardController.loadScreen(event, "AllAppointments.fxml");
    }

    /**
     * Save button event handler.
     * Saves appointment with updated fields.
     * @param event ActionEvent
     */
    @FXML
    void onActionSaveAppointment(ActionEvent event) {
        try{
            //appointment fields
            int index = AppointmentDatabase.getAppointmentList().indexOf(AppointmentDatabase.searchAppointment(selectedAppointmentID));
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
                inputsInvalid = true;
                AlertMessages.warning("Invalid time entry.");
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

            if(AppointmentDatabase.checkAppointmentOverlap(customerID, startTimestamp, endTimestamp)){
                AlertMessages.warning("Customer has appointment scheduled during this time.");
                inputsInvalid = true;
            }

            //Add appointment if all inputs valid
            if(!inputsInvalid){
                Appointment a = new Appointment(appointmentID, title, description, type, location, startTimestamp, endTimestamp, customerID, userID, contactID);
                AppointmentDatabase.updateAppointment(index, a);
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
     * Event Handler for Customer Table select button
     * Selects a customer and updates customerID textfield
     */
    @FXML
    void onActionSelectCustomer() {
        if(customerTable.getSelectionModel().getSelectedItem() != null){
            Customer c = customerTable.getSelectionModel().getSelectedItem();
            customerIdTextfield.setText(String.valueOf(c.getCustomerID()));
        }
        else{
            AlertMessages.information("Select a customer.");
        }
    }

    /**
     * Method used to transfer selected appointment data from all appointments screen
     * @param appointment Selected appointment
     */
    //Transfer appointment data
    public static void setSelectedAppointment(Appointment appointment) {
        selectedAppointmentID = appointment.getAppointmentID();
    }

    /**
     * Checks if the inputs for time are valid.
     * @param time Time input checked - String
     * @return Boolean - True if the entry is valid, False if not valid
     */
    //checking for valid time entries
    public static Boolean validTimeEntries(String time){
        String time1 = time.trim();
        if(!time1.contains(":")){
            return false;
        }

        String hour = time1.substring(0,1);
        String minute = time1.substring(3, 4);
        for(int i = 0; i < hour.length(); i++){
            if(!Character.isDigit(hour.charAt(i)) || !Character.isDigit(minute.charAt(i))){
                return false;
            }
        }
        return Integer.parseInt(hour) <= 24 && Integer.parseInt(minute) <= 59;
    }
}
