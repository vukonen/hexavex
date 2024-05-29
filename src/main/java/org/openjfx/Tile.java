package org.openjfx;

import java.util.Arrays;

import javafx.scene.Group;
import javafx.scene.shape.Polygon;

final class Tile {
    static final double RADIUS = 75.0;
    static final double STROKE_WIDTH = 2.0;
    static final int SENTINEL_DIGIT = -1;

    private final Group group;
    private final Polygon[] polygons;
    private final int[] digits;
    private Cell cell;

    Tile(final Group group, final Polygon[] polygons) {
        this.group = group;
        this.polygons = polygons;
        this.digits = Tile.getStartingDigits();
        this.cell = null;
    }

    private static final int[] getStartingDigits() {
        final int[] digits = new int[Controller.SIDE_COUNT];

        Arrays.fill(digits, Tile.SENTINEL_DIGIT);

        return digits;
    }

    final Group getGroup() {
        return this.group;
    }

    final Polygon[] getPolygons() {
        return this.polygons;
    }

    final int[] getDigits() {
        return this.digits;
    }

    final Cell getCell() {
        return this.cell;
    }

    final void setCell(final Cell cell) {
        this.cell = cell;
    }
}
