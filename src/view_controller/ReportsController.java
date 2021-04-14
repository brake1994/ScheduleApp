package view_controller;

import Model.Appointment;
import Model.AppointmentDatabase;
import Utils.AlertMessages;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controller for reports screen
 * Three separate tabs each with its own report
 */
public class ReportsController implements Initializable {


    @Override
    public void initialize(URL url, ResourceBundle rb){
        //report 1 tab initialize
        monthComboBox.setItems(AppointmentDatabase.getMonthList());

        //report 2 tab initialize
        contactComboBox.setItems(AppointmentDatabase.getContactIDList());

        //report 3 tab
        barChart.setAnimated(false);
        xAxis.setLabel("Appointment Start Hour");
        yAxis.setLabel("Occurrences");
        populateBarChart();

    }

    /**
     * Button event handler that returns user to dashboard screen
     * @param event ActionEvent
     */
    @FXML
    void returnToDashboard(ActionEvent event) {
        DashboardController.loadScreen(event, "Dashboard.fxml");
    }

    //report 1 FXIDs and event handlers
    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private ListView<String> listView;

    /**
     * Filter the ListView by month on action
     */
    @FXML
    void filterListView() {
        if(!monthComboBox.getSelectionModel().isEmpty()) {
            listView.getItems().remove(0, listView.getItems().size());

            String selectedMonth = monthComboBox.getSelectionModel().getSelectedItem();
            ObservableList<String> filteredTypeList = FXCollections.observableArrayList();
            String month;
            String year;
            String type;

            for (String s : AppointmentDatabase.createReportByTypeMonth()) {
                year = s.substring(0,3);
                month = s.substring(5, 7);
                YearMonth a = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));

                if(a.getMonth().toString().contains(selectedMonth.toUpperCase())) {
                    type = s.substring(8);
                    filteredTypeList.add(type);
                }
            }
            for(String t: filteredTypeList){
                listView.getItems().add(t);
            }

        }
        else{
            AlertMessages.information("Select a month.");
        }

    }

    //report 2  FXIDs and event handlers

    @FXML
    private TableView<Appointment> tableView;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIDColumn;
    @FXML
    private TableColumn<Appointment, Timestamp> startDateTimeColumn;
    @FXML
    private TableColumn<Appointment, Timestamp> endDateTimeColumn;
    @FXML
    private TableColumn<Appointment, String> titleColumn;
    @FXML
    private TableColumn<Appointment, String> typeColumnTwo;
    @FXML
    private TableColumn<Appointment, String> descriptionColumn;
    @FXML
    private TableColumn<Appointment, Integer> customerIDColumn;

    @FXML
    private ComboBox<Integer> contactComboBox;


    /**
     * Filter tableview in tab 2 by contact
     */
    @FXML
    void filterTableView() {
        if(contactComboBox.getSelectionModel().isEmpty()){
            AlertMessages.information("Select a contact.");
        }
        else{
            int contactID = contactComboBox.getSelectionModel().getSelectedItem();

            tableView.setItems(AppointmentDatabase.createScheduleForContact(contactID));
            appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            typeColumnTwo.setCellValueFactory(new PropertyValueFactory<>("type"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            startDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTimestamp"));
            endDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTimestamp"));
            tableView.refresh();
        }
    }

    //Report 3 FXIDs and event handlers
    @FXML
    private BarChart<String, Integer> barChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    /**
     * Parse data from AppointmentDatabase.createReportOnStartHour method
     */
    private void populateBarChart(){
        String startHour;
        String amount;
        List<Map<String, Integer>> completeList = new ArrayList<>();

        int indexOfSpace;
        for (String s : AppointmentDatabase.createReportOnStartHour()) {
            indexOfSpace = s.lastIndexOf(' ');
            String y = s.trim();
            startHour = y.substring(0, indexOfSpace);
            amount = y.substring(indexOfSpace).trim();

            completeList.add(Map.of(startHour, Integer.parseInt(amount)));
        }

        barChart.getData().add(generateDataSeries(completeList));
    }

    /**
     * Method that creates a dataSeries from a List of Maps
     * Used in populateBarChart method.
     * Lambda is needed to sort through key, value map pairs and save them to new variables
     * @param finishedList List of Map<String, Integer>
     * @return dataSeries - XYChart.Series<String, Integer>
     */
    private static XYChart.Series<String, Integer> generateDataSeries(List<Map<String, Integer>> finishedList){
        XYChart.Series<String, Integer> dataSeries = new XYChart.Series<>();
        AtomicReference<String> s = new AtomicReference<>();
        AtomicInteger t = new AtomicInteger();
        for (Map<String, Integer> stringIntegerMap : finishedList) {
            stringIntegerMap.forEach((key, value) -> {
                s.set(key);
                t.set(value);
            });
            dataSeries.getData().add(new XYChart.Data<>(s.toString(), Integer.parseInt(t.toString())));
        }
        return dataSeries;
    }


}
