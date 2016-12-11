package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    Group root = new Group();
    Scene scene = new Scene(root, 1280, 720, Color.web("#1F4060"));

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ColdMail - Homepage");
        OpenHomepage(primaryStage);
    }

    public void OpenHomepage(Stage primaryStage){
        GridPane gridPane = new GridPane();
        // gridPane.setGridLinesVisible(true);
        gridPane.setPadding(new Insets(400, 0, 0, 450));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        Rectangle border = new Rectangle();
        border.setFill(Color.LIGHTGRAY);
        border.setX(350);
        border.setY(120);
        border.setWidth(600);
        border.setHeight(450);
        border.setArcWidth(20);
        border.setArcHeight(20);

        Image logo = new Image("coldMail2.png");
        ImageView imageView = new ImageView();
        imageView.setFitWidth(500);
        imageView.setFitHeight(250);
        imageView.setX(400);
        imageView.setY(150);

        imageView.setImage(logo);

        Label userLabel = new Label("Username");
        gridPane.add(userLabel, 1,1 );
        TextField userField = new TextField();
        userField.setPrefWidth(300);
        userField.setPromptText("youremail@domain.com");
        GridPane.setHalignment(userField, HPos.LEFT);
        gridPane.add(userField, 2, 1);

        ObservableList<String> emailDomain = FXCollections.observableArrayList(
                "GMail",
                "Windows Live Hotmail",
                "Outlook",
                "Rogers",
                "Yahoo! Mail"
        );

        final ComboBox emailDropdown = new ComboBox(emailDomain);
        emailDropdown.setValue("GMail");
        emailDropdown.setMaxWidth(300);
        gridPane.add(emailDropdown,2,3);

        Label passLabel = new Label("Password");
        gridPane.add(passLabel, 1, 2);
        TextField passField = new PasswordField();
        passField.setPromptText("");
        GridPane.setHalignment(passField, HPos.LEFT);
        gridPane.add(passField, 2, 2);

        //userField.setText("csci3070U@gmail.com");
        //passField.setText("csci3070UU");

        root.getChildren().add(border);
        root.getChildren().add(gridPane);
        root.getChildren().add(imageView);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}


