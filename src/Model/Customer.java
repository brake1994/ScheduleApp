package Model;

import Utils.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Class to create Customer objects
 */
public class Customer {
    //customer information
    private int customerID;
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty address = new SimpleStringProperty();
    private SimpleStringProperty postalCode = new SimpleStringProperty();
    private SimpleStringProperty phoneNumber = new SimpleStringProperty();
    private Timestamp createDate;
    private final SimpleStringProperty createdBy = new SimpleStringProperty();
    private String country;
    private String firstLevel;
    private String streetAddress;
    private String city;
    private int divisionID;
    private Timestamp lastUpdate;
    private String updatedBy;

    /* Constructors
    ===================================================================== */

    /**
     * Customer constructor used in database data retrieval
     * @param customerID customerID - int
     * @param name customer name - String
     * @param postalCode customer postalCode - String
     * @param phoneNumber customer phone - String
     * @param divisionID customer DivisionID - int
     * @param address customer address - String
     */
    //used for database retrieval
    public Customer(int customerID, String name, String postalCode, String phoneNumber, int divisionID, String address){
        this.customerID = customerID;
        setName(name);
        setPostalCode(postalCode);
        setPhoneNumber(phoneNumber);
        parseDivisionData(divisionID);
        parseAddressFromDatabase(address);
        setCreatedBy(DatabaseConnection.getUsername());
        setCreateDate(Timestamp.valueOf(LocalDateTime.now()));

    }

    /**
     * Customer constructor used when a customer is saved in the CustomerDatabaseController.
     * Customer objects created with this are used by methods that write to database
     * @param customerID customerID - int
     * @param name customer name - String
     * @param postalCode customer postalCode - String
     * @param phoneNumber customer phone - String
     * @param divisionID customer divisionID - int
     * @param streetAddress customer street address - String
     * @param city customer city - String
     */
    public Customer(int customerID, String name, String postalCode, String phoneNumber, int divisionID, String streetAddress, String city){
        this.customerID = customerID;
        setName(name);
        setPostalCode(postalCode);
        setPhoneNumber(phoneNumber);
        parseDivisionData(divisionID);
        createAddress(streetAddress, city, this.country);
        setCreatedBy(DatabaseConnection.getUsername());
        setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    /* Address Methods
    ========================================================================= */

    /**
     * Method used to create address in the correct format based on country
     * @param streetAddress customer street address - String
     * @param city customer city - String
     * @param country customer country - String
     */
    //concat address
    public void createAddress(String streetAddress, String city, String country){
        switch(country){
            case "United States":
            case "Canada":
                this.address.set(streetAddress + ", " + city);
                break;
            case "England":
                this.address.set(streetAddress + ", " + city + ", " + firstLevel);
        }
    }

    /**
     * Parse division data from database based on divisionID
     * Sets the customer country to the matching ID
     * @param divisionID divisionID - int
     */
    //Parse information from Division ID
    public void parseDivisionData(int divisionID){
        try {
            this.divisionID =divisionID;
            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "SELECT Division_ID, Division, COUNTRY_ID FROM first_level_divisions";
            statement.execute(query);
            ResultSet resultSet = statement.getResultSet();
            while(resultSet.next()) {
                if(divisionID == resultSet.getInt("Division_ID")) {
                    this.firstLevel = resultSet.getString("Division");
                    int country_id = resultSet.getInt("COUNTRY_ID");
                    switch (country_id){
                        case 38:
                            this.country = "Canada";
                            break;
                        case 230:
                            this.country = "England";
                            break;
                        case 231:
                            this.country = "United States";
                            break;
                    }
                }
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Retrieve address from database and parses into customer variables.
     * The variables parsed are: streetAddress and City
     * @param address Retrieved address - String
     */
    public void parseAddressFromDatabase(String address){
        try {
            setAddress(address);
            int indexOfComma = address.indexOf(',');
            String part1 = address.substring(0, indexOfComma).trim();
            String part2 = address.substring(indexOfComma + 1).trim();

            //removing first level division from england addresses
            if(part2.contains(",")){
                int secondCommaIndex = part2.indexOf(',');
                part2 = part2.substring(0, secondCommaIndex).trim();
            }
            setStreetAddress(part1);
            setCity(part2);
        }catch (StringIndexOutOfBoundsException e){
            System.out.println("Address index out of bounds.");
        }
    }


    /* Getters
    ==================================================================== */

    public int getCustomerID() {
        return customerID;
    }

    public String getName(){
        return name.get();
    }

    public String getCountry() {
        return country;
    }

    public String getFirstLevel() {
        return firstLevel;
    }

    public int getDivisionID() {
        return divisionID;
    }

    public String getAddress(){
        return address.get();
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public String getCreatedBy() {
        return createdBy.get();
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /* Setters
    =========================================================================== */

    public void setName(String name){
        this.name.set(name);
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy.set(createdBy);
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
