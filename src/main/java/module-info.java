module ZenTask {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens info.prog.zentask.controller to javafx.fxml; // Ouvre le package controller pour javafx.fxml

    exports info.prog.zentask; // Vous pouvez également exporter le package principal si nécessaire
}
