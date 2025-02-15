module org.example.lab1new {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens org.example.lab1new to javafx.fxml;
    exports org.example.lab1new;
}