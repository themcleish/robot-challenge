package com.cat.robot.service;

import com.cat.robot.dto.CommandDTO;
import com.cat.robot.exception.RobotNotAdjustedException;
import com.cat.robot.exception.RobotNotPlacedException;
import com.cat.robot.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RobotServiceImpl implements RobotService {

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
            throw new RobotNotPlacedException("Robot already on table");
        }

        if (isNotLegalPosition(position)) {
            log.warn("Robot placement FAILED at {} — outside table bounds. Current bounds = X=0..{}, Y=0..{}",
                    position, table.width() - 1, table.height() - 1);
            throw new RobotNotPlacedException("Intended placement outside table bounds");
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
            log.warn("Robot move ignored as no robot placed");
            throw new RobotNotAdjustedException("Robot move ignored as no robot placed");
        }

        Position possibleFuturePosition = robot.getPosition().move(robot.getDirection());
        if (isNotLegalPosition(possibleFuturePosition)) {
            log.warn("Robot movement FAILED, {} — outside table bounds. Current bounds = X=0..{}, Y=0..{}",
                    possibleFuturePosition, table.width() - 1, table.height() - 1);
            throw new RobotNotAdjustedException("Move ignored as intended location outside table bounds");
        }

        robot.updatePosition(possibleFuturePosition);
        log.info("Robot moved to {}", robot.getPosition());
        return true;
    }

    @Override
    public boolean turnLeft() {
        if (!robot.isPlaced()) {
            log.warn("Robot left turn ignored as no robot placed");
            throw new RobotNotAdjustedException("Robot left turn ignored as no robot placed");
        }

        robot.turnLeft();
        log.info("Robot turned left, current direction is {}", robot.getDirection());
        return true;
    }

    @Override
    public boolean turnRight() {
        if (!robot.isPlaced()) {
            log.warn("Robot right turn ignored as no robot placed");
            throw new RobotNotAdjustedException("Robot right turn ignored as no robot placed");
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

            try {
                switch (type) {
                    case "PLACE" -> handlePlaceCommand(command);
                    case "MOVE"  -> move();
                    case "LEFT"  -> turnLeft();
                    case "RIGHT" -> turnRight();
                    default      -> log.warn("Unknown command type '{}' ignored", command.type());
                }
            } catch (RobotNotPlacedException | RobotNotAdjustedException ex) {
                log.debug("{} command ignored during batch execution", type);
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

    @Override
    public void reset() {
        robot.reset();
        log.info("Robot has been reset and removed from the table");
    }
}
