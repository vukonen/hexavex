package org.openjfx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public final class Hexavex extends Application {
    private static final String TITLE = "HEXAVEX";

    public static final void main(final String[] arguments) {
        Application.launch(arguments);
    }

    @Override
    public final void start(final Stage stage) throws IOException {
        final AnchorPane anchorPane = FXMLLoader.load(Hexavex.class.getResource("/hexavex.fxml"));
        final Scene scene = new Scene(anchorPane);

        stage.setTitle(Hexavex.TITLE);
        stage.setScene(scene);
        stage.show();
    }
}
