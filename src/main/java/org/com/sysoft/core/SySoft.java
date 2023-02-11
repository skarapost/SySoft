package org.com.sysoft.core;

import org.com.sysoft.database.DbController;

import javafx.application.Application;
import javafx.stage.Stage;

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