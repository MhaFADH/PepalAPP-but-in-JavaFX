package com.pepal.pepalfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

import java.io.IOException;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class LoginController{
    Tools tools = new Tools();
    public static String username = "";
    public static String password = "";
    public static HashMap<String,String> cookie;

    @FXML
    private Label sdvLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    public LoginController(){
    }

    public void login(ActionEvent e) throws IOException, InterruptedException {

        loginButton.setDisable(true);
        username = usernameField.getText();
        password = passwordField.getText();
        cookie = tools.connectPepal(usernameField.getText(),passwordField.getText());




        if(cookie == null){

            errorLabel.setVisible(true);
            loginButton.setDisable(false);

        }else {
            loginButton.setDisable(true);
            Stage stgActuel = (Stage) loginButton.getScene().getWindow();
            stgActuel.close();
            Main mainn = new Main();
            mainn.mainScreen(new Stage());

        }



    }



}
