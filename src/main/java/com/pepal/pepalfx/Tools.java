package com.pepal.pepalfx;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.*;

import static com.pepal.pepalfx.LoginController.*;


public class Tools {

    // se connecter à pepal en cas d'erreur retourner null
    public HashMap<String,String> connectPepal(String username,String password) {
        try {
            Connection.Response response = Jsoup.connect("https://www.pepal.eu/include/php/ident.php")
                    .method(Connection.Method.GET)
                    .execute();

            Document loginDoc = response.parse();
            HashMap<String,String> cookie = new HashMap<>(response.cookies());


            HashMap<String,String> formData = new HashMap<>();


            formData.put("login", username);
            formData.put("pass", password);

            Connection.Response homePage = Jsoup.connect("https://www.pepal.eu/include/php/ident.php")
                    .cookies(cookie)
                    .data(formData)
                    .method(Connection.Method.POST)
                    .execute();
            Elements doc = homePage.parse().getElementsByTag("p");


            if (doc.text().contains("Redirection dans : ")) {
                return cookie;
            } else {
                return null;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //récuperer la page
    public Document getHtml(String url) throws IOException {
        Connection.Response page = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .cookies(cookie)
                .execute();

        return page.parse();

    }


    public void orgaNotes(TextArea noteField, TextField moyField, float sumNotes, float moyenne) throws IOException {
        noteField.clear();
        Document noteHtml = null;
        sumNotes = 0;
        moyenne = 0;

        List<String> tabloNotes = new ArrayList<>();

        noteHtml = getHtml("https://www.pepal.eu/?my=notes");

/*        Observable.just(getHtml("https://www.pepal.eu/?my=notes"))
                .map(doc -> doc.getElementsByClass("note_devoir"))
                .flatMapIterable(list -> list.stream().collect(Collectors.toList()))
                .subscribe(onNext -> {
                    float note = Float.parseFloat(onNext.child(3).text());
                    noteField.appendText(onNext.child(0).text().replace(" PUBLIE","") +": "+note);
                });*/

        Elements trClass = noteHtml.getElementsByClass("note_devoir");

        HashMap<String,String> notes = new HashMap<String, String>();


        for(Element ele:trClass){
            String note = ele.child(3).text();
            tabloNotes.add(ele.child(0).text().replace(" PUBLIE","")+":  "+ note);
            sumNotes += Double.parseDouble(note);
        }

        moyenne = sumNotes / tabloNotes.size();


        for(String ele:tabloNotes){
            noteField.appendText(ele+"\n");

        }

        moyField.setText("Moyenne generale: "+ moyenne);


    }








}
