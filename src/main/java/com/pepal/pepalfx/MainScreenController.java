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


import static com.pepal.pepalfx.LoginController.username;
import static com.pepal.pepalfx.LoginController.password;
import static com.pepal.pepalfx.LoginController.cookie;



public class MainScreenController implements Initializable {

    @FXML
    private Button buttonAct;
    @FXML
    private Button buttonPres;
    @FXML
    private TextArea noteField;
    @FXML
    private TextField moyField;

    public MainScreenController() throws IOException {
    }


    public void act(ActionEvent e) throws IOException {

        try {
            Connection.Response response = Jsoup.connect("https://www.pepal.eu/include/php/ident.php")
                    .method(Connection.Method.GET)
                    .execute();

            Document loginDoc = response.parse();

            cookie = new HashMap<>(response.cookies());



            HashMap<String, String> formData = new HashMap<>();
            formData.put("login", username);
            formData.put("pass", password);

            Connection.Response homePage = Jsoup.connect("https://www.pepal.eu/include/php/ident.php")
                    .cookies(cookie)
                    .data(formData)
                    .method(Connection.Method.POST)
                    .execute();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        HelloApplication helloApplication = new HelloApplication();

        Stage stage = (Stage) buttonAct.getScene().getWindow();
        stage.close();

        helloApplication.mainScreen(new Stage());
        orgaNotes();




    }

    public Document getHtm(String url) throws IOException {
        Connection.Response page = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .cookies(cookie)
                .execute();

        return page.parse();

    }


    public void pres(ActionEvent e) throws IOException {
        String msg = "Vous avez déjà été noté présent, ou alors l'appel a été clôturé" ;

        Document presPage;
        try {
            presPage = getHtm("https://www.pepal.eu/presences");
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

        String matin = hrefLinks[0].attr("href").replace("/presences/s/","");
        String aprem = hrefLinks[1].attr("href").replace("/presences/s/","");



        HashMap<String,String> setPresToken = new HashMap<>();
        setPresToken.put("act","set_present");

        HashMap<String,String> matinMap = new HashMap<>();
        matinMap.put("seance_pk",matin);

        HashMap<String,String> apremMap = new HashMap<>();
        apremMap.put("seance_pk",aprem);

        Document matinPres;
        Document apremPres;

        try {
            matinPres = getHtm("https://www.pepal.eu/presences/s/"+matin);
            apremPres = getHtm("https://www.pepal.eu/presences/s/"+aprem);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Elements matinResult = matinPres.getElementsByClass("alert alert-success");
        Elements matinResult1 = matinPres.getElementsByClass("alert alert-danger");

        Elements apremResult = apremPres.getElementsByClass("alert alert-success");
        Elements apremResult1 = apremPres.getElementsByClass("alert alert-danger");

        SimpleDateFormat sdf =new SimpleDateFormat("HH:mm");

        Date limitTime;
        try {
            limitTime = sdf.parse("12:00");
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
        Date currentDate = new Date();
        if(currentDate.getHours() > limitTime.getHours()){
            if(apremResult.text().contains("Vous avez été noté présent le")||apremResult1.text().contains("L'appel est clôturé")|| apremResult1.text().contains(" L'appel n'est pas encore ouvert")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Validation failed");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
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
            if(matinResult.text().contains("Vous avez été noté présent le")||matinResult1.text().contains("L'appel est clôturé")|| matinResult1.text().contains(" L'appel n'est pas encore ouvert")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Validation failed");
                alert.setHeaderText(null);
                alert.setContentText(msg);
                alert.showAndWait();
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

    public void orgaNotes(){
        noteField.clear();
        Document noteHtm = null;
        float sumNotes = 0;
        float moyenne = 0;

        try {
            noteHtm = getHtm("https://www.pepal.eu/?my=notes");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Elements trClass = noteHtm.getElementsByClass("note_devoir");

        HashMap<String,String> notes = new HashMap<String, String>();
        List<String> tabloNotes = new ArrayList<>();

        for(Element ele:trClass){
            String titreNote = ele.child(0).text().replace(" PUBLIE","");
            String note = ele.child(3).text();
            tabloNotes.add(titreNote +":  "+ note);
            sumNotes += Double.parseDouble(note);

        }

        moyenne = sumNotes / tabloNotes.size();


        for(String ele:tabloNotes){
            noteField.appendText(ele+"\n");

        }

        moyField.setText("Moyenne generale: "+ moyenne);


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        orgaNotes();
    }
}
