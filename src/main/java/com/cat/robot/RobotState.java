package com.cat.robot;

public record RobotState(Position position, Direction direction, boolean isPlaced) {}