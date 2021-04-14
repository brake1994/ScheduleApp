package view_controller;

import Model.Customer;
import Model.CustomerDatabase;
import Utils.AlertMessages;
import Utils.DatabaseConnection;
import Utils.FirstLevelDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Customer database screen.
 * All customer management is handled in this screen.
 */
public class CustomerDatabaseController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb){
        //populate table
        customerTable.setItems(CustomerDatabase.getAllCustomersFromDatabase());
        customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        firstLevel.setCellValueFactory(new PropertyValueFactory<>("firstLevel"));
        country.setCellValueFactory(new PropertyValueFactory<>("country"));

        filterCountryComboBox.setItems(FirstLevelDivision.getCountriesList());
        filterFirstLevelComboBox.setVisible(false);
        filterFirstLevelLabel.setVisible(false);

        allowInputFieldsEdit(false);
        customerIDTextfield.setEditable(false);
        setCancelChangesButtonBehavior("Default");
        setSaveChangesButtonBehavior("Default");
    }

    //table FXIDs
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> customerID;
    @FXML
    private TableColumn<Customer, String> name;
    @FXML
    private TableColumn<Customer, String> address;
    @FXML
    private TableColumn<Customer, String> postalCode;
    @FXML
    private TableColumn<Customer, String> phone;
    @FXML
    private TableColumn<Customer, String> firstLevel;
    @FXML
    private TableColumn<Customer, String> country;

    //Input FXIDs
    @FXML
    private TextField customerIDTextfield;
    @FXML
    private TextField customerNameTextfield;
    @FXML
    private TextField customerPhoneTextfield;
    @FXML
    private TextField customerStreetAddressTextfield;
    @FXML
    private TextField customerCityTextfield;
    @FXML
    private TextField customerPostalTextfield;
    @FXML
    private ComboBox<String> customerCountryComboBox;
    @FXML
    private Label firstLevelLabel;
    @FXML
    private ComboBox<String> customerFirstLevelComboBox;
    @FXML
    private Button CancelChangesButton;
    @FXML
    private Button SaveChangesButton;

    //filter and search FXIDs
    @FXML
    private TextField searchTableTextfield;
    @FXML
    private Label filterFirstLevelLabel;
    @FXML
    private ComboBox<String> filterCountryComboBox;
    @FXML
    private ComboBox<String> filterFirstLevelComboBox;


    /* Customer table action buttons
     =============================================================== */

    /**
     * Add customer button handler.
     */
    @FXML
    void onActionAddButton() {
        try {
            allowInputFieldsEdit(true);
            clearInputFields();
            customerCountryComboBox.setItems(FirstLevelDivision.getCountriesList());
            firstLevelLabel.setVisible(false);
            customerFirstLevelComboBox.setVisible(false);
            customerIDTextfield.setText(String.valueOf(CustomerDatabase.generateCustomerID()));
            setSaveChangesButtonBehavior("Add");
            setCancelChangesButtonBehavior("Add");

        }
        catch(NullPointerException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Delete customer event handler
     * User must confirm deletion.
     */
    @FXML
    void onActionDeleteButton() {
        if(!customerTable.getSelectionModel().isEmpty()){
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setContentText("Delete selected customer?");
            a.showAndWait();
            if (a.getResult() == ButtonType.OK) {
                Customer c = customerTable.getSelectionModel().getSelectedItem();
                CustomerDatabase.deleteCustomer(c);
                CustomerDatabase.deleteCustomerFromDatabase(c);
            }
            else{
                a.close();
            }
        }
        else{
            AlertMessages.warning("Select a customer.");
        }
    }

    /**
     * Update customer event handler.
     * Selected customer fields are loaded into text fields
     */
    @FXML
    void onActionUpdateButton() {
        Customer c = customerTable.getSelectionModel().getSelectedItem();
        if(c != null){
            allowInputFieldsEdit(true);
            customerCountryComboBox.setItems(FirstLevelDivision.getCountriesList());
            populateInputFields(c);
            setCancelChangesButtonBehavior("Update");
            setSaveChangesButtonBehavior("Update");
        }
        else{
            AlertMessages.warning("Select a customer.");
        }
    }

    /**
     * View customer event handler.
     * Display selected customer information in textfields for easier readability.
     * Edits are disabled.
     * Cancel button clears the textfields.
     */
    @FXML
    void onActionViewButton() {
        try{
            Customer c = customerTable.getSelectionModel().getSelectedItem();
            if(c != null){
                populateInputFields(c);
                allowInputFieldsEdit(false);
                setCancelChangesButtonBehavior("View");
                setSaveChangesButtonBehavior("View");
                firstLevelLabel.setVisible(true);
            }
            else{
                AlertMessages.information("Select a customer.");
            }
        }
        catch (NullPointerException e ){
            System.out.println(e.getMessage());
        }
    }


    /* filter action events
    =================================================================== */

    /**
     * Filter customer table by selected country.
     * First level label/ combo-box is set based on country.
     */
    @FXML
    void onActionCountryFilter() {
        try {
            if(filterCountryComboBox.getSelectionModel().isEmpty()){
                filterFirstLevelLabel.setVisible(false);
                filterFirstLevelComboBox.setVisible(false);
            }
            if (filterCountryComboBox.getSelectionModel().isSelected(0)) {
                filterFirstLevelLabel.setVisible(true);
                filterFirstLevelComboBox.setVisible(true);
                filterFirstLevelComboBox.setItems(FirstLevelDivision.getProvincesList());
                filterFirstLevelLabel.setText("Province:");
            }
            if (filterCountryComboBox.getSelectionModel().isSelected(1)) {
                filterFirstLevelLabel.setVisible(true);
                filterFirstLevelComboBox.setVisible(true);
                filterFirstLevelComboBox.setItems(FirstLevelDivision.getEnglandFirstLevelList());
                filterFirstLevelLabel.setText("Region:");
            }
            if (filterCountryComboBox.getSelectionModel().isSelected(2)) {
                filterFirstLevelLabel.setVisible(true);
                filterFirstLevelComboBox.setVisible(true);
                filterFirstLevelComboBox.setItems(FirstLevelDivision.getStatesList());
                filterFirstLevelLabel.setText("State:");
            }
        }
        catch(NullPointerException e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * Apply the select country/first level division filter.
     */
    @FXML
    void ApplyFilterButton() {
        try{
            if(searchTableTextfield.getText().trim().isEmpty()) {
                if (!filterCountryComboBox.getSelectionModel().isEmpty() && filterFirstLevelComboBox.getSelectionModel().isEmpty()) {
                    customerTable.setItems(CustomerDatabase.filterCountries(filterCountryComboBox.getValue()));
                }
                if (!filterCountryComboBox.getSelectionModel().isEmpty() && !filterFirstLevelComboBox.getSelectionModel().isEmpty()) {
                    customerTable.setItems(CustomerDatabase.filterFirstLevel(filterFirstLevelComboBox.getValue()));
                }
            }
            else{
                if (!filterCountryComboBox.getSelectionModel().isEmpty() && filterFirstLevelComboBox.getSelectionModel().isEmpty()) {
                    if(Objects.requireNonNull(customerSearchAction()).getCountry().equals(filterCountryComboBox.getSelectionModel().getSelectedItem())) {
                        customerSearchAction();
                    }
                    else{
                        customerTable.setItems(CustomerDatabase.getAllCustomersFromDatabase());
                        AlertMessages.information("No customers found!");
                    }
                }
                if (!filterCountryComboBox.getSelectionModel().isEmpty() && !filterFirstLevelComboBox.getSelectionModel().isEmpty()) {
                    if(Objects.requireNonNull(customerSearchAction()).getFirstLevel().equals(filterFirstLevelComboBox.getSelectionModel().getSelectedItem())) {
                        customerSearchAction();
                    }
                    else{
                        customerTable.setItems(CustomerDatabase.getAllCustomersFromDatabase());
                        AlertMessages.information("No customers found!");
                    }
                }

            }
        }
        catch (NullPointerException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sets first level label + combo-box based on country.
     */
    @FXML
    void onActionCustomerCountry() {
        try {
            if(customerCountryComboBox.getSelectionModel().isEmpty()){
                firstLevelLabel.setVisible(false);
                customerFirstLevelComboBox.setVisible(false);
            }
            if (customerCountryComboBox.getSelectionModel().isSelected(0)) {
                firstLevelLabel.setVisible(true);
                customerFirstLevelComboBox.setVisible(true);
                customerFirstLevelComboBox.setItems(FirstLevelDivision.getProvincesList());
                setFirstLevelLabel(customerCountryComboBox.getValue());
            }
            if (customerCountryComboBox.getSelectionModel().isSelected(1)) {
                firstLevelLabel.setVisible(true);
                customerFirstLevelComboBox.setVisible(true);
                customerFirstLevelComboBox.setItems(FirstLevelDivision.getEnglandFirstLevelList());
                setFirstLevelLabel(customerCountryComboBox.getValue());
            }
            if (customerCountryComboBox.getSelectionModel().isSelected(2)) {
                firstLevelLabel.setVisible(true);
                customerFirstLevelComboBox.setVisible(true);
                customerFirstLevelComboBox.setItems(FirstLevelDivision.getStatesList());
                setFirstLevelLabel(customerCountryComboBox.getValue());
            }
        }
        catch(NullPointerException e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * Reset current filter.
     * Set table to all customers.
     */
    @FXML
    void ResetFilterButton() {
        customerTable.setItems(CustomerDatabase.getCustomerList());
        filterCountryComboBox.getSelectionModel().clearSelection();
        filterFirstLevelComboBox.getSelectionModel().clearSelection();
        customerTable.refresh();
    }

    /**
     * Search customer event handler.
     * Searches for customer based on customer ID
     * Filter option must be empty to perform search.
     */
    @FXML
    void OnActionSearchCustomer() {
       if(filterFirstLevelComboBox.getSelectionModel().isEmpty() && filterCountryComboBox.getSelectionModel().isEmpty()) {
           customerSearchAction();
       }
       else{
           AlertMessages.information("Please click reset filter button to begin search.");
       }
    }


    /* Navigation button actions
    =================================================================== */

    /**
     * Loads All appointments screen
     * @param event ActionEvent
     */
    @FXML
    void AppointmentsButton(ActionEvent event) {
        DashboardController.loadScreen(event, "AllAppointments.fxml");
    }

    /**
     * Loads dashboard screen
     * @param event ActionEvent
     */
    @FXML
    void DashboardButton(ActionEvent event) {
        DashboardController.loadScreen(event, "Dashboard.fxml");
    }

    /**
     * Load reports screen
     * @param event ActionEvent
     */
    @FXML
    void ReportsButton(ActionEvent event) {
        DashboardController.loadScreen(event, "Reports.fxml");
    }

    /* Methods for this controller only
    ======================================================================== */

    /**
     * Disable/enable input fields based on boolean parameter.
     * @param editable boolean - true = enabled, false = disabled.
     */
    //disable input all fields
    private void allowInputFieldsEdit(boolean editable){
        customerNameTextfield.setEditable(editable);
        customerPhoneTextfield.setEditable(editable);
        customerCityTextfield.setEditable(editable);
        customerPostalTextfield.setEditable(editable);
        customerStreetAddressTextfield.setEditable(editable);

        firstLevelLabel.setVisible(editable);
    }

    /**
     * Method used to populate input fields when a customer is selected via view or update button.
     * @param c Customer selected
     */
    private void populateInputFields(Customer c){
        customerIDTextfield.setText(Integer.toString(c.getCustomerID()));
        customerNameTextfield.setText(c.getName());
        customerPhoneTextfield.setText(c.getPhoneNumber());
        customerPostalTextfield.setText(c.getPostalCode());
        customerStreetAddressTextfield.setText(c.getStreetAddress());
        customerCityTextfield.setText(c.getCity());
        customerCountryComboBox.getSelectionModel().select(c.getCountry());
        customerFirstLevelComboBox.setVisible(true);
        firstLevelLabel.setVisible(true);
        setFirstLevelLabel(c.getCountry());
        customerFirstLevelComboBox.getSelectionModel().select(c.getFirstLevel());
    }

    /**
     * Clear all input fields.
     * Used when cancel button is clicked.
     */
    private void clearInputFields(){
        customerIDTextfield.clear();
        customerNameTextfield.clear();
        customerPhoneTextfield.clear();
        customerPostalTextfield.clear();
        customerStreetAddressTextfield.clear();
        customerCityTextfield.clear();
        customerCountryComboBox.getSelectionModel().clearSelection();
        firstLevelLabel.setVisible(false);
        customerFirstLevelComboBox.setVisible(false);
        customerFirstLevelComboBox.getSelectionModel().clearSelection();
    }

    /**
     * Sets First level label based on country
     * @param country country input - String
     */
    private void setFirstLevelLabel(String country) {
        try {
            switch (country) {
                case "United States":
                    firstLevelLabel.setText("State:");
                    break;
                case "Canada":
                    firstLevelLabel.setText("Province:");
                    break;
                case "England":
                    firstLevelLabel.setText("Region:");
                    break;
            }
        }
        catch(NullPointerException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Cancel button behavior method based on selected case.
     * The lambda is used to call the actions of the change button event handler
     * @param buttonUsed Switch statement parameter based on button used prior to cancel button.
     */
    //method to determine how cancel button behaves depending on what is happening
    private void setCancelChangesButtonBehavior(String buttonUsed){
        switch (buttonUsed){
            case "Default":
                CancelChangesButton.setDisable(true);
                break;
            case "View":
                CancelChangesButton.setDisable(false);
                CancelChangesButton.setOnAction(actionEvent -> {
                    clearInputFields();
                    setCancelChangesButtonBehavior("Default");
                });
                break;
            case "Add":
            case "Update":
                CancelChangesButton.setDisable(false);
                CancelChangesButton.setOnAction(actionEvent -> {
                    clearInputFields();
                    customerTable.refresh();
                    setCancelChangesButtonBehavior("Default");
                });
                break;
        }
    }

    /**
     * Save changes button behavior method based on selected case.
     * Lambda is used to call action for event handler.
     * Saved changes are checked for validity and appropriate error messages are displayed
     * for invalid inputs.
     * @param buttonUsed Switch statement parameter based on previous button used.
     */
    private void setSaveChangesButtonBehavior(String buttonUsed){
        switch (buttonUsed){
            case "Default":
            case "View":
                SaveChangesButton.setDisable(true);
                break;
            case "Add":
                SaveChangesButton.setDisable(false);
                SaveChangesButton.setOnAction(actionEvent -> {
                    try{
                        //new customer values
                        int id = Integer.parseInt(customerIDTextfield.getText());
                        String name = customerNameTextfield.getText().trim();
                        String phone = customerPhoneTextfield.getText().trim();
                        String postal = customerPostalTextfield.getText().trim();
                        String streetAddress = customerStreetAddressTextfield.getText().trim();
                        String city = customerCityTextfield.getText().trim();

                        TextField[] testfield = {customerIDTextfield, customerNameTextfield, customerPhoneTextfield, customerPostalTextfield, customerStreetAddressTextfield, customerCityTextfield};
                        boolean inputsValid = true;
                        for(TextField t: testfield){
                            if(t == null || t.getText().trim().isEmpty()) {
                                AlertMessages.warning("Fill in all fields.");
                                inputsValid = false;
                            }
                        }
                        if(customerCountryComboBox.getSelectionModel().isEmpty() || customerFirstLevelComboBox.getSelectionModel().isEmpty()){
                            inputsValid = false;
                            AlertMessages.warning("Fill in all fields.");
                        }
                        if(inputsValid) {
                            String firstLevel = customerFirstLevelComboBox.getValue().trim();
                            //retrieve division_id
                            Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
                            String query = "SELECT Division_ID FROM first_level_divisions WHERE Division = '" + firstLevel + "'";
                            statement.execute(query);
                            ResultSet rs = statement.getResultSet();
                            rs.next();
                            int division_ID = rs.getInt(1);

                            Customer customer = new Customer(id, name, postal, phone, division_ID, streetAddress, city);
                            CustomerDatabase.addCustomer(customer);
                            CustomerDatabase.addCustomerToDatabase(customer);
                            customerTable.refresh();
                            clearInputFields();
                            setSaveChangesButtonBehavior("Default");
                            AlertMessages.information("Customer successfully added.");
                        }
                    }
                    catch (SQLException e){
                        e.printStackTrace();
                    }
                    catch (NullPointerException e){
                        AlertMessages.warning("Fill in " + e);
                    }
                });
                break;
            case "Update":
                SaveChangesButton.setDisable(false);
                SaveChangesButton.setOnAction(actionEvent -> {
                   try { //new customer values
                       int index = CustomerDatabase.getCustomerList().indexOf(customerTable.getSelectionModel().getSelectedItem());
                       int id = Integer.parseInt(customerIDTextfield.getText());
                       String name = customerNameTextfield.getText().trim();
                       String phone = customerPhoneTextfield.getText().trim();
                       String postal = customerPostalTextfield.getText().trim();
                       String streetAddress = customerStreetAddressTextfield.getText().trim();
                       String city = customerCityTextfield.getText().trim();

                       TextField[] testfield = {customerIDTextfield, customerNameTextfield, customerPhoneTextfield, customerPostalTextfield, customerStreetAddressTextfield, customerCityTextfield};
                       boolean inputsValid = true;
                       for(TextField t: testfield){
                           if(t == null || t.getText().trim().isEmpty()) {
                               AlertMessages.warning("Fill in all fields.");
                               inputsValid = false;
                           }
                       }
                       if(customerCountryComboBox.getSelectionModel().isEmpty() || customerFirstLevelComboBox.getSelectionModel().isEmpty()){
                           inputsValid = false;
                           AlertMessages.warning("Fill in all fields.");
                       }
                       if(inputsValid) {
                           String firstLevel = customerFirstLevelComboBox.getValue().trim();

                           //retrieve division_id
                           Statement statement = DatabaseConnection.startDatabaseConnection().createStatement();
                           String query = "SELECT Division_ID FROM first_level_divisions WHERE Division = '" + firstLevel + "'";
                           statement.execute(query);
                           ResultSet rs = statement.getResultSet();
                           rs.next();
                           int division_ID = rs.getInt(1);

                           Customer c = new Customer(id, name, postal, phone, division_ID, streetAddress, city);
                           CustomerDatabase.updateCustomer(index, c);
                           customerTable.refresh();
                           clearInputFields();
                           setSaveChangesButtonBehavior("Default");
                           AlertMessages.information("Customer successfully updated.");
                       }
                   }
                   catch (SQLException e){
                       System.out.println(e.getMessage());
                   }
                   catch (NullPointerException e){
                       AlertMessages.warning("Fill in " + e);
                   }
                });
        }
    }

    /**
     * Checks if String input is a number.
     * @param string String
     * @return boolean- true if number, false if not number
     */
    private boolean isNumber(String string){
        for ( int i = 0; i < string.length(); i++){
            if(!Character.isDigit(string.charAt(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * Search customer based on customer ID.
     * Checks if the searchTableTextfield input is valid (numbers only).
     * @return Customer found or null if no customer found.
     */
    private Customer customerSearchAction(){
        String search = searchTableTextfield.getText().trim();
        ObservableList<Customer> searchResults = FXCollections.observableArrayList();
        if(isNumber(search) && !search.isEmpty()){
            int searchID = Integer.parseInt(search);
            Customer c = CustomerDatabase.searchCustomer(searchID);
            searchResults.add(c);
            customerTable.setItems(searchResults);
            return c;
        }
        else{
            AlertMessages.information("Customer not found.");
            customerTable.setItems(CustomerDatabase.getAllCustomersFromDatabase());
        }
        customerTable.refresh();
        return null;
    }
}
