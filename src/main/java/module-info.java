module com.pepal.pepalfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;


    opens com.pepal.pepalfx to javafx.fxml;
    exports com.pepal.pepalfx;
}