package com.cat.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RobotServiceImpl implements RobotService{

    private static final Logger log = LoggerFactory.getLogger(RobotServiceImpl.class);

    private final Table table;
    private final Robot robot = new Robot();

    public RobotServiceImpl(@Value("${robot.table.default.width}") int tableWidth,
                            @Value("${robot.table.default.height}") int tableHeight) {
        this.table = new Table(tableWidth, tableHeight);
    }

    /**
     * Attempts to place the robot at the given position and direction.
     *
     * @param position  the desired position on the table.
     * @param direction the direction the robot should face after placement.
     * @return true if placement succeeds (legal position - inside table bounds), otherwise false.
     */
    @Override
    public boolean place(Position position, Direction direction) {
        if (isLegalPosition(position)) {
            robot.place(position, direction);
            log.info("Robot successfully placed at {} facing {}", position, direction);
            return true;
        }
        log.warn("Robot placement FAILED at {} — outside table bounds. Current bounds = X=0..{}, Y=0..{}",
                position, table.width() - 1, table.height() - 1);
        return false;
    }

    /**
     * Checks if position is legal - inside table bounds.
     *
     * @param position
     * @return true if legal, otherwise false.
     */
    private boolean isLegalPosition(Position position) {
        return position.x() >= 0 && position.x() < table.width()
                && position.y() >= 0 && position.y() < table.height();
    }

    /**
     * Attempts a move, but will only move if the future position is
     * legal (resulting state is safe and on the table).
     *
     * @return true if move successful, otherwise false.
     */
    @Override
    public boolean move() {
        if (!robot.isPlaced()) {
            log.warn("Move ignored as no robot placed");
            return false;
        }
        Position possibleFuturePosition = robot.getPosition().move(robot.getDirection());
        if (isLegalPosition(possibleFuturePosition)) {
            robot.updatePosition(possibleFuturePosition);
            log.info("Robot moved to {}", robot.getPosition());
            return true;
        }
        log.warn("Robot movement FAILED, {} — outside table bounds. Current bounds = X=0..{}, Y=0..{}",
                possibleFuturePosition, table.width() - 1, table.height() - 1);
        return false;
    }

    @Override
    public void turnLeft() {
        if (robot.isPlaced()) {
            robot.turnLeft();
            log.info("Robot turned left, current direction is {}", robot.getDirection());
        } else {
            log.warn("Left turn ignored as no robot placed");
        }
    }

    @Override
    public void turnRight() {
        if (robot.isPlaced()) {
            robot.turnRight();
            log.info("Robot turned right, current direction is {}", robot.getDirection());
        } else {
            log.warn("Right turn ignored as no robot placed");
        }
    }

    @Override
    public RobotState getReport() {
        return new RobotState(robot.getPosition(), robot.getDirection(), robot.isPlaced());
    }
}
