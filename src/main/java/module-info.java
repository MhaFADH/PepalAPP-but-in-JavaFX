module com.pepal.pepalfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires java.desktop;
    requires io.reactivex.rxjava3;


    opens com.pepal.pepalfx to javafx.fxml;
    exports com.pepal.pepalfx;
}