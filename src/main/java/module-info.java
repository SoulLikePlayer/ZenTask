module info.prog.zentask {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens info.prog.zentask to javafx.fxml;
    exports info.prog.zentask;
}