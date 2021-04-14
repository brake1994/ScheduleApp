package Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class with various methods and variables related to locations
 */
public class FirstLevelDivision {

    /**
     * List of all states in United States from Database
     */
    private static final ObservableList<String> statesList = FXCollections.observableArrayList();
    /**
     * List of all provinces in Canada from Database
     */
    private static final ObservableList<String> provincesList = FXCollections.observableArrayList();
    /**
     * List of first level divisions in England from Database
     */
    private static final ObservableList<String> englandFirstLevelList = FXCollections.observableArrayList();

    /**
     * List of countries used to populate country filter combo-box
     */
    //used to populate country filter combo-box
    private static final ObservableList<String> countriesList = FXCollections.observableArrayList("Canada", "England", "United States");

    /**
     * Return countries List
     * @return List of all countries- ObservableList(String)
     */
    public static ObservableList<String> getCountriesList() {
        return countriesList;
    }

    /**
     * Retrieve list of states stored in database
     * @return ObservableList(String) - List of states
     */
    public static ObservableList<String> getStatesList() {
        try {
            Statement stmt = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "SELECT Division FROM first_level_divisions WHERE COUNTRY_ID = 231";
            stmt.execute(query);
            ResultSet rs = stmt.getResultSet();
            while(rs.next()){
                if(!statesList.contains(rs.getString("Division"))) {
                    statesList.add(rs.getString("Division"));
                }
            }
            return statesList;
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Retrieve provinces from database
     * @return ObservableList(String) - list of provinces
     */
    public static ObservableList<String> getProvincesList() {
        try {
            Statement stmt = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "SELECT Division FROM first_level_divisions WHERE COUNTRY_ID = 38";
            stmt.execute(query);
            ResultSet rs = stmt.getResultSet();
            while(rs.next()){
                if(!provincesList.contains(rs.getString("Division"))) {
                    provincesList.add(rs.getString("Division"));
                }
            }
            return provincesList;
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Retrieve england first level divisions from database
     * @return ObservableList(String) - list of first level divisions in england
     */
    public static ObservableList<String> getEnglandFirstLevelList() {
        try {
            Statement stmt = DatabaseConnection.startDatabaseConnection().createStatement();
            String query = "SELECT Division FROM first_level_divisions WHERE COUNTRY_ID = 230";
            stmt.execute(query);
            ResultSet rs = stmt.getResultSet();
            while(rs.next()){
                if(!englandFirstLevelList.contains(rs.getString("Division"))) {
                    englandFirstLevelList.add(rs.getString("Division"));
                }
            }
            return englandFirstLevelList;
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}

