package com.pepal.pepalfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.pepal.pepalfx.LoginController.*;


public class MainScreenController implements Initializable {
    Tools tools = new Tools();
    public final String msg = "Vous avez déjà été noté présent, ou alors l'appel a été clôturé" ;
    public final String msg1 = "l'appel a été clôturé" ;
    public final String msg2 = "l'appel n'a pas été encore ouvert" ;
    public final String msg3 = "Pas de cours prévu pour aujourd'hui" ;
    public static float sumNotes;
    public static float moyenne;

    @FXML
    private Button buttonAct;
    @FXML
    private Button buttonPres;
    @FXML
    private TextArea noteField;
    @FXML
    private TextField moyField;

    public void act(ActionEvent e) throws IOException {

        cookie = tools.connectPepal(username,password);

        Main mainn = new Main();

        Stage stage = (Stage) buttonAct.getScene().getWindow();
        stage.close();

        mainn.mainScreen(new Stage());
        tools.orgaNotes(noteField,moyField,sumNotes,moyenne);


    }




    public void pres(ActionEvent e) throws IOException {


        Document presPage;

        try {
            presPage = tools.getHtml("https://www.pepal.eu/presences");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Elements success = presPage.getElementsByClass("alert alert-success");

        Elements hrefPres = presPage.getElementsByClass("btn btn-primary");
        Element []hrefLinks = new Element[hrefPres.size()];

        for (int i = 0;i<=hrefPres.size()-1;i++){
            hrefLinks[i] = hrefPres.get(i);
        }

        Elements noCours = presPage.getElementsByClass("alert alert-danger");
        String matin = "";
        String aprem = "";

        if(noCours.text().contains("Pas de cours de prévu")){
        }else{
            matin = hrefLinks[0].attr("href").replace("/presences/s/","");
            aprem = hrefLinks[1].attr("href").replace("/presences/s/","");
        }





        HashMap<String,String> setPresToken = new HashMap<>();
        setPresToken.put("act","set_present");

        HashMap<String,String> matinMap = new HashMap<>();
        matinMap.put("seance_pk",matin);

        HashMap<String,String> apremMap = new HashMap<>();
        apremMap.put("seance_pk",aprem);

        Document matinPres;
        Document apremPres;
        Document pasCours;

        try {
            matinPres = tools.getHtml("https://www.pepal.eu/presences/s/"+matin);
            apremPres = tools.getHtml("https://www.pepal.eu/presences/s/"+aprem);
            pasCours = tools.getHtml("https://www.pepal.eu/presences");

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Elements matinResult = matinPres.getElementsByClass("alert alert-success");
        Elements matinResult1 = matinPres.getElementsByClass("alert alert-danger");

        Elements apremResult = apremPres.getElementsByClass("alert alert-success");
        Elements apremResult1 = apremPres.getElementsByClass("alert alert-danger");

        Elements pacC = pasCours.getElementsByClass("alert alert-danger");

        SimpleDateFormat sdf =new SimpleDateFormat("HH:mm");

        Date limitTime;
        try {
            limitTime = sdf.parse("12:00");
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
        Date currentDate = new Date();
        if(currentDate.getHours() > limitTime.getHours()){
            if(apremResult.text().contains("Vous avez été noté présent le")){
                alert(msg);
            }else if(apremResult1.text().contains("L'appel est clôturé")){
                alert(msg1);
            }else if(apremResult1.text().contains(" L'appel n'est pas encore ouvert")){
                alert(msg2);
            }else if(pacC.text().contains("Pas de cours de prévu")){
                alert(msg3);
            }else{
                try {
                    Connection.Response prespge = Jsoup.connect("https://www.pepal.eu/student/upload.php")
                            .method(Connection.Method.POST)
                            .cookies(cookie)
                            .data(setPresToken)
                            .data(apremMap)
                            .execute();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }




        }else{
            if(matinResult.text().contains("Vous avez été noté présent le")){
                alert(msg);
            }else if(matinResult1.text().contains("L'appel est clôturé")){
                alert(msg1);
            }else if(matinResult1.text().contains(" L'appel n'est pas encore ouvert")){
                alert(msg2);
            }else if(pacC.text().contains("Pas de cours de prévu")){
                alert(msg3);
            }else{
                try {
                    Connection.Response prespge = Jsoup.connect("https://www.pepal.eu/student/upload.php")
                            .method(Connection.Method.POST)
                            .cookies(cookie)
                            .data(setPresToken)
                            .data(matinMap)
                            .execute();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }



    }

    public void alert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Validation failed");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tools.orgaNotes(noteField,moyField,sumNotes,moyenne);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
