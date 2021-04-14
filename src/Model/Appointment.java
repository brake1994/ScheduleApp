package Model;

import Utils.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Class to create Appointment objects which are used in the Appointment Database
 */
public class Appointment {


    private int appointmentID;
    private SimpleStringProperty title = new SimpleStringProperty();
    private SimpleStringProperty description = new SimpleStringProperty();
    private SimpleStringProperty type = new SimpleStringProperty();
    private SimpleStringProperty location = new SimpleStringProperty();
    private int contactID;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private SimpleStringProperty startTime = new SimpleStringProperty();
    private SimpleStringProperty endTime = new SimpleStringProperty();
    private int customerID;
    private int userID;
    private String created_by;
    private Timestamp created_by_time;
    private String updated_by;
    private Timestamp updated_by_time;

    /* Constructors
    ============================================================================== */

    /**
     * Appointment constructor
     * @param appointmentID Appointment ID - Integer
     * @param title Appointment Title - String
     * @param description Appointment Description - String
     * @param type Appointment Type - String
     * @param location Appointment Location - String
     * @param startTimestamp Appointment Start Time/Date - Timestamp
     * @param endTimestamp Appointment End Time/Date - Timestamp
     * @param customerID Customer ID for Appointment - Integer
     * @param userID User ID for user creating Appointment - Integer
     * @param contactID Contact ID for Appointment - Integer
     */
    public Appointment(int appointmentID, String title, String description, String type, String location, Timestamp startTimestamp, Timestamp endTimestamp, int customerID, int userID, int contactID) {
        this.appointmentID = appointmentID;
        setTitle(title);
        setDescription(description);
        setType(type);
        setLocation(location);
        setStartTimestamp(startTimestamp);
        setEndTimestamp(endTimestamp);
        setCustomerID(customerID);
        setUserID(userID);
        setContactID(contactID);
        setTimes(this.startTimestamp, this.endTimestamp);
        setCreated_by(DatabaseConnection.getUsername());
        setCreated_by_time(Timestamp.valueOf(LocalDateTime.now()));
    }

    /**
     * Appointment Constructor used by filterAppointmentByWeek method in Appointment Database
     * @param startTimestamp Appointment Start Time/Date
     * @param endTimestamp Appointment End Time/Date
     */
    public Appointment(Timestamp startTimestamp, Timestamp endTimestamp){
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public Appointment(){}

    /* Getters
    ===============================================================================  */

    public int getAppointmentID() {
        return appointmentID;
    }

    public String getDescription() {
        return description.get();
    }

    public String getType() {
        return type.get();
    }

    public String getLocation() {
        return location.get();
    }

    public String getTitle() {
        return title.get();
    }

    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public Timestamp getEndTimestamp() {
        return endTimestamp;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getContactID() {
        return contactID;
    }

    public int getUserID() {
        return userID;
    }

    public String getStartTime() {
        return startTime.get();
    }

    public String getEndTime() {
        return endTime.get();
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public Timestamp getUpdated_by_time() {
        return updated_by_time;
    }

    /* Setters
    ==================================================================== */

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public void setCreated_by_time(Timestamp created_by_time) {
        this.created_by_time = created_by_time;
    }

    public void setUpdated_by_time(Timestamp updated_by_time) {
        this.updated_by_time = updated_by_time;
    }

    /**
     * Method used to parse start/end times from start/end timestamps
     * Used for interfaces where only time should be displayed.
     * @param startTimestamp - Timestamp
     * @param endTimestamp - Timestamp
     */
    public void setTimes(Timestamp startTimestamp, Timestamp endTimestamp){
        try{
            //convert to locale time
            DateTimeFormatter timestampConverter = DateTimeFormatter.ofPattern("HH:mm");
            String convertedStartTimestamp =  ZonedDateTime.of(startTimestamp.toLocalDateTime(), ZoneId.systemDefault()).format(timestampConverter);
            String convertedEndTimestamp =  ZonedDateTime.of(endTimestamp.toLocalDateTime(), ZoneId.systemDefault()).format(timestampConverter);

            startTime.set(convertedStartTimestamp);
            endTime.set(convertedEndTimestamp);
        }
        catch (NullPointerException e ){
            System.out.println("Invalid time: " + e.getMessage());
        }
    }

}
