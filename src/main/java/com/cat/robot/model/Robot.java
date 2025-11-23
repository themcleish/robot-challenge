package com.cat.robot.model;

public class Robot {

    private Position position;
    private Direction direction;
    private boolean placed = false;

    public boolean isPlaced() {
        return placed;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void place(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
        this.placed = true;
    }

    public void updatePosition(Position newPosition) {
        this.position = newPosition;
    }

    public void turnLeft() {
        this.direction = this.direction.turnLeft();
    }

    public void turnRight() {
        this.direction = this.direction.turnRight();
    }

    public void reset() {
        this.position = null;
        this.direction = null;
        this.placed = false;
    }
}