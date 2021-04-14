package view_controller;

import Model.AppointmentDatabase;
import Utils.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller for login screen
 */
public class LoginScreenController implements Initializable {


    @Override
    public void initialize(URL url, ResourceBundle rb){

        //load resource bundle
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Languages", Locale.getDefault());
        usernameLabel.setText(resourceBundle.getString("Username") + ":");
        locationLabel.setText(resourceBundle.getString("Location") + ":");
        passwordLabel.setText(resourceBundle.getString("Password") + ":");
        loginButtonLabel.setText(resourceBundle.getString("Log-in"));
        errorLabel1.setText(resourceBundle.getString("errorLabel1"));
        errorLabel2.setText(resourceBundle.getString("errorLabel2"));


        //display user country
        displayLocationLabel.setText(ZoneId.systemDefault().getId());
    }

    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButtonLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label displayLocationLabel;
    @FXML
    private Label errorLabel1;
    @FXML
    private Label errorLabel2;

    /**
     * Action handler for log-in button.
     * Validates log-in and displays appropriate error messages for invalid log-in attempts
     * @param event ActionEvent
     */
    @FXML
    void loginButtonAction(ActionEvent event){
        try {
            //retrieve user entries
            String user = usernameField.getText();
            String password = passwordField.getText();
            errorLabel1.setVisible(false);
            errorLabel2.setVisible(false);

            //check for empty inputs
            if ((user.equals("") || password.equals(""))){
                errorLabel1.setVisible(true);
                usernameField.clear();
                passwordField.clear();
            }
            else if(DatabaseConnection.verifyLoginDatabase(user, password)){
                System.out.println("Login Successful");
                DatabaseConnection.writeLoginActivity(true);
                DashboardController.loadScreen(event, "Dashboard.fxml");
                AppointmentDatabase.appointmentWithinFifteenMin();
            }
            else{
                errorLabel2.setVisible(true);
                DatabaseConnection.writeLoginActivity(false);
                usernameField.clear();
                passwordField.clear();
            }
        }
        catch(NullPointerException e){
            System.out.println("Please input value.");
        }

    }


}
