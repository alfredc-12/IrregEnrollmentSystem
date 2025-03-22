module org.enrolment.enrollmentsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires webcam.capture;
    requires ortools.java;

    exports Application;
    opens Application to javafx.fxml;
    exports Classes;
    opens Classes to javafx.fxml;
}
