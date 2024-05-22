package org.openjfx;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class Controller implements Initializable {
    private static final Random RANDOM = new Random();
    private static final int HEX_COUNT = 19;
    private static final int SIDE_COUNT = 6;
    private static final int DIGIT_COUNT = 10;
    private static final double HEX_RADIUS = 75.0;
    private static final double HEX_STROKE_WIDTH = 2.0;

    private double mouseAnchorX = 0.0;
    private double mouseAnchorY = 0.0;

    @FXML
    private AnchorPane game;

    @FXML
    private Rectangle shelf;

	@Override
	public final void initialize(final URL url, ResourceBundle resourceBundle) {
        for (int i = 0; i < Controller.HEX_COUNT; ++i) {
            final Group group = new Group();

            group.setOnMousePressed(mouseEvent -> {
                this.mouseAnchorX = mouseEvent.getX();
                this.mouseAnchorY = mouseEvent.getY();
            });

            group.setOnMouseDragged(mouseEvent -> {
                group.setLayoutX(mouseEvent.getSceneX() - this.mouseAnchorX);
                group.setLayoutY(mouseEvent.getSceneY() - this.mouseAnchorY);
            });

            final double cx = this.shelf.getLayoutX() + Controller.HEX_RADIUS + Controller.RANDOM.nextDouble()
                * (this.shelf.getWidth() - 2.0 * Controller.HEX_RADIUS);
            final double cy = this.shelf.getLayoutY() + Controller.HEX_RADIUS + Controller.RANDOM.nextDouble()
                * (this.shelf.getHeight() - 2.0 * Controller.HEX_RADIUS);

            double angle = 2.0 * Math.PI * (Controller.SIDE_COUNT - 1) / Controller.SIDE_COUNT;
            double px = cx;
            double py = cy;
            double qx = cx + Controller.HEX_RADIUS * Math.sin(angle);
            double qy = cy + Controller.HEX_RADIUS * Math.cos(angle);

            for (int j = 0; j < Controller.SIDE_COUNT; ++j) {
                angle = 2.0 * Math.PI * j / Controller.SIDE_COUNT;
                px = qx;
                py = qy;
                qx = cx + Controller.HEX_RADIUS * Math.sin(angle);
                qy = cy + Controller.HEX_RADIUS * Math.cos(angle);

                final Polygon triangle = new Polygon(cx, cy, px, py, qx, qy);
                final int digit = Controller.RANDOM.nextInt(Controller.DIGIT_COUNT);

                triangle.setFill(Controller.getTriangleColor(digit));
                triangle.setStroke(Color.BLACK);
                triangle.setStrokeWidth(Controller.HEX_STROKE_WIDTH);

                group.getChildren().add(triangle);
            }

            this.game.getChildren().add(group);
        }
	}

    private static final Color getTriangleColor(final int digit) {
        switch (digit) {
            case 0: {
                return Color.CORNSILK;
            }

            case 1: {
                return Color.BLANCHEDALMOND;
            }

            case 2: {
                return Color.DARKSALMON;
            }

            case 3: {
                return Color.SANDYBROWN;
            }

            case 4: {
                return Color.LIGHTCYAN;
            }

            case 5: {
                return Color.LIGHTSTEELBLUE;
            }

            case 6: {
                return Color.STEELBLUE;
            }

            case 7: {
                return Color.KHAKI;
            }

            case 8: {
                return Color.LAVENDER;
            }

            case 9: {
                return Color.PLUM;
            }

            default: {
                return Color.BLACK;
            }
        }
    }
}
