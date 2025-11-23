package com.cat.robot;

public class RobotNotPlacedException extends IllegalStateException {
    public RobotNotPlacedException(String message) {
        super(message);
    }
}
