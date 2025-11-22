package com.cat.robot;

public record Table(int width, int height) {

    public boolean isInside(Position position) {
        int x = position.x();
        int y = position.y();

        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
