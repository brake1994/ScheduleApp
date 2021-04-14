package Model;

import Utils.AlertMessages;
import Utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Class to manage appointments in the database/application.
 */
public class AppointmentDatabase {
    /**
     * Appointment List- stores all appointments from database
     */
    private static  ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    //variables used for various time/date methods
    private static final List<LocalDate> daysList = new ArrayList<>();
    private static final ObservableList<LocalDate[]> weekList = FXCollections.observableArrayList();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final YearMonth startYearMonth = YearMonth.of(2021, 1);
    private static final YearMonth endYearMonth = YearMonth.of(2022, 1);
    private static final ObservableList<String> monthList = FXCollections.observableArrayList();


    /*  GET, ADD, UPDATE, DELETE methods for AppointmentList
    ======================================================================= */


    public static ObservableList<Appointment> getAppointmentList(){
        return appointmentList;
    }

    /**
     * Method used to retrieve all appointments from database.
     * A query is executed then the ResultSet is created into Appointment objects
     * and added to the local AppointmentList.
     * Error messages are thrown for SQLExceptions
     * @return Observable List of all Appointments
     */
    public static ObservableList<Appointment> getAllAppointmentsFromDatabase(){
        try{
            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "SELECT * FROM appointments";
            statement.execute(query);
            ResultSet resultSet = statement.getResultSet();
            appointmentList.clear();

            //retrieve appointments
            while (resultSet.next()){
                Appointment appointment = new Appointment(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Type"),
                        resultSet.getString("Location"),
                        convertToLocalTime(resultSet.getTimestamp("Start")),
                        convertToLocalTime(resultSet.getTimestamp("End")),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID"),
                        resultSet.getInt("Contact_ID"));

                appointmentList.add(appointment);
            }


            return appointmentList;

        }
        catch (SQLException | NullPointerException e){
            AlertMessages.error("ERROR: " + e.getMessage());
            return null;
        }
    }

    public static void addAppointment(Appointment appointment){
        appointmentList.add(appointment);
    }

    /**
     * Add appointment to Database.
     * Appointment param is passed through and the variables are stored locally to be used in the query.
     * Query is executed and the data is stored to the database.
     * @param appointment Appointment to be added
     */
    public static void addAppointmentToDatabase(Appointment appointment){
        try{
            int appointmentID = appointment.getAppointmentID();
            String title = appointment.getTitle();
            String description = appointment.getDescription();
            String type = appointment.getType();
            String location = appointment.getLocation();
            Timestamp startTimestamp = convertToUTC(appointment.getStartTimestamp());
            Timestamp endTimestamp = convertToUTC(appointment.getEndTimestamp());
            int customerID = appointment.getCustomerID();
            int userID = appointment.getUserID();
            int contactID = appointment.getContactID();
            String created_by = appointment.getCreated_by();
            
            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "INSERT INTO appointments(Appointment_ID, Title, Description, Location, Type, Start, End, Created_By, Customer_ID, User_ID, Contact_ID)" +
                    "VALUES('" + appointmentID + "', '" + title + "', '" + description + "', '" + location + "', '" + type + "', '" + startTimestamp + "', " +
                    "'" + endTimestamp + "', '" + created_by + "', '" + customerID + "', '" + userID + "', '" + contactID + "')";
            statement.executeUpdate(query);
            System.out.println("Appointment added to database.");

        }catch (SQLException e){
            AlertMessages.error("Error with database: " + e.getMessage());
        }
    }

    /**
     * Deletes appointment used as parameter from local list if the appointment is not null.
     * Otherwise, returns appointment not found.
     * @param appointment Appointment to be deleted.
     */
    public static void deleteAppointment(Appointment appointment){
        if(appointment != null) {
            appointmentList.remove(appointment);
        }
        else{
            System.out.println("Appointment not found.");
        }
    }

