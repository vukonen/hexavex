package org.openjfx;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public final class Controller implements Initializable {
    static final int SIDE_COUNT = 6;

    private static final Random RANDOM = new Random();
    private static final int GRID_RADIUS = 2;
    private static final int DIGIT_COUNT = 10;

    private final Map<Coordinate, Cell> cellsByCoordinates = new HashMap<>();
    private final Map<Tile, Cell> cellsByTiles = new HashMap<>();
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
                return Color.WHITE;
            }
        }
    }

	@Override
	public final void initialize(final URL url, ResourceBundle resourceBundle) {
        this.createCells();
        this.createTiles();
        this.createDigits();
	}

    private final void createCells() {
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

                this.cellsByCoordinates.put(coordinate, new Cell(coordinate, polygon));
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

    private final void createTiles() {
        for (final Cell cell : this.cellsByCoordinates.values()) {
            final Group group = new Group();
            final Polygon[] polygons = new Polygon[Controller.SIDE_COUNT];

            group.setOnMousePressed(mouseEvent -> {
                this.mouseAnchorX = mouseEvent.getX();
                this.mouseAnchorY = mouseEvent.getY();
            });

            group.setOnMouseDragged(mouseEvent -> {
                group.setLayoutX(mouseEvent.getSceneX() - this.mouseAnchorX);
                group.setLayoutY(mouseEvent.getSceneY() - this.mouseAnchorY);
            });

            final double cx = 0.0;
            final double cy = 0.0;

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

                final Polygon polygon = new Polygon(cx, cy, px, py, qx, qy);

                polygon.setStroke(Color.BLACK);
                polygon.setStrokeWidth(Tile.STROKE_WIDTH);

                group.getChildren().add(polygon);
                polygons[j] = polygon;
            }

            final double lx = this.shelf.getLayoutX() + Tile.RADIUS + Controller.RANDOM.nextDouble()
                * (this.shelf.getWidth() - 2.0 * Tile.RADIUS);
            final double ly = this.shelf.getLayoutY() + Tile.RADIUS + Controller.RANDOM.nextDouble()
                * (this.shelf.getHeight() - 2.0 * Tile.RADIUS);
            
            group.setLayoutX(lx);
            group.setLayoutY(ly);

            this.game.getChildren().add(group);
            this.cellsByTiles.put(new Tile(group, polygons), cell);
        }
    }

    private final void createDigits() {
        for (final Map.Entry<Tile, Cell> entry : this.cellsByTiles.entrySet()) {
            final Tile tile = entry.getKey();
            final Cell cell = entry.getValue();

            tile.setCell(cell);
            cell.setTile(tile);
        }

        for (final Cell cell : this.cellsByCoordinates.values()) {
            final Cell[] neighbors = cell.getNeighbors();
            final Tile t0 = cell.getTile();

            for (int j = 0; j < Controller.SIDE_COUNT; ++j) {
                int digit;

                if (neighbors[j] == null) {
                    digit = Controller.RANDOM.nextInt(Controller.DIGIT_COUNT);
                } else {
                    final Tile t1 = neighbors[j].getTile();
                    final int k = (j + (Controller.SIDE_COUNT >> 1)) % Controller.SIDE_COUNT;

                    digit = t1.getDigits()[k];

                    if (digit == Tile.SENTINEL_DIGIT) {
                        digit = Controller.RANDOM.nextInt(Controller.DIGIT_COUNT);
                    }
                }

                t0.getDigits()[j] = digit;

                final Label label = new Label(Integer.toString(digit));
                final double angle = 2.0 * Math.PI * (j - 0.5) / Controller.SIDE_COUNT;

                label.setLayoutX(Tile.RADIUS * 7.0 / 10.0 * Math.sin(angle));
                label.setLayoutY(Tile.RADIUS * 7.0 / 10.0 * Math.cos(angle));
                label.translateXProperty().bind((label.widthProperty().divide(2.0).negate()));
                label.translateYProperty().bind((label.heightProperty().divide(2.0).negate()));

                final Font font = Font.font("Dejavu Sans", FontWeight.BOLD, Tile.RADIUS / 5.0);

                label.setFont(font);

                t0.getGroup().getChildren().add(label);
            }
        }

        for (final Cell cell : this.cellsByTiles.values()) {
            final Tile tile = cell.getTile();

            tile.setCell(null);
            cell.setTile(null);
        }

        for (final Tile tile : this.cellsByTiles.keySet()) {
            final Polygon[] polygons = tile.getPolygons();
            final int[] digits = tile.getDigits();

            for (int j = 0; j < Controller.SIDE_COUNT; ++j) {
                polygons[j].setFill(Controller.getTriangleColor(digits[j]));
            }
        }
    }

    private final void solve() {
        for (final Map.Entry<Tile, Cell> entry : this.cellsByTiles.entrySet()) {
            final Tile tile = entry.getKey();
            final Cell cell = entry.getValue();

            tile.setCell(cell);
            cell.setTile(tile);

            final Group group = tile.getGroup();

            group.setLayoutX(this.grid.getLayoutX() + cell.getPolygon().getLayoutX());
            group.setLayoutY(this.grid.getLayoutY() + cell.getPolygon().getLayoutY());
        }
    }
}
