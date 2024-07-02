module ZenTask {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens info.prog.zentask.controller to javafx.fxml;
    opens info.prog.zentask.model to javafx.base;
    opens info.prog.zentask to javafx.graphics, javafx.fxml;

    exports info.prog.zentask;
}
