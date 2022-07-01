package lto.manager.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RootApplication extends Application {
	public static UserSettings userSettings = new UserSettings();

    @Override
    public void start(Stage stage) throws IOException {
        /*String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();*/
    	
    	
    	//Res.class.getClassLoader().getResource("lto/manager/mail.fxml").getFile();
    	//this.getClass().getClassLoader().ge
        Parent root = FXMLLoader.load(RootApplication.class.getClassLoader().getResource("lto/manager/main.fxml"));
        Scene scene = new Scene(root, userSettings.getRootWindowWidth(), userSettings.getRootWindowHeight());
    
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}