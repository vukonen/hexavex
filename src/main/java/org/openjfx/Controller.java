package org.openjfx;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public final class Controller implements Initializable {
    static final int SIDE_COUNT = 6;

    private static final Random RANDOM = new Random();
    private static final int GRID_RADIUS = 2;
    private static final int TILE_COUNT = 19;
    private static final int DIGIT_COUNT = 10;

    private final Map<Coordinate, Cell> cellsByCoordinates = new HashMap<>();
    private double mouseAnchorX = 0.0;
    private double mouseAnchorY = 0.0;

    @FXML
    private AnchorPane game;

    @FXML
    private AnchorPane grid;

    @FXML
    private Rectangle shelf;

    @FXML
    private Polygon template;

	@Override
	public final void initialize(final URL url, ResourceBundle resourceBundle) {
        this.createGrid();

        for (int i = 0; i < Controller.TILE_COUNT; ++i) {
            final Group group = new Group();

            group.setOnMousePressed(mouseEvent -> {
                this.mouseAnchorX = mouseEvent.getX();
                this.mouseAnchorY = mouseEvent.getY();
            });

            group.setOnMouseDragged(mouseEvent -> {
                group.setLayoutX(mouseEvent.getSceneX() - this.mouseAnchorX);
                group.setLayoutY(mouseEvent.getSceneY() - this.mouseAnchorY);
            });

            final double cx = this.shelf.getLayoutX() + Tile.RADIUS + Controller.RANDOM.nextDouble()
                * (this.shelf.getWidth() - 2.0 * Tile.RADIUS);
            final double cy = this.shelf.getLayoutY() + Tile.RADIUS + Controller.RANDOM.nextDouble()
                * (this.shelf.getHeight() - 2.0 * Tile.RADIUS);

            double angle = 2.0 * Math.PI * (Controller.SIDE_COUNT - 1) / Controller.SIDE_COUNT;
            double px = cx;
            double py = cy;
            double qx = cx + Tile.RADIUS * Math.sin(angle);
            double qy = cy + Tile.RADIUS * Math.cos(angle);

            for (int j = 0; j < Controller.SIDE_COUNT; ++j) {
                angle = 2.0 * Math.PI * j / Controller.SIDE_COUNT;
                px = qx;
                py = qy;
                qx = cx + Tile.RADIUS * Math.sin(angle);
                qy = cy + Tile.RADIUS * Math.cos(angle);

                final Polygon triangle = new Polygon(cx, cy, px, py, qx, qy);
                final int digit = Controller.RANDOM.nextInt(Controller.DIGIT_COUNT);

                triangle.setFill(Controller.getTriangleColor(digit));
                triangle.setStroke(Color.BLACK);
                triangle.setStrokeWidth(Tile.STROKE_WIDTH);

                group.getChildren().add(triangle);
            }

            this.game.getChildren().add(group);
        }
	}

    private final void createGrid() {
        final double cx = this.template.getLayoutX();
        final double cy = this.template.getLayoutY();

        for (int q = -Controller.GRID_RADIUS; q <= Controller.GRID_RADIUS; ++q) {
            final int a = Math.max(-Controller.GRID_RADIUS, -q - Controller.GRID_RADIUS);
            final int b = Math.min(Controller.GRID_RADIUS, -q + Controller.GRID_RADIUS);

            for (int r = a; r <= b; ++r) {
                final Coordinate coordinate = new Coordinate(q, r, -q - r);
                final Polygon polygon = new Polygon();

                this.grid.getChildren().add(polygon);

                polygon.getPoints().addAll(this.template.getPoints());
                polygon.setLayoutX(cx + Cell.RADIUS * (Math.sqrt(3.0) * q + Math.sqrt(3.0) / 2.0 * r));
                polygon.setLayoutY(cy + Cell.RADIUS * 3.0 / 2.0 * r);
                polygon.setFill(Color.WHITE);
                polygon.setStroke(Color.BLACK);
                polygon.setStrokeWidth(Cell.STROKE_WIDTH);

                final Cell cell = new Cell(coordinate, polygon);

                this.cellsByCoordinates.put(coordinate, cell);
            }
        }

        for (final Map.Entry<Coordinate, Cell> entry : this.cellsByCoordinates.entrySet()) {
            final Coordinate[] neighborCoordinates = entry.getKey().getNeighborCoordinates();
            final Cell[] neighbors = new Cell[Controller.SIDE_COUNT];

            for (int i = 0; i < Controller.SIDE_COUNT; ++i) {
                neighbors[i] = this.cellsByCoordinates.get(neighborCoordinates[i]);
            }

            entry.getValue().setNeighbors(neighbors);
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
