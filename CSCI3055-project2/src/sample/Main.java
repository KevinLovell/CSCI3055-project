/*
    Authors:
    Mitchell Childerhose (100553756)
    Kevin Lovell (100559665)

    Date:
    December 13th, 2016

    Description:
    Cold Mail application
 */

package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    Group root = new Group();
    Scene scene = new Scene(root, 1280, 720, Color.web("#1F4060"));

    Stage stage = new Stage();
    Group layout = new Group();
    Scene scene2 = new Scene(layout, 1280, 720, Color.web("#1F4060"));

    private int index;
    private Store store;
    Stage emailStage = new Stage();
    Group emailLayout = new Group();
    Scene scene3 = new  Scene(emailLayout, 1280, 720, Color.web("#1F4060"));
    private String host;
    private ConnectionThread connectionThread = null;
    private ObservableList<email> emails = FXCollections.observableArrayList();

    public static void alertBox(String alertMessage, String title)
    {
        alertBox(alertMessage, title, null);
    }

    public static void alertBox(String alertMessage, String title, String alertHeader)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(alertHeader);
        alert.setContentText(alertMessage);
        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ColdMail - Homepage");
        OpenHomepage(primaryStage);
    }

    public void OpenHomepage(Stage primaryStage){
        GridPane gridPane = new GridPane();
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

        userField.setText("csci3070U@gmail.com");
        passField.setText("csci3070UU");

        Button Login = new Button("Login");
        Login.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                final String username = userField.getText();
                final String password = passField.getText();
                final String client = emailDropdown.getValue().toString();
                getHost(client);
                if(userField.getText().isEmpty() || passField.getText().isEmpty()) {
                    alertBox("Missing Credentials","Error");
                } else {
                    boolean failed = true;
                    Properties props = new Properties();
                    props.setProperty("mail.store.protocol", "imaps");

                    try {
                        Session session = Session.getInstance(props, null);
                        store = session.getStore();
                        store.connect(host, username, password);

                    } catch(AuthenticationFailedException ex) {
                        failed = false;
                    } catch (NoSuchProviderException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    System.out.println(host);
                    if(!failed) {
                        alertBox("Login Failed","Error");
                    } else {
                        System.out.println(host);
                        primaryStage.close();
                        email(username, password, primaryStage);
                    }
                }
            }
        });
        Login.setTranslateX(70);
        Login.setMaxWidth(70);
        gridPane.add(Login, 2, 4);

        Button Close = new Button("Close");
        Close.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                System.exit(0);
            }
        });
        Close.setTranslateX(150);
        Close.setMaxWidth(70);
        gridPane.add(Close, 2, 4);

        root.getChildren().add(border);
        root.getChildren().add(gridPane);
        root.getChildren().add(imageView);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public void getHost(String client) {
        if(client == "GMail") {
            this.host = "imap.gmail.com";
        } else if(client == "Windows Live Hotmail" || client == "Outlook") {
            this.host = "imap-mail.outlook.com";
        } else if(client == "Yahoo! Mail") {
            this.host = "imap.mail.yahoo.com";
        } else if(client == "Rogers") {
            this.host = "imap.mail.yahoo.com";
        }
    }

    public void email(String username, String password, Stage primaryStage){
        Label messageNum = new Label("");
        messageNum.setTextFill(Color.WHITE);
        messageNum.setTranslateX(50);
        messageNum.setTranslateY(50);
        Label messagesLabel = new Label("Message");
        messagesLabel.setTextFill(Color.WHITE);
        messagesLabel.setTranslateX(720);
        messagesLabel.setTranslateY(50);

        connectionThread = new ConnectionThread(username, password, host);

        stage.setTitle("Mail "+username+" ColdMail");

        stage.setScene(scene2);
        stage.setResizable(false);
        stage.show();

        TextArea emailWindow = new TextArea();
        emailWindow.setEditable(false);
        emailWindow.setPrefHeight(615);
        emailWindow.setPrefColumnCount(40);
        emailWindow.setWrapText(true);
        emailWindow.setTranslateX(700);
        emailWindow.setTranslateY(75);

        Image bottomBanner = new Image("coldMail3.png");
        ImageView bannerImage = new ImageView();
        bannerImage.setY(345);
        bannerImage.setImage(bottomBanner);

        ListView<String> list = new ListView<String>();
        list.setItems(connectionThread.getAllMail());
        list.setPrefWidth(580);
        list.setPrefHeight(615);
        list.setTranslateX(30);
        list.setTranslateY(75);

        messageNum.setText(connectionThread.getNumberOfMsg());

        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                index = list.getSelectionModel().getSelectedIndex();

                Thread t1 = new Thread(new Runnable() {
                    public void run() {
                        emails = connectionThread.getEmails();
                        emailWindow.setText(connectionThread.mailBody(index));
                    }
                });
                t1.start();
                messageNum.setText(connectionThread.getNumberOfMsg());
            }
        });

        Rectangle listBorder = new Rectangle();
        listBorder.setFill(Color.LIGHTGRAY);
        listBorder.setX(35);
        listBorder.setY(70);
        listBorder.setWidth(580);
        listBorder.setHeight(615);
        listBorder.setArcWidth(10);
        listBorder.setArcHeight(10);

        Rectangle emailBorder = new Rectangle();
        emailBorder.setFill(Color.LIGHTGRAY);
        emailBorder.setX(705);
        emailBorder.setY(70);
        emailBorder.setWidth(550);
        emailBorder.setHeight(615);
        emailBorder.setArcWidth(10);
        emailBorder.setArcHeight(10);

        Label newLabel = new Label("    Compose    ");
        Label replyLabel = new Label("    Reply    ");
        Label exitLabel = new Label("    Exit    ");
        Label logOutLabel = new Label("   LogOut   ");

        newLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                emailPage(username, password, primaryStage,  "", "");
                stage.close();
            }
        });

        replyLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                String to = emails.get(index).getAddressString();
                String subject = emails.get(index).getSubject();
                String message = "\n\n\n\n\n----------------------------------------\nRE: " +
                        emails.get(index).getDate() + ", " + emails.get(index).getAddressFrom()[0] + "\nSubject: " + subject +
                        "\nMessage: " + emails.get(index).getMessageBody();

                emailPage(username, password, primaryStage, message, to);
            }
        });

        exitLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                System.exit(0);
            }
        });

        logOutLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                OpenHomepage(primaryStage);
                stage.close();

            }
        });

        Menu newMenu = new Menu();
        newMenu.setGraphic(newLabel);
        Menu replyMenu = new Menu();
        replyMenu.setGraphic(replyLabel);
        Menu exitMenu = new Menu();
        exitMenu.setGraphic(exitLabel);
        Menu logOutMenu = new Menu();
        logOutMenu.setGraphic(logOutLabel);

        layout.getChildren().add(messageNum);
        layout.getChildren().add(messagesLabel);
        layout.getChildren().add(bannerImage);
        layout.getChildren().add(listBorder);
        layout.getChildren().add(list);
        layout.getChildren().add(emailBorder);
        layout.getChildren().add(emailWindow);
        MenuBar menuBar = new MenuBar();
        menuBar.setTranslateX(30);
        MenuBar exitBar = new MenuBar();
        exitBar.setTranslateX(1063);

        menuBar.getMenus().add(newMenu);
        menuBar.getMenus().add(replyMenu);

        exitBar.getMenus().add(exitMenu);
        exitBar.getMenus().add(logOutMenu);

        layout.getChildren().add(menuBar);
        layout.getChildren().add(exitBar);
    }

    public void emailPage(String username, String password, Stage primaryStage, String message, String to){
        emailStage.setTitle("ColdMail - Compose Email");
        emailStage.setScene(scene3);
        emailStage.setResizable(false);
        emailStage.show();

        Label fontLabel = new Label("Font");
        Label fontSizeLabel = new Label("Font Size");
        Label boldLabel = new Label("B");
        boldLabel.setStyle(boldLabel.getStyle() + "-fx-font-weight: bold;");
        Label italicsLabel = new Label("I");
        italicsLabel.setStyle(italicsLabel.getStyle() + "-fx-font-style: italic;");
        Label normalLabel = new Label("N");
        Label alignLeftLabel = new Label("Align Left");
        Label alignRightLabel= new Label("Align Right");
        Label backLabel = new Label("   Back   ");

        Image bottomBanner = new Image("coldMail3.png");
        ImageView bannerImage = new ImageView();
        bannerImage.setY(345);
        bannerImage.setImage(bottomBanner);

        Rectangle emailBorder = new Rectangle();
        emailBorder.setFill(Color.LIGHTGRAY);
        emailBorder.setX(30);
        emailBorder.setY(40);
        emailBorder.setWidth(1230);
        emailBorder.setHeight(620);
        emailBorder.setArcWidth(10);
        emailBorder.setArcHeight(10);

        GridPane composeSection = new GridPane();
        composeSection.setGridLinesVisible(true);
        composeSection.setPadding(new Insets(100, 0, 0, 10));
        composeSection.setVgap(10);
        composeSection.setHgap(10);

        Label toLabel = new Label("To:");
        toLabel.setTranslateX(80);
        toLabel.setTranslateY(72);

        TextField toField = new TextField();
        toField.setText(to);
        toField.setPrefWidth(1050);
        toField.setTranslateX(150);
        toField.setTranslateY(70);

        Label subjectLabel = new Label("Subject:");
        subjectLabel.setTranslateX(80);
        subjectLabel.setTranslateY(112);

        TextField subjectField = new TextField();
        subjectField.setPrefWidth(1050);
        subjectField.setTranslateX(150);
        subjectField.setTranslateY(110);

        Label messageLabel = new Label("Message:");
        messageLabel.setTranslateX(80);
        messageLabel.setTranslateY(152);

        TextArea messageField = new TextArea();
        messageField.setText(message);
        messageField.setPrefWidth(1050);
        messageField.setPrefHeight(400);
        messageField.setWrapText(true);
        messageField.setTranslateX(150);
        messageField.setTranslateY(150);
        //composeSection.add(messageField, 2, 3);

        Menu fontMenu = new Menu();
        fontMenu.setGraphic(fontLabel);

        Menu fontSizeMenu = new Menu();
        fontSizeMenu.setGraphic(fontSizeLabel);

        MenuItem twelve = new MenuItem("12");
        twelve.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                messageField.setStyle("-fx-font-size: 12px ;");
            }
        });

        MenuItem thirteen = new MenuItem("13");
        thirteen.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                messageField.setStyle("-fx-font-size: 13px ;");
            }
        });

        MenuItem fourteen = new MenuItem("14");
        fourteen.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                messageField.setStyle("-fx-font-size: 14px;");
            }
        });

        fontSizeMenu.getItems().add(twelve);
        fontSizeMenu.getItems().add(thirteen);
        fontSizeMenu.getItems().add(fourteen);

        Menu boldMenu = new Menu();
        boldMenu.setGraphic(boldLabel);

        Menu italicsMenu = new Menu();
        italicsMenu.setGraphic(italicsLabel);

        Menu normalMenu = new Menu();
        normalMenu.setGraphic(normalLabel);

        Menu alignLeftMenu = new Menu();
        alignLeftMenu.setGraphic(alignLeftLabel);

        Menu alignRightMenu = new Menu();
        alignRightMenu.setGraphic(alignRightLabel);
        Menu backMenu = new Menu();
        backMenu.setGraphic(backLabel);

        boldLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                messageField.setStyle(messageField.getStyle() + "-fx-font-weight: bold;");
            }
        });

        italicsLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                messageField.setStyle(messageField.getStyle() + "-fx-font-style: italic;");
            }
        });

        normalLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                messageField.setStyle(messageField.getStyle() + "-fx-font-weight: normal;");
                messageField.setStyle(messageField.getStyle() + "-fx-font-style: normal;");
            }
        });

        alignLeftLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                messageField.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            }
        });

        alignRightLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                messageField.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            }
        });

        backLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                emailStage.close();
                email(username, password, primaryStage);
            }
        });

        Button send = new Button("Send");
        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent ee) {

                Properties props = System.getProperties();
                props.put("mail.smtp.starttls.enable", "true"); //transport layer security protocol
                props.put("mail.smtp.auth", "true"); //authentication of user
                props.put("mail.smtp.host", "smtp.gmail.com"); //smtp server to connect to
                props.put("mail.smtp.port", "587"); //smtp server port to connect to

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(toField.getText()));
                    message.setSubject(subjectField.getText());
                    message.setText(messageField.getText());

                    Transport.send(message);

                    System.out.println("Message Sent");
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }

                toField.clear();
                subjectField.clear();
                messageField.clear();
            }

        });
        send.setTranslateX(500);
        send.setTranslateY(590);
        send.setPrefWidth(150);
        send.setPrefHeight(50);

        Button discard = new Button("Discard");
        discard.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent ee) {
                toField.clear();
                subjectField.clear();
                messageField.clear();
            }

        });

        discard.setTranslateX(700);
        discard.setTranslateY(590);
        discard.setPrefWidth(150);
        discard.setPrefHeight(50);

        MenuBar OptionsMenu = new MenuBar();
        OptionsMenu.getMenus().add(fontSizeMenu);
        OptionsMenu.getMenus().add(boldMenu);
        OptionsMenu.getMenus().add(italicsMenu);
        OptionsMenu.getMenus().add(normalMenu);
        OptionsMenu.getMenus().add(alignLeftMenu);
        OptionsMenu.getMenus().add(alignRightMenu);

        MenuBar backMenuBar = new MenuBar();
        backMenuBar.setTranslateX(1187);
        backMenuBar.getMenus().add(backMenu);

        emailLayout.getChildren().add(OptionsMenu);
        emailLayout.getChildren().add(backMenuBar);
        emailLayout.getChildren().add(emailBorder);
        emailLayout.getChildren().add(messageLabel);
        emailLayout.getChildren().add(toLabel);
        emailLayout.getChildren().add(subjectLabel);
        emailLayout.getChildren().add(toField);
        emailLayout.getChildren().add(subjectField);
        emailLayout.getChildren().add(messageField);
        emailLayout.getChildren().add(bannerImage);
        emailLayout.getChildren().add(send);
        emailLayout.getChildren().add(discard);
    }

}


