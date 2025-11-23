package com.cat.robot;

public class RobotNotAdjustedException extends IllegalStateException {
    public RobotNotAdjustedException(String message) {
        super(message);
    }
}
