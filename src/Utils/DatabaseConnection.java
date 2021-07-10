package Utils;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.ZonedDateTime;

public class DatabaseConnection {

    //Check for active connection
    private static boolean connected = false;
    //JDBC URL components
    private static final String protocol = "JDBC";
    private static final String vendorName = ":mysql:";
    private static final String serverName = "//wgudb.ucertify.com/WJ07LMF";

    //concat to create JDBC URL
    private static final String jdbcUrl= protocol + vendorName + serverName;

    //Driver interface reference
    private static final String mysqlJdbcDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection connection = null;

    //Username
    private static String username = "U07LMF";

    //Password
    private static String password = "53689059476";

    /**
     * Connect to database.
     * @return Connection
     */
    public static Connection startDatabaseConnection()
    {
        try {
            if(!connected) {
                Class.forName(mysqlJdbcDriver);
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                System.out.println("Connection successful.");
                connected = true;
            }
        }
        catch(ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
        }
        return connection;
    }

    /**
     * Close database connect when application is closed.
     */
    public static void closeConnection(){
        try {
            connection.close();
            System.out.println("Connection closed.");
        }
        catch (NullPointerException | SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Verify login information based on data stored in database
     * @param username username - String
     * @param password password - String
     * @return true if valid, false if invalid
     */
    //verify login with database
    public static boolean verifyLoginDatabase(String username, String password){
        try {
            Statement statement = startDatabaseConnection().createStatement();
            String query = "SELECT * FROM users";
            statement.execute(query);
            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                if(rs.getString("User_Name").equals(username) && rs.getString("Password").equals(password)){
                    setUsername(rs.getString("User_Name"));
                    setPassword(rs.getString("Password"));
                    return true;
                }
            }

            return false;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }

    }

    /**
     * Writes login attempts to a txt file.
     * ZonedDateTime, username, and whether the attempt is successful is written.
     * @param successful true if successful login, false if unsuccessful
     */
    //Write login activity
    public static void writeLoginActivity(Boolean successful) {
        try {
            File file = new File("login_activity.txt");
            FileWriter fileWriter = new FileWriter(file, true);

            if(successful){
                fileWriter.write(ZonedDateTime.now() + " User: " + getUsername() + " Attempt: Successful " + "\n");
            }
            else{
                fileWriter.write(ZonedDateTime.now() + " User: " + getUsername() + " Attempt: Failed " + "\n");
            }
            fileWriter.flush();
            fileWriter.close();

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }


    public static String getUsername() {
        return username;
    }

    /**
     * Retrieves username from database of a given userID
     * @param userID userID - int
     * @return username - String
     */
    //retrieve user for updated_by fields
    public static String getUsernameFromDatabase(int userID){
        try{
            String name = "";
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM users WHERE User_ID = '" + userID + "'";
            statement.execute(query);
            ResultSet resultset = statement.getResultSet();
            while(resultset.next()){
                name = resultset.getString("User_Name");
            }

            return name;
        }catch (SQLException e){
            System.out.println("User not found.");
            return "";
        }
    }

    public static void setUsername(String username) {
        DatabaseConnection.username = username;
    }

    public static void setPassword(String password) {
        DatabaseConnection.password = password;
    }
}
