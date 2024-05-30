package org.openjfx;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

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
    private AnchorPane mainAnchorPane;

    @FXML
    private AnchorPane cellAnchorPane;

    @FXML
    private Rectangle tileRectangle;

    @FXML
    private Polygon templatePolygon;

    @FXML
    private Button newButton;

    @FXML
    private Button solveButton;

    @FXML
    private Text congratulationsText;

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

        this.newButton.setOnMousePressed(mouseEvent -> {
            for (final Map.Entry<Tile, Cell> entry : this.cellsByTiles.entrySet()) {
                final Tile tile = entry.getKey();
                final Cell cell = entry.getValue();

                this.mainAnchorPane.getChildren().remove(tile.getGroup());
                cell.setTile(null);
            }

            this.cellsByTiles.clear();

            this.check();
            this.createTiles();
            this.createDigits();
        });

        this.solveButton.setOnMousePressed(mouseEvent -> {
            this.solve();
        });
	}

    private final void createCells() {
        final double cx = this.templatePolygon.getLayoutX();
        final double cy = this.templatePolygon.getLayoutY();

        for (int q = -Controller.GRID_RADIUS; q <= Controller.GRID_RADIUS; ++q) {
            final int a = Math.max(-Controller.GRID_RADIUS, -q - Controller.GRID_RADIUS);
            final int b = Math.min(Controller.GRID_RADIUS, -q + Controller.GRID_RADIUS);

            for (int r = a; r <= b; ++r) {
                final Coordinate coordinate = new Coordinate(q, r, -q - r);
                final Polygon polygon = new Polygon();

                this.cellAnchorPane.getChildren().add(polygon);

                polygon.getPoints().addAll(this.templatePolygon.getPoints());
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

            this.mainAnchorPane.getChildren().add(group);

            final double lx = this.tileRectangle.getLayoutX() + Tile.RADIUS + Controller.RANDOM.nextDouble()
                * (this.tileRectangle.getWidth() - 2.0 * Tile.RADIUS);
            final double ly = this.tileRectangle.getLayoutY() + Tile.RADIUS + Controller.RANDOM.nextDouble()
                * (this.tileRectangle.getHeight() - 2.0 * Tile.RADIUS);

            group.setLayoutX(lx);
            group.setLayoutY(ly);

            final Tile tile = new Tile(group, polygons);

            this.setTileMouseEventHandlers(tile, group);
            this.cellsByTiles.put(tile, cell);
        }
    }

    private final void createDigits() {
        for (final Map.Entry<Tile, Cell> entry : this.cellsByTiles.entrySet()) {
            final Tile tile = entry.getKey();
            final Cell cell = entry.getValue();

            cell.setTile(tile);
            tile.setCell(cell);
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

            cell.setTile(null);
            tile.setCell(null);
        }

        for (final Tile tile : this.cellsByTiles.keySet()) {
            final Polygon[] polygons = tile.getPolygons();
            final int[] digits = tile.getDigits();

            for (int j = 0; j < Controller.SIDE_COUNT; ++j) {
                polygons[j].setFill(Controller.getTriangleColor(digits[j]));
            }
        }
    }

    private final void setTileMouseEventHandlers(final Tile tile, final Group group) {
        group.setOnMousePressed(mouseEvent -> {
            this.mouseAnchorX = mouseEvent.getX();
            this.mouseAnchorY = mouseEvent.getY();

            final Cell cell = tile.getCell();

            if (cell != null) {
                cell.setTile(null);
                tile.setCell(null);
            }

            this.check();
        });

        group.setOnMouseDragged(mouseEvent -> {
            group.setLayoutX(mouseEvent.getSceneX() - this.mouseAnchorX);
            group.setLayoutY(mouseEvent.getSceneY() - this.mouseAnchorY);
        });

        group.setOnMouseReleased(mouseEvent -> {
            final Point2D p0 = group.localToScene(0.0, 0.0);

            for (final Cell cell : this.cellsByCoordinates.values()) {
                final Point2D p1 = cell.getPolygon().localToScene(0.0, 0.0);
                final double a = Math.pow(p0.getX() - p1.getX(), 2.0);
                final double b = Math.pow(p0.getY() - p1.getY(), 2.0);

                if (Math.sqrt(a + b) < Tile.RADIUS / 2.0) {
                    cell.setTile(tile);
                    tile.setCell(cell);

                    group.setLayoutX(p1.getX());
                    group.setLayoutY(p1.getY());

                    break;
                }
            }

            this.check();
        });
    }

    private final void check() {
        int count = 0;

        for (final Cell cell : this.cellsByCoordinates.values()) {
            final Cell[] neighbors = cell.getNeighbors();
            final Polygon polygon = cell.getPolygon();
            final Tile t0 = cell.getTile();

            if (t0 == null) {
                polygon.setFill(Color.WHITE);

                continue;
            }

            int j = 0;

            for (; j < Controller.SIDE_COUNT; ++j) {
                if (neighbors[j] == null) {
                    continue;
                }

                final Tile t1 = neighbors[j].getTile();

                if (t1 == null) {
                    continue;
                }

                final int k = (j + (Controller.SIDE_COUNT >> 1)) % Controller.SIDE_COUNT;

                if (t0.getDigits()[j] != t1.getDigits()[k]) {
                    polygon.setFill(Color.RED);

                    break;
                }
            }

            if (j == Controller.SIDE_COUNT) {
                polygon.setFill(Color.GREEN);
                ++count;
            }
        }

        if (count < this.cellsByCoordinates.size()) {
            this.congratulationsText.setVisible(false);
        } else {
            this.congratulationsText.setVisible(true);
        }
    }

    private final void solve() {
        for (final Map.Entry<Tile, Cell> entry : this.cellsByTiles.entrySet()) {
            final Tile tile = entry.getKey();
            final Cell cell = entry.getValue();

            cell.setTile(tile);
            tile.setCell(cell);

            final Group group = tile.getGroup();

            group.setLayoutX(this.cellAnchorPane.getLayoutX() + cell.getPolygon().getLayoutX());
            group.setLayoutY(this.cellAnchorPane.getLayoutY() + cell.getPolygon().getLayoutY());
        }

        this.check();
    }
}