    /**
     * Deletes appointment used as parameter from database.
     * Appointment is never null because it is checked before calling this method.
     * @param appointment Appointment to be deleted.
     */
    public static void deleteAppointmentFromDatabase(Appointment appointment){
        try{
            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
            int id = appointment.getAppointmentID();
            String query = "DELETE FROM appointments WHERE Appointment_ID = '" + id + "'";
            statement.executeUpdate(query);
            AlertMessages.information("Appointment with ID: " + appointment.getAppointmentID() + " Type: " + appointment.getType() + " deleted.");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * Updates appointment locally and in the database with new inputs.
     * An appointment and its index are passed in and stored to a new appointment object.
     * The old appointment is deleted from the local list and the new one is added.
     * A update query is executed to add the new data to the database.
     * @param index Index of old appointment to be deleted
     * @param appointment Appointment to be updated
     */
    public static void updateAppointment(int index, Appointment appointment){
        try{
            Appointment a = new Appointment(
                    appointment.getAppointmentID(),
                    appointment.getTitle(),
                    appointment.getDescription(),
                    appointment.getType(),
                    appointment.getLocation(),
                    convertToUTC(appointment.getStartTimestamp()),
                    convertToUTC(appointment.getEndTimestamp()),
                    appointment.getCustomerID(),
                    appointment.getUserID(),
                    appointment.getContactID());
            deleteAppointment(appointmentList.get(index));
            a.setUpdated_by(DatabaseConnection.getUsernameFromDatabase(a.getUserID()));
            a.setUpdated_by_time(convertToUTC(Timestamp.valueOf(LocalDateTime.now())));

            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "UPDATE appointments SET Title = '" + a.getTitle() + "', Description = '" + a.getDescription() + "', Location = '" + a.getLocation() + "'," +
                    "Type = '" + a.getType() + "', Start = '" + a.getStartTimestamp() + "', End = '" + a.getEndTimestamp() + "'," +
                    "Last_Updated_By = '" + a.getUpdated_by() + "', Last_Update = '" + a.getUpdated_by_time() + "', " +
                    "Customer_ID = '" + a.getCustomerID() + "', User_ID = '" + a.getUserID() + "', Contact_ID = '" + a.getContactID() + "' " +
                    "WHERE Appointment_ID = '" + a.getAppointmentID() + "'";
            statement.executeUpdate(query);
            System.out.println("Appointment updated.");
            addAppointment(a);

        }catch (SQLException e){
            AlertMessages.error("Error with database: " + e.getMessage());
        }
    }

    /* Search, Filter methods
    ============================================================================= */

    /**
     * Search for appointment by AppointmentID
     * A lambda is used to filter through the different appointments looking for a matching ID.
     * Once a matching ID is found the appointment is collected to a single appointment
     * using the toSingleObject method. Returns the appointment
     * @param appointmentID appointmentID to be searched for
     * @return Found appointment or null
     */
    public static Appointment searchAppointment(int appointmentID){
        return appointmentList.stream().filter(a -> a.getAppointmentID() == appointmentID).collect(toSingleObject());
    }

    /**
     * Converts collection used in search method to a single object
     * A collection is stored as a list and then a lambda expression is used
     * to check the size of the list. If the list is not the size of 1 then null is returned.
     * Otherwise, the first object is returned from the list.
     * @param <Object> unused
     * @return Object found
     */
    //converts collection in search methods to a single object
    public static <Object> Collector<Object, ?, Object> toSingleObject(){
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list-> {if (list.size() != 1) {
                    return null;
                }
                    return list.get(0);
                }
        );
    }

    /**
     * Creates a list of weeks that start on monday for the current year (Jan 1 - Dec 31)
     * The first and last monday of the year including Jan 1 and DEC 31 used to create the list.
     * This results in the first and last week including days from the previous and next year.
     * A do-while statement is used to fill the list from start monday to end monday.
     */
    public static void createWeekList(){

        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = endYearMonth.atDay(1);
        TemporalAdjuster temporalAdjusterEnd = TemporalAdjusters.next(DayOfWeek.MONDAY);
        TemporalAdjuster temporalAdjusterStart = TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY);
        LocalDate startMonday = startDate.with(temporalAdjusterStart);
        LocalDate endMonday = endDate.with(temporalAdjusterEnd);

        //create days list starting with first monday then adding days array(week) to week list
        do{
            daysList.add(startMonday);

            if(daysList.size() == 7){
                LocalDate[] datesList = {daysList.get(0), daysList.get(1), daysList.get(2), daysList.get(3), daysList.get(4), daysList.get(5), daysList.get(6)};
                weekList.add(datesList);
                daysList.clear();
            }
            startMonday = startMonday.plusDays(1);
        }while(startMonday.isBefore(endMonday));
   }

