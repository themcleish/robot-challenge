package com.cat.robot.service;

import com.cat.robot.dto.CommandDTO;
import com.cat.robot.model.Direction;
import com.cat.robot.model.Position;
import com.cat.robot.model.RobotState;

import java.util.List;

public interface RobotService {

    /**
     * Attempts to place the robot at the given position and direction. If a robot already
     * placed, it will reject the placement.
     *
     * @param position  the desired position on the table.
     * @param direction the direction the robot should face after placement.
     * @return true if placement succeeds (legal position - inside table bounds), otherwise false.
     */
    boolean place(Position position, Direction direction);

    /**
     * Attempts a move, but will only move if the future position is
     * legal (resulting state is safe and on the table).
     *
     * @return true if move successful, otherwise false.
     */
    boolean move();

    /**
     * Rotates the robot left 90 degrees, does not change the position of the robot.
     *
     * @return true if robot is placed otherwise false.
     */
    boolean turnLeft();

    /**
     * Rotates the robot right 90 degrees, does not change the position of the robot.
     *
     * @return true if robot is placed otherwise false.
     */
    boolean turnRight();

    /**
     * Returns a report of the robots current state.
     */
    RobotState getReport();

    /**
     * Executes the passed list of commands ignoring any that can't be executed.
     * Then returns the final state of the robot.
     *
     * @param commands list of commands in the CommandDTO format.
     * @return robot state.
     */
    RobotState executeCommands(List<CommandDTO> commands);

    /**
     * Removes the robot from the table.
     */
    void reset();
}
