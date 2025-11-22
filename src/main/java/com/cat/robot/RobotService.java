package com.cat.robot;

public interface RobotService {

    boolean place(Position position, Direction direction);
    boolean move();
    void turnLeft();
    void turnRight();
    RobotState getReport();
}
