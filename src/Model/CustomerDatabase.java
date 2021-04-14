package Model;

import Utils.AlertMessages;
import Utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Class used to manage customers in the database/ application.
 */
public class CustomerDatabase {
    /**
     * List of all customers from database
     */
    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();

    /* GET, ADD, DELETE, UPDATE methods for customerList
    ================================================================= */

    //return stored customers
    public static ObservableList<Customer> getCustomerList(){
        return customerList;
    }

    /**
     * Method to retrieve all customers from the database.
     * Creates customer objects and stores them in customerList
     * @return ObservableList(Customer) list of customers
     */
    public static ObservableList<Customer> getAllCustomersFromDatabase(){
        try {
            Statement stmt = DatabaseConnection.startDatabaseConnection().createStatement();
            String selectStatement = "SELECT * FROM customers";
            stmt.execute(selectStatement);
            ResultSet resultSet = stmt.getResultSet();
            customerList.clear();

            //retrieve all customers
            while (resultSet.next()){
                Customer customer = new Customer(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Phone"),
                        resultSet.getInt("Division_ID"),
                        resultSet.getString("Address"));

                customerList.add(customer);
            }

            return customerList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add customer to customerList.
     * Only adds if list doesn't contain customer and customer is not null
     * @param customer customer to add
     */
    public static void addCustomer(Customer customer){
        if(!customerList.contains(customer) && customer != null) {
            customerList.add(customer);
        }
        else{
            System.out.println("Customer already added.");
        }
    }

    /**
     * Adds customer to database
     * @param customer customer to be added
     */
    public static void addCustomerToDatabase(Customer customer){
        try{
            int id = customer.getCustomerID();
            String name = customer.getName();
            String address = customer.getAddress();
            String postal = customer.getPostalCode();
            String phone = customer.getPhoneNumber();
            int division_id = customer.getDivisionID();
            String created_by = customer.getCreatedBy();

            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "INSERT INTO customers(Customer_ID, Customer_Name, Address, Postal_Code, Phone, Created_By, Division_ID) " +
                    "VALUES('" + id + "', '" + name + "', '" + address + "', '" + postal + "', '" + phone + "', '" + created_by + "', '" + division_id + "')";
            statement.executeUpdate(query);

            System.out.println("Customer added to database.");
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Deletes customer from customerList if the customer has no appointments
     * @param customer customer to delete
     */
    public static void deleteCustomer(Customer customer){
        if(customer != null) {
            if(!checkForCustomerAppointments(customer.getCustomerID())) { // never null
                customerList.remove(customer);
            }
            else{
                System.out.println("Customer has scheduled appointments. Can't delete.");
            }
        }
        else{
            System.out.println("Customer not found.");
        }
    }

    /**
     * Deletes the customer from database if the customer has no appointments.
     * @param customer customer to delete
     */
    public static void deleteCustomerFromDatabase(Customer customer){
        try{
            if(customer == null){
                System.out.println("Customer is null.");
            }
            else {
                if (!checkForCustomerAppointments(customer.getCustomerID())) { // never null
                    Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
                    int id = customer.getCustomerID();
                    String query = "DELETE FROM customers WHERE Customer_ID = '" + id + "'";
                    statement.executeUpdate(query);
                    AlertMessages.information("Customer successfully deleted.");
                } else {
                    AlertMessages.warning("All appointments for customer must be deleted.");
                }
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Updates customer in customerList and database.
     * Timestamp converted to UTC for database.
     * @param index index of old object - int
     * @param customer Customer to be updated
     */
    public static void updateCustomer(int index, Customer customer){
        try {
            Customer c = new Customer(
                    customer.getCustomerID(),
                    customer.getName(),
                    customer.getPostalCode(),
                    customer.getPhoneNumber(),
                    customer.getDivisionID(),
                    customer.getAddress());
            deleteCustomer(customerList.get(index));

            c.setUpdatedBy(DatabaseConnection.getUsername());
            c.setLastUpdate(AppointmentDatabase.convertToUTC(Timestamp.valueOf(LocalDateTime.now())));

            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "UPDATE customers SET Customer_Name = '" + c.getName() + "', Postal_Code = '" + c.getPostalCode() + "', " +
                    "Phone = '" + c.getPhoneNumber() + "', Last_Updated_By = '" + c.getUpdatedBy() + "', Last_Update = '" + c.getLastUpdate() + "', " +
                    "Division_ID = '" + c.getDivisionID() + "', Address = '" + c.getAddress() + "'" +
                    " WHERE Customer_ID = '" + c.getCustomerID() + "'";
            statement.executeUpdate(query);
            addCustomer(c);
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /* Search, Filter Methods and Generate Unique ID method
    ============================================================== */

    /**
     * Search customerList based on customerID
     * @param customerID customerID - int
     * @return Customer object - if found, null - if not found
     */
    public static Customer searchCustomer(int customerID){
        for( Customer c : customerList){
            if(c.getCustomerID() == customerID) {
                return c;
            }
        }
        return null;
    }

    /**
     * Method used to filter customer by country.
     * @param country country - String
     * @return ObservableList(Customer) filtered list of customers
     */
    public static ObservableList<Customer> filterCountries(String country){
        ObservableList<Customer> filterResults = FXCollections.observableArrayList();
        for(Customer c: customerList){
            if(c.getCountry().equals(country)){
                if(!filterResults.contains(c)){
                    filterResults.add(c);
                }
            }
        }
        return filterResults;
    }

    /**
     * Filter customers by first level division (i.e. State/Province)
     * @param firstLevel firstLevel - String
     * @return ObservableList(Customer) list of filtered customers
     */
    public static ObservableList<Customer> filterFirstLevel(String firstLevel){
        ObservableList<Customer> filterResults = FXCollections.observableArrayList();
        for(Customer c: customerList){
            if(c.getFirstLevel().equals(firstLevel)){
                if(!filterResults.contains(c)){
                    filterResults.add(c);
                }
            }
        }
        return filterResults;
    }

    /**
     * Checks database for appointments with customerID entered.
     * Used when a customer delete method is called.
     * @param customerID customerID - int
     * @return Boolean - true if customer has appointments, false if no appointments
     */
    public static Boolean checkForCustomerAppointments(int customerID){
        try {
            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "SELECT * FROM appointments WHERE Customer_ID = '" + customerID +"'";
            statement.execute(query);
            ResultSet resultSet = statement.getResultSet();
            return resultSet.next();

        }catch (SQLException e ){
            System.out.println(e.getMessage());
            return null;
        }

    }

    /**
     * Generated a unique customer ID.
     * Even numbers between 0 - 1000
     * @return Generated CustomerID - int
     */
    public static int generateCustomerID(){
        try {
            Random random = new Random();
            int bound = 1000;
            if (bound <= CustomerDatabase.getCustomerList().size()) {
                System.out.println("Customer capacity reached. Increase bound!");
                return 0;
            }
            int randID = random.nextInt(bound);

            if(randID % 2 == 0) {
                if (searchCustomer(randID) == null && randID != 0) {
                    return randID;
                }
            }
            return generateCustomerID();
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }


}
