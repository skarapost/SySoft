package sysoft.core;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Alerts {

    private Alerts(){
    }

    public static void fireNoFieldForNameAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Caution");
        alert.setHeaderText(null);
        alert.setContentText("There is no field with this name");
        alert.showAndWait();
    }

    public static void fireMissingDatabaseAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Caution");
        alert.setHeaderText(null);
        alert.setContentText("Please connect a database");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK)
            System.exit(1);
    }

    public static void fireDefault3FieldsInTableAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Caution");
        alert.setHeaderText(null);
        alert.setContentText("There can not be less than 3 fields");
        alert.showAndWait();
    }

    public static void fireStatusInsertionAlert(boolean status) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Information");
        alert.setContentText("The insertion was " + (status ? "successful" : "unsuccessful"));
        alert.showAndWait();
    }

    public static void fireAtLeastOneFieldAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Information");
        alert.setContentText("Please fill at least one field");
        alert.showAndWait();
    }

    public static void fireMissingSelectedRowAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Caution");
        alert.setHeaderText(null);
        alert.setContentText("Please select a row");
        alert.showAndWait();
    }

    public static void fireAboutAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Creator: Karapostolakis Sotirios\nE-mail: sotkar14@gmail.com");
        alert.showAndWait();
    }

    public static void fireMissing2SearchOptionsAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Caution");
        alert.setHeaderText(null);
        alert.setContentText("Please check 2 search options");
        alert.showAndWait();
    }
}
