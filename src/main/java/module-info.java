module com.komar_olga.cloud_gb_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.codec;


    opens com.komar_olga.cloud_gb to javafx.fxml;
    exports com.komar_olga.cloud_gb;
}