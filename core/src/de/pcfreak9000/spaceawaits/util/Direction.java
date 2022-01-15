package de.pcfreak9000.spaceawaits.util;

public enum Direction {
    Up(0, 1), Down(0, -1), Left(-1, 0), Right(1, 0), UpLeft(-1, 1), UpRight(1, 1), DownLeft(-1, -1), DownRight(1, -1),
    Zero(0, 0);
    
    public static final Direction[] MOORE_NEIGHBOURS = Direction.values();
    public static final Direction[] VONNEUMANN_NEIGHBOURS = { Up, Down, Left, Right };
    
    public final int dx;
    public final int dy;
    
    private Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    public Direction opposite() {
        switch (this) {
        case Down:
            return Up;
        case DownLeft:
            return UpRight;
        case DownRight:
            return UpLeft;
        case Left:
            return Right;
        case Right:
            return Left;
        case Up:
            return Down;
        case UpLeft:
            return DownRight;
        case UpRight:
            return DownLeft;
        default:
            return null;
        }
    }
    
    public Direction orth0() {
        switch (this) {
        case Down:
            return Right;
        case DownLeft:
            return DownRight;
        case DownRight:
            return UpRight;
        case Left:
            return Down;
        case Right:
            return Up;
        case Up:
            return Left;
        case UpLeft:
            return DownLeft;
        case UpRight:
            return UpLeft;
        default:
            return null;
        }
    }
    
    public Direction orth1() {
        switch (this) {
        case Down:
            return Left;
        case DownLeft:
            return UpLeft;
        case DownRight:
            return DownLeft;
        case Left:
            return Up;
        case Right:
            return Down;
        case Up:
            return Right;
        case UpLeft:
            return UpRight;
        case UpRight:
            return DownRight;
        default:
            return null;
        }
    }

}