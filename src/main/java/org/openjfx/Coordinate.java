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

    @Override
    public final boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }

        final Coordinate coordinate = (Coordinate) object;

        return this.q == coordinate.q && this.r == coordinate.r && this.s == coordinate.s;
    }

    @Override
    public final int hashCode() {
        return 31 * (113 * (227 * this.q + this.r) + this.s);
    }

    final Coordinate[] getNeighborCoordinates() {
        final Coordinate[] neighborCoordinates = new Coordinate[Controller.SIDE_COUNT];

        neighborCoordinates[0] = new Coordinate(q - 1, r + 1, s);
        neighborCoordinates[1] = new Coordinate(q, r + 1, s - 1);
        neighborCoordinates[2] = new Coordinate(q + 1, r, s - 1);
        neighborCoordinates[3] = new Coordinate(q + 1, r - 1, s);
        neighborCoordinates[4] = new Coordinate(q, r - 1, s + 1);
        neighborCoordinates[5] = new Coordinate(q - 1, r, s + 1);

        return neighborCoordinates;
    }
}
