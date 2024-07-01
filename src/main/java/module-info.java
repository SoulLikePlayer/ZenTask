module info.prog.zentask {
    requires javafx.controls;
    requires javafx.fxml;


    opens info.prog.zentask to javafx.fxml;
    exports info.prog.zentask;
}