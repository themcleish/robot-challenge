package com.cat.robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RobotServiceImpl implements RobotService{

    private static final Logger log = LoggerFactory.getLogger(RobotServiceImpl.class);

    private final Table table;
    private final Robot robot = new Robot();

    public RobotServiceImpl(@Value("${robot.table.default.width}") int tableWidth,
                            @Value("${robot.table.default.height}") int tableHeight) {
        this.table = new Table(tableWidth, tableHeight);
    }

    @Override
    public boolean place(Position position, Direction direction) {
        if (robot.isPlaced()) {
            log.warn("Robot placement FAILED, robot already on table at {} facing {}", robot.getPosition(), robot.getDirection());
            return false;
        }

        if (isNotLegalPosition(position)) {
            log.warn("Robot placement FAILED at {} — outside table bounds. Current bounds = X=0..{}, Y=0..{}",
                    position, table.width() - 1, table.height() - 1);
            return false;
        }

        robot.place(position, direction);
        log.info("Robot successfully placed at {} facing {}", position, direction);
        return true;
    }

    /**
     * Checks if position is legal - inside table bounds.
     *
     * @param position to be checked.
     * @return true if not legal, otherwise false.
     */
    private boolean isNotLegalPosition(Position position) {
        return !table.isInside(position);
    }

    @Override
    public boolean move() {
        if (!robot.isPlaced()) {
            log.warn("Move ignored as no robot placed");
            return false;
        }

        Position possibleFuturePosition = robot.getPosition().move(robot.getDirection());
        if (isNotLegalPosition(possibleFuturePosition)) {
            log.warn("Robot movement FAILED, {} — outside table bounds. Current bounds = X=0..{}, Y=0..{}",
                    possibleFuturePosition, table.width() - 1, table.height() - 1);
            return false;
        }

        robot.updatePosition(possibleFuturePosition);
        log.info("Robot moved to {}", robot.getPosition());
        return true;
    }

    @Override
    public boolean turnLeft() {
        if (!robot.isPlaced()) {
            log.warn("Left turn ignored as no robot placed");
            return false;
        }

        robot.turnLeft();
        log.info("Robot turned left, current direction is {}", robot.getDirection());
        return true;
    }

    @Override
    public boolean turnRight() {
        if (!robot.isPlaced()) {
            log.warn("Right turn ignored as no robot placed");
            return false;
        }

        robot.turnRight();
        log.info("Robot turned right, current direction is {}", robot.getDirection());
        return true;
    }

    @Override
    public RobotState getReport() {
        return new RobotState(robot.getPosition(), robot.getDirection(), robot.isPlaced());
    }

    @Override
    public RobotState executeCommands(List<CommandDTO> commands) {
        if (commands == null) {
            log.warn("Cannot call executeCommands with null");
            return getReport();
        }

        for (CommandDTO command : commands) {
            if (command == null || command.type() == null) {
                continue;
            }

            String type = command.type().trim().toUpperCase();

            switch (type) {
                case "PLACE" -> handlePlaceCommand(command);
                case "MOVE"  -> move();
                case "LEFT"  -> turnLeft();
                case "RIGHT" -> turnRight();
                default -> log.warn("Unknown command type '{}' ignored", command.type());
            }
        }

        return getReport();
    }

    private void handlePlaceCommand(CommandDTO command) {
        if (command.x() == null || command.y() == null || command.direction() == null) {
            log.warn("PLACE command missing required fields: {}", command);
            return;
        }

        place(new Position(command.x(), command.y()), command.direction());
    }
}
