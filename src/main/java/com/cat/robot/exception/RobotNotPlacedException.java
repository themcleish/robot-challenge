package com.cat.robot.exception;

public class RobotNotPlacedException extends IllegalStateException {
    public RobotNotPlacedException(String message) {
        super(message);
    }
}
