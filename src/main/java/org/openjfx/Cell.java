package org.openjfx;

import javafx.scene.shape.Polygon;

final class Cell {
    static final double RADIUS = 80.0;
    static final double STROKE_WIDTH = 2.0;

    private final Coordinate coordinate;
    private final Polygon polygon;
    private Cell[] neighbors;
    private Tile tile;

    Cell(final Coordinate coordinate, final Polygon polygon) {
        this.coordinate = coordinate;
        this.polygon = polygon;
        this.neighbors = null;
        this.tile = null;
    }

	final void setNeighbors(final Cell[] neighbors) {
		this.neighbors = neighbors;
	}
}