    /**
     * Week list is parsed into strings for use in Filter combo-boxes in GUI.
     * @return parsed list of weeks as strings - ObservableList(String)
     */
   public static ObservableList<String> parseWeekForFilter(){
       String firstDate;
       String lastDate;
       String weekOf;
       ObservableList<String> parsedList = FXCollections.observableArrayList();

       for (LocalDate[] localDates : weekList) {
           LocalDate initialDateValue = localDates[0];
           LocalDate secondDateValue = localDates[6];

           firstDate = initialDateValue.format(formatter);
           lastDate = secondDateValue.format(formatter);
           weekOf = firstDate + " to " + lastDate;
           parsedList.addAll(weekOf);
       }

       return parsedList;
   }

    /**
     * Method used to filter appointments by week
     * A start and end date are used as parameters.
     * The parameters are converted to Timestamps and compared to the appointment Timestamps.
     * @param startDate start date of week - LocalDate
     * @param endDate end date of week - LocalDate
     * @return list of appointments for week - ObservableList(Appointment)
     */
   public static ObservableList<Appointment> filterAppointmentsByWeek(LocalDate startDate, LocalDate endDate){
        String startDateString = startDate.format(formatter);
        String endDateString = endDate.format(formatter);
        Timestamp startTimestamp = Timestamp.valueOf(startDateString + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDateString + " 23:59:59");
        Appointment a = new Appointment(startTimestamp, endTimestamp);
        ObservableList<Appointment> filteredList = FXCollections.observableArrayList();

        for(Appointment b : appointmentList){
            if(b.getStartTimestamp().after(a.getStartTimestamp()) && b.getStartTimestamp().before(a.getEndTimestamp())){
                filteredList.add(b);
            }
        }
        return filteredList;
   }

    /**
     * Filter appointments by month.
     * Accepts a String of the month as a parameter.
     * @param month String of month to filter
     * @return list of appointments for month - ObservableList(Appointment)
     */
   public static ObservableList<Appointment> filterAppointmentsByMonth(String month){
        try {
            ObservableList<Appointment> filteredList = FXCollections.observableArrayList();
            Date date = new SimpleDateFormat("MMM").parse(month);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int monthInt = calendar.get(Calendar.MONTH);

            for (Appointment b : appointmentList) {
                Calendar c = Calendar.getInstance();
                Date d = new Date(b.getStartTimestamp().getTime());
                c.setTime(d);
                if (c.get(Calendar.MONTH) == monthInt) {
                    filteredList.add(b);
                }
            }
            return filteredList;

        }catch (ParseException e){
            System.out.println(e.getMessage());
            return null;
        }
   }

    /**
     * Creates an ObservableList(String) of months.
     * Used in month filter combo boxes in GUI
     */
   public static void createMonthList(){
       String[] months = new DateFormatSymbols().getShortMonths();
       String[] monthsWithoutUndecimber = Arrays.copyOf(months, months.length - 1);
       monthList.addAll(Arrays.asList(monthsWithoutUndecimber));
   }

    public static ObservableList<String> getMonthList() {
        return monthList;
    }

    /**
     * Filter appointments by day
     * Used to filter tableView of Add/update appointment GUIs to allow the user to see scheduled appointments for that day
     * @param date Selected Date to filter
     * @return List of filtered appointments - ObservableList(Appointment)
     */
    //filter by day method used in add/appointment controller
    public static ObservableList<Appointment> filterAppointmentsByDay(LocalDate date){
        ObservableList<Appointment> filteredList = FXCollections.observableArrayList();
        Timestamp selectedDateStart = Timestamp.valueOf(date.toString() + " 00:00:00");
        Timestamp selectedDateEnd = Timestamp.valueOf(date.toString() + " 23:59:59");

        for(Appointment a: appointmentList){
            if(a.getStartTimestamp().after(selectedDateStart) && a.getStartTimestamp().before(selectedDateEnd)){
                filteredList.add(a);
            }
        }
        return filteredList;
    }

    /* Retrieve contact ID list and Generate Unique Appointment ID
    ===================================================================== */

