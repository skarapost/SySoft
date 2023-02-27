package sysoft.core;

import javafx.application.Application;
import javafx.stage.Stage;
import sysoft.database.DbController;

public class SySoft extends Application {
    

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws ClassNotFoundException {
    	DbController.initialiseDatabase();
    	
    	Layout.getInstance().configureLayout(primaryStage);

    	Layout.getInstance().setLayoutBehaviours(primaryStage);
    }
    
}