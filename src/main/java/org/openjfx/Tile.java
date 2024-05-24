package org.openjfx;

import javafx.scene.Group;

final class Tile {
    static final double RADIUS = 75.0;
    static final double STROKE_WIDTH = 2.0;

    private final Group group;
    private final int[] digits;
    private Cell cell;

    Tile(final Group group, final int[] digits) {
        this.group = group;
        this.digits = digits;
        this.cell = null;
    }
}
