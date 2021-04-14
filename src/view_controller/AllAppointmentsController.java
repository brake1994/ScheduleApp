package view_controller;

import Model.Appointment;
import Model.AppointmentDatabase;
import Utils.AlertMessages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * All appointments screen controller
 */
public class AllAppointmentsController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb){
        AppointmentDatabase.getAllAppointmentsFromDatabase();

        //week tab
        weekComboBox.setItems(AppointmentDatabase.parseWeekForFilter());
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
        weekComboBox.getSelectionModel().select(currentWeek);
        filterWeek();

        startDateTimeWeek.setCellValueFactory(new PropertyValueFactory<>("startTimestamp"));
        endDateTimeWeek.setCellValueFactory(new PropertyValueFactory<>("endTimestamp"));
        appointmentIDWeek.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleWeek.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionWeek.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationWeek.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactWeek.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        typeWeek.setCellValueFactory(new PropertyValueFactory<>("type"));
        customerIDWeek.setCellValueFactory(new PropertyValueFactory<>("customerID"));


        //month tab
        monthComboBox.setItems(AppointmentDatabase.getMonthList());
        int currentMonth = calendar.get(Calendar.MONTH);
        monthComboBox.getSelectionModel().select(currentMonth);
        filterMonth();

        startDateTimeMonth.setCellValueFactory(new PropertyValueFactory<>("startTimestamp"));
        endDateTimeMonth.setCellValueFactory(new PropertyValueFactory<>("endTimestamp"));
        appointmentIDMonth.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleMonth.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionMonth.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationMonth.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactMonth.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        typeMonth.setCellValueFactory(new PropertyValueFactory<>("type"));
        customerIDMonth.setCellValueFactory(new PropertyValueFactory<>("customerID"));

    }

    //week FXIDs
    @FXML
    private Tab weekTab;
    @FXML
    private TableView<Appointment> weekTable;
    @FXML
    private TableColumn<Appointment, Timestamp> startDateTimeWeek;
    @FXML
    private TableColumn<Appointment, Timestamp> endDateTimeWeek;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIDWeek;
    @FXML
    private TableColumn<Appointment, String> titleWeek;
    @FXML
    private TableColumn<Appointment, String> descriptionWeek;
    @FXML
    private TableColumn<Appointment, String> locationWeek;
    @FXML
    private TableColumn<Appointment, Integer> contactWeek;
    @FXML
    private TableColumn<Appointment, String> typeWeek;
    @FXML
    private TableColumn<Appointment, Integer> customerIDWeek;
    @FXML
    private ComboBox<String> weekComboBox;

    //month FXIDs
    @FXML
    private Tab monthTab;
    @FXML
    private TableView<Appointment> monthTable;
    @FXML
    private TableColumn<Appointment, Timestamp> startDateTimeMonth;
    @FXML
    private TableColumn<Appointment, Timestamp> endDateTimeMonth;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIDMonth;
    @FXML
    private TableColumn<Appointment, String> titleMonth;
    @FXML
    private TableColumn<Appointment, String> descriptionMonth;
    @FXML
    private TableColumn<Appointment, String> locationMonth;
    @FXML
    private TableColumn<Appointment, Integer> contactMonth;
    @FXML
    private TableColumn<Appointment, String> typeMonth;
    @FXML
    private TableColumn<Appointment, Integer> customerIDMonth;
    @FXML
    private ComboBox<String> monthComboBox;

    /**
     * Filter table by week ( week tab )
     */
    @FXML
    void onActionFilterWeek() {
        filterWeek();
    }

    /**
     * Filter table by month ( month tab )
     */
    @FXML
    void onActionFilterMonth() {
        filterMonth();
    }

    /**
     * Loads add appointment screen
     * @param event ActionEvent
     */
    @FXML
    void addAppointmentButton(ActionEvent event) {
        DashboardController.loadScreen(event, "AddAppointment.fxml");
    }

    /**
     * Loads update appointment screen.
     * Selected appointment is sent to Update appointment screen through a setter method
     * @param event ActionEvent
     */
    @FXML
    void updateAppointmentButton(ActionEvent event) {
        Appointment a = weekTable.getSelectionModel().getSelectedItem();
        Appointment b = monthTable.getSelectionModel().getSelectedItem();

        if (weekTab.isSelected()) {
            if (a != null) {
                UpdateAppointmentController.setSelectedAppointment(a);
                DashboardController.loadScreen(event, "UpdateAppointment.fxml");
            }
            else{
                AlertMessages.information("Select an appointment.");
            }
        }
        if (monthTab.isSelected()) {
            if (b != null) {
                UpdateAppointmentController.setSelectedAppointment(b);
                DashboardController.loadScreen(event, "UpdateAppointment.fxml");
            }
            else {
                AlertMessages.information("Select an appointment.");
            }
        }
    }

    /**
     * Delete button event handler.
     * Delete selected appointment if the user confirms.
     */
    @FXML
    void deleteAppointmentButton() {
        if(weekTab.isSelected()){
            if(weekTable.getSelectionModel().isEmpty()){
                AlertMessages.information("No appointment selected.");
            }
            else {
                Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                a.setContentText("Delete selected appointment?");
                a.showAndWait();
                if (a.getResult() == ButtonType.OK) {
                    Appointment appointment = weekTable.getSelectionModel().getSelectedItem();
                    AppointmentDatabase.deleteAppointment(appointment);
                    AppointmentDatabase.deleteAppointmentFromDatabase(appointment);
                    if(weekComboBox.getSelectionModel().isEmpty()){
                        weekTable.setItems(AppointmentDatabase.getAllAppointmentsFromDatabase());
                    }
                    else{
                        filterWeek();
                    }

                } else {
                    a.close();
                }
            }
        }
        if(monthTab.isSelected()){
            if(monthTable.getSelectionModel().isEmpty()){
                AlertMessages.information("No appointment selected.");
            }
            else {
                Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                a.setContentText("Delete selected appointment?");
                a.showAndWait();
                if (a.getResult() == ButtonType.OK) {
                    Appointment appointment = monthTable.getSelectionModel().getSelectedItem();
                    AppointmentDatabase.deleteAppointment(appointment);
                    AppointmentDatabase.deleteAppointmentFromDatabase(appointment);
                    if(monthComboBox.getSelectionModel().isEmpty()){
                        monthTable.setItems(AppointmentDatabase.getAllAppointmentsFromDatabase());
                    }
                    else{
                        filterMonth();
                    }
                } else {
                    a.close();
                }
            }
        }

    }

    /**
     * Show all appointments in selected tab
     */
    @FXML
    void showAllAppointmentsButton() {
        if(weekTab.isSelected()){
            weekTable.setItems(AppointmentDatabase.getAllAppointmentsFromDatabase());
            weekComboBox.getSelectionModel().clearSelection();
        }
        if(monthTab.isSelected()){
            monthTable.setItems(AppointmentDatabase.getAllAppointmentsFromDatabase());
            monthComboBox.getSelectionModel().clearSelection();
        }
    }

    //navigation buttons

    /**
     * Load customer database screen
     * @param event ActionEvent
     */
    @FXML
    void customerScreenButton(ActionEvent event) {
        DashboardController.loadScreen(event, "CustomerDatabase.fxml");
    }

    /**
     * Load dashboard screen
     * @param event ActionEvent
     */
    @FXML
    void dashboardButton(ActionEvent event) {
        DashboardController.loadScreen(event, "Dashboard.fxml");
    }

    /**
     * Load reports screen
     * @param event ActionEvent
     */
    @FXML
    void reportsScreenButton(ActionEvent event) {
        DashboardController.loadScreen(event, "Reports.fxml");
    }

    /**
     * Filter table by week ( week tab only )
     */
    //Method created to reduce redundancy in code due to multiple calls
    private void filterWeek(){
        //parse dates as Strings
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(weekComboBox.getSelectionModel().isEmpty()){
            System.out.println("Week selection cleared.");
        }
        else {
            String selectedOption = weekComboBox.getSelectionModel().getSelectedItem();
            String startDateString = selectedOption.substring(0, 10);
            String endDateString = selectedOption.substring(14, 24);

            LocalDate startDate = LocalDate.parse(startDateString, dateFormat);
            LocalDate endDate = LocalDate.parse(endDateString, dateFormat);


            weekTable.setItems(AppointmentDatabase.filterAppointmentsByWeek(startDate, endDate));
            weekTable.refresh();
        }
    }

    /**
     * Filter table by month ( month tab only )
     */
    private void filterMonth(){
        if(monthComboBox.getSelectionModel().isEmpty()){
            System.out.println("Month selection cleared.");
        }
        else{
            String selectedOption = monthComboBox.getSelectionModel().getSelectedItem();
            monthTable.setItems(AppointmentDatabase.filterAppointmentsByMonth(selectedOption));
            monthTable.refresh();
        }
    }

}
