package com.cat.robot.model;

public record Position(int x, int y) {

    public Position move(Direction direction) {
        return switch (direction) {
            case NORTH -> new Position(x, y + 1);
            case SOUTH -> new Position(x, y - 1);
            case EAST  -> new Position(x + 1, y);
            case WEST  -> new Position(x - 1, y);
        };
    }
}
