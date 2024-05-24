package org.openjfx;

final class Coordinate {
    private final int q;
    private final int r;
    private final int s;

    Coordinate(final int q, final int r, final int s) {
        this.q = q;
        this.r = r;
        this.s = s;
    }

    final Coordinate[] getNeighborCoordinates() {
        final Coordinate[] neighborCoordinates = new Coordinate[Controller.SIDE_COUNT];

        neighborCoordinates[0] = new Coordinate(q, r - 1, s + 1);
        neighborCoordinates[1] = new Coordinate(q + 1, r - 1, s);
        neighborCoordinates[2] = new Coordinate(q + 1, r, s - 1);
        neighborCoordinates[3] = new Coordinate(q, r + 1, s - 1);
        neighborCoordinates[4] = new Coordinate(q - 1, r + 1, s);
        neighborCoordinates[5] = new Coordinate(q - 1, r, s + 1);

        return neighborCoordinates;
    }
}