    /**
     * Retrieves a list of contact IDs from database.
     * Used to check if the contact exists in the database.
     * @return list of contact IDs - ObservableList(Integer)
     */
    public static ObservableList<Integer> getContactIDList(){
        try{
            ObservableList<Integer> contactIdList = FXCollections.observableArrayList();
            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "SELECT * FROM contacts";
            statement.execute(query);
            ResultSet resultset = statement.getResultSet();

            while(resultset.next()){
                contactIdList.add(resultset.getInt("Contact_ID"));
            }
            return contactIdList;
        }catch (SQLException | NullPointerException | NumberFormatException e){
            AlertMessages.error("Error: " + e.getMessage());
            return null;
        }
   }

    /**
     * Generates a unique appointment ID
     * Only returns odd numbers between 0 - 1000
     * @return appointmentID - int
     */
    public static int generateAppointmentID(){
        try {
            Random random = new Random();
            int bound = 1000;
            if (bound <= CustomerDatabase.getCustomerList().size()) {
                System.out.println("Appointment capacity reached. Increase bound!");
                return 0;
            }
            int randID = random.nextInt(bound);

            if(randID % 2 == 1) {
                if (searchAppointment(randID) == null) {
                    return randID;
                }
            }
            return generateAppointmentID();
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }


    /* Time conversion and time validation check methods
    ============================================================================ */

    /**
     * Converts a timestamp to UTC timestamp
     * Used when timestamps are stored in database (database only in UTC)
     * @param timestamp timestamp
     * @return timestamp converted to UTC
     */
    public static Timestamp convertToUTC(Timestamp timestamp){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime localTimestamp = ZonedDateTime.of(timestamp.toLocalDateTime(), ZoneId.systemDefault());
        ZonedDateTime utcTimestamp = localTimestamp.withZoneSameInstant(ZoneId.of("UTC", ZoneId.SHORT_IDS));
        return Timestamp.valueOf(dateFormat.format(utcTimestamp));
    }

    /**
     * Convert timestamp to localtime timestamp based on user default system settings
     * Used for any displayed timestamp in GUI
     * @param timestamp timestamp to be converted
     * @return timestamp converted to localtime - timestamp
     */
    public static Timestamp convertToLocalTime(Timestamp timestamp){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime utcTimestamp = ZonedDateTime.of(timestamp.toLocalDateTime(), ZoneId.of("UTC", ZoneId.SHORT_IDS));
        ZonedDateTime localTimestamp = utcTimestamp.withZoneSameInstant(ZoneId.systemDefault());
        return Timestamp.valueOf(dateFormat.format(localTimestamp));
    }

    /**
     * Convert timestamp to EST timestamp
     * Used for appointmentDuringBusinessHours method.
     * business hours are in EST
     * @param timestamp timestamp
     * @return timestamp converted to EST
     */
    public static Timestamp convertToEST(Timestamp timestamp){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime localTimestamp = ZonedDateTime.of(timestamp.toLocalDateTime(), ZoneId.systemDefault());
        ZonedDateTime estTimestamp = localTimestamp.withZoneSameInstant(ZoneId.of("America/New_York", ZoneId.SHORT_IDS));
        return Timestamp.valueOf(dateFormat.format(estTimestamp));
    }

    /**
     * Method to check if an appointment starts within 15 minutes of current time
     * Used for dashboard initialization after log-in.
     * Displays an alert based on if an appointment is within 15 minutes or not
     */
    public static void appointmentWithinFifteenMin(){
        LocalDate today = LocalDate.now();
        long now = (long) Math.floor(Timestamp.valueOf(LocalDateTime.now()).toInstant().getEpochSecond() / 60.0);
        List<String> upcomingAppointments = new ArrayList<>();

        for(Appointment a: filterAppointmentsByDay(today)){
            long appointmentTime = (long) Math.floor(a.getStartTimestamp().toInstant().getEpochSecond() / 60.0); 
            System.out.println(now - appointmentTime);
            System.out.println(appointmentTime - now);

            if ((appointmentTime - now) <= 15 && (appointmentTime - now) >= 0) {
                upcomingAppointments.add("Appointment ID: " + a.getAppointmentID() + " Start Time: " + a.getStartTime() + " Date : " + today + "\n");
            }
            if ((now - appointmentTime) <= 15 && (now - appointmentTime) >= 0)  {
                upcomingAppointments.add("Appointment ID: " + a.getAppointmentID() + " Start Time: " + a.getStartTime() + " Date : " + today + "\n");
            }
        }
        if(upcomingAppointments.size() >= 1){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText( "Appointments within 15 minutes" + "\n" + upcomingAppointments);
            alert.show();
        }
        else{
            AlertMessages.information("No Appointments within next 15 minutes.");
        }
    }

    /**
     * Time validation method to check if an appointment is in the past.
     * Appointments can't be scheduled in the past.
     * @param appointment Appointment checked
     * @return Boolean- true if in past, false if not
     */
    public static Boolean appointmentInPast(Appointment appointment){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentEnd = appointment.getEndTimestamp().toLocalDateTime();

        return now.isAfter(appointmentEnd);
    }

    /**
     * Method to check if appointment is scheduled during business hours ( 8 am - 10 pm EST every day)
     * @param startTimestamp Appointment start timestamp
     * @param endTimestamp Appointment end timestamp
     * @return Boolean - true if appointment during business hours, otherwise false.
     */
    public static Boolean appointmentDuringBusinessHours(Timestamp startTimestamp, Timestamp endTimestamp){
        //checking for valid times
        boolean duringBusinessHrs = true;
        Timestamp estStartTimestamp = convertToEST(startTimestamp);
        Timestamp estEndTimestamp = convertToEST(endTimestamp);
        LocalDateTime startTime = estStartTimestamp.toLocalDateTime();
        LocalDateTime endTime = estEndTimestamp.toLocalDateTime();
        int year = startTime.getYear();
        int day = startTime.getDayOfMonth();
        int month = startTime.getMonthValue();
        LocalDateTime businessOpen = LocalDateTime.of(year, month, day, 7, 59);
        LocalDateTime businessClose = LocalDateTime.of(year, month, day, 22, 1);

        if(startTime.isBefore(businessOpen) || startTime.isAfter(businessClose)){
            duringBusinessHrs = false;
        }
        if(endTime.isBefore(businessOpen) || endTime.isAfter(businessClose)){
            duringBusinessHrs = false;
        }

        return duringBusinessHrs;
    }

    /**
     * Method to validate timestamps
     * Displays an alert for a failed validation check
     * @param startTimestamp appointment start timestamp
     * @param endTimestamp appointment end timestamp
     * @return Boolean- true if invalid timestamp, false if valid
     */
    //checks cases when timestamp is invalid. reduces redundancy
    public static Boolean timestampsInvalid(Timestamp startTimestamp, Timestamp endTimestamp){
        boolean timestampInvalid = false;
        LocalDateTime today = LocalDateTime.now();
        if(startTimestamp.toLocalDateTime().isBefore(today)){
            AlertMessages.warning("Cannot schedule appointments in the past.");
            timestampInvalid = true;
        }
        if(startTimestamp.after(endTimestamp)){
            AlertMessages.warning("Invalid appointment times. Start time occurs after end time.");
            timestampInvalid = true;
        }
        if(startTimestamp.equals(endTimestamp)){
            AlertMessages.warning("Appointment start and end time is the same.");
            timestampInvalid = true;
        }
        return timestampInvalid;
    }

    /**
     * Check for customer appointment overlap
     * @param customerID appointment customer id
     * @param startTimestamp appointment start timestamp
     * @param endTimestamp appointment end timestamp
     * @return boolean- true if overlap
     */
    public static boolean checkAppointmentOverlap(int customerID, Timestamp startTimestamp, Timestamp endTimestamp){
        long startTime = startTimestamp.getTime();
        long endTime = endTimestamp.getTime();
        for(Appointment a: appointmentList){
            long aStartTime = a.getStartTimestamp().getTime();
            long aEndTime = a.getEndTimestamp().getTime();
            if(((aStartTime <= startTime && startTime <= aEndTime) && (aEndTime <= endTime)) || (startTime <= aStartTime && aEndTime <= endTime)){
                if (customerID == a.getCustomerID()) {
                    return true;
                }
            }
            if((aStartTime == endTime || aEndTime == startTime)){
                if(customerID == a.getCustomerID()){
                    return true;
                }
            }
        }
        return false;
    }


    /* Report Generation methods
    ======================================================================== */

    /**
     * Generates a report based on amount of each unique appointment type per month
     * Result is parsed within the ReportsController to display desired data.
     * @return ObservableList(String) - String = YearMonth + type + amount
     */
    public static ObservableList<String> createReportByTypeMonth(){

        //collect months with appointments
        List<YearMonth> months = monthsWithAppointments();

        ObservableList<String> typeMonth = FXCollections.observableArrayList();
        List<String> types = new ArrayList<>();

        for(YearMonth m: months){
            List<String> typeAmount = new ArrayList<>();
            List<String> countTypesList = new ArrayList<>();
            for(Appointment a: appointmentList){
                LocalDateTime l = a.getStartTimestamp().toLocalDateTime();
                YearMonth y = YearMonth.of(l.getYear(), l.getMonth());
                if(y.equals(m)){
                    countTypesList.add(a.getType());
                    if(!types.contains(a.getType())) {
                        types.add(a.getType());
                    }
                }
            }
            Set<String> h = new HashSet<>(countTypesList);
            // counting amount of each type
            for(String s : h){
                typeAmount.add(s + " " + Collections.frequency(countTypesList, s));
            }
            //Final String of YearMonth + (type + amount)
            for( String t: typeAmount){
                typeMonth.add(m.toString() + " " + t);
            }
        }
        return typeMonth;
    }

    /**
     * Creates a schedule for the contact entered as parameter
     * Displayed in reports GUI
     * @param contactID contactID - int
     * @return ObservableList(Appointment) - list of all appointments for contact
     */
    //Schedule for each contact
    public static ObservableList<Appointment> createScheduleForContact(int contactID){
        try {
            ObservableList<Appointment> contactSchedule = FXCollections.observableArrayList();
            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "SELECT * FROM appointments WHERE Contact_ID = '" + contactID + "'";
            statement.execute(query);
            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                Appointment appointment = new Appointment(
                        rs.getInt("Appointment_ID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getString("Type"),
                        rs.getString("Location"),
                        convertToLocalTime(rs.getTimestamp("Start")),
                        convertToLocalTime(rs.getTimestamp("End")),
                        rs.getInt("Customer_ID"),
                        rs.getInt("User_ID"),
                        rs.getInt("Contact_ID"));

                contactSchedule.add(appointment);
            }
            return contactSchedule;
        }catch (SQLException e){
            System.out.println("Invalid contact"  + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a report of the total amount of each appointment start hour.
     * This report's purpose is to help business decision makers decide when
     * to schedule additional staff to meet increased demand.
     * The lambda is used to sort the list by starting hour, lowest - highest.
     * Data is sorted in this manner to increase readability of barChart in reports screen.
     * @return ObservableList(String) - String = startHour + amount
     */
    //Report to determine most common starting appointment times by hour
    public static ObservableList<String> createReportOnStartHour(){
        ObservableList<String> result = FXCollections.observableArrayList();

        List<String> totalHoursAmount = new ArrayList<>();
        List<String> hoursAmount = new ArrayList<>();
        for(Appointment a: appointmentList){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(a.getStartTimestamp().getTime());
            totalHoursAmount.add(String.valueOf(c.get(Calendar.HOUR_OF_DAY)).trim());
        }

        HashSet<String> a = new HashSet<>(totalHoursAmount);

        for(String s: a){
            hoursAmount.add(s + " " + Collections.frequency(totalHoursAmount, s));
        }
        hoursAmount.sort(Comparator.comparingInt(f-> Integer.parseInt(f.substring(0, f.lastIndexOf(' ')).trim())));
        result.addAll(hoursAmount);

        return result;
    }

    /**
     * Method to determine which months have an appointment scheduled.
     * Was originally written inside createReportByTypeMonth method but
     * it was moved here because I had intentions to reuse it in another report. ( never did )
     *
     * The lambda is used to sort through the appointment start timestamps.
     * The timestamp months are collected to a list if the month is unique.
     * @return List<YearMonth> - months with an appointment scheduled
     */
    //returns a list of YearMonths with appointments scheduled
    private static List<YearMonth> monthsWithAppointments(){

        return appointmentList.stream().map(Appointment::getStartTimestamp).map(startTimestamp -> {
            LocalDateTime date = startTimestamp.toLocalDateTime();
            return YearMonth.of(date.getYear(), date.getMonth());
        }).distinct().collect(Collectors.toList());
    }


}