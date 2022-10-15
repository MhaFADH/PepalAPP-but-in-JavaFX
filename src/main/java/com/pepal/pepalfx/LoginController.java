package com.pepal.pepalfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public static String username = "";
    public static String password = "";


    @FXML
    private ImageView imageView;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;


    public void login(ActionEvent e){
        loginButton.setDisable(true);
        username = usernameField.getText();
        password = passwordField.getText();

        try{

            Connection.Response response = Jsoup.connect("https://www.pepal.eu/include/php/ident.php")
                    .method(Connection.Method.GET)
                    .execute();

            Document loginDoc = response.parse();

            HashMap<String,String> cookies = new HashMap<>(response.cookies());



            HashMap<String, String> formData = new HashMap<>();
            formData.put("login", username);
            formData.put("pass", password);

            Connection.Response homePage = Jsoup.connect("https://www.pepal.eu/include/php/ident.php")
                    .cookies(cookies)
                    .data(formData)
                    .method(Connection.Method.POST)
                    .execute();
            Elements doc = homePage.parse().getElementsByTag("p");

            if(doc.text().contains("Redirection dans : ")){
                Stage stgActuel = (Stage) loginButton.getScene().getWindow();
                stgActuel.close();

                HelloApplication helloApplication = new HelloApplication();
                helloApplication.mainScreen(new Stage());





            }else {
                errorLabel.setVisible(true);
                loginButton.setDisable(false);
            }


        }catch(Exception ex){
            ex.printStackTrace();
        }



    }

    private Button button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File sdvBannerF = new File("images/sdvBanner.jpg");
        Image sdvBannerI = new Image(sdvBannerF.toURI().toString());
        imageView.setImage(sdvBannerI);
    }

}
