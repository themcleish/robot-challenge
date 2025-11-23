package com.cat.robot.service;

import com.cat.robot.dto.CommandDTO;
import com.cat.robot.exception.RobotNotAdjustedException;
import com.cat.robot.exception.RobotNotPlacedException;
import com.cat.robot.model.Direction;
import com.cat.robot.model.Position;
import com.cat.robot.model.RobotState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RobotServiceImplTest {

    RobotService robotService;

    @BeforeEach
    void setUp() {
        robotService = new RobotServiceImpl(5,5);
    }

    @Test
    void test_canPlace_WhenWithinTableBounds() {
        Position startPosition = new Position(1,1);
        Direction startDirection = Direction.NORTH;

        assertTrue(robotService.place(startPosition, startDirection));
        RobotState state = robotService.getReport();
        assertTrue(state.isPlaced());
        assertEquals(startPosition, state.position());
        assertEquals(startDirection, state.direction());
    }

    @Test
    void test_canNotPlace_WhenNotWithinTableBounds() {
        Position startPosition = new Position(5,1);
        Direction startDirection = Direction.NORTH;

        RobotNotPlacedException ex = assertThrows(
                RobotNotPlacedException.class,
                () -> robotService.place(startPosition, startDirection)
        );
        assertEquals("Intended placement outside table bounds", ex.getMessage());
        assertFalse(robotService.getReport().isPlaced());
    }

    @Test
    void test_canNotPlace_WhenExistingRobotAlreadyOnTable() {
        Position startPosition = new Position(1,1);
        Direction startDirection = Direction.NORTH;
        robotService.place(startPosition, startDirection);

        Position secondPosition = new Position(3,2);

        RobotNotPlacedException ex = assertThrows(
                RobotNotPlacedException.class,
                () -> robotService.place(secondPosition, startDirection)
        );
        assertEquals("Robot already on table", ex.getMessage());

        RobotState state = robotService.getReport();
        assertTrue(state.isPlaced());
        assertEquals(startPosition, state.position());
        assertEquals(startDirection, state.direction());
    }

    @Test
    void test_canMove_whenForwardCellIsWithinTableBounds() {
        Position startPosition = new Position(1,1);
        Direction startDirection = Direction.NORTH;
        robotService.place(startPosition, startDirection);

        assertTrue(robotService.move());

        Position expectedFuturePosition = new Position(1, 2);
        RobotState state = robotService.getReport();
        assertEquals(expectedFuturePosition, state.position());
        assertEquals(startDirection, state.direction());
    }

    @Test
    void test_canNotMove_whenForwardCellIsNotWithinTableBounds() {
        Position startPosition = new Position(1,4);
        Direction startDirection = Direction.NORTH;
        robotService.place(startPosition, startDirection);

        assertThrows(RobotNotAdjustedException.class, () -> robotService.move());

        RobotState state = robotService.getReport();
        assertEquals(startPosition, state.position());
        assertEquals(startDirection, state.direction());
    }

    @Test
    void test_validMovesAllowedAfterInvalidMove() {
        Position startPosition = new Position(1,4);
        Direction startDirection = Direction.NORTH;
        robotService.place(startPosition, startDirection);

        // Ignored Move
        assertThrows(RobotNotAdjustedException.class, () -> robotService.move());

        // Some valid commands
        robotService.turnRight();
        robotService.turnRight();
        robotService.move();

        Direction finalExpectedDirection = Direction.SOUTH;
        Position finalExpectedPosition = new Position(1,3);
        RobotState state = robotService.getReport();
        assertEquals(finalExpectedPosition, state.position());
        assertEquals(finalExpectedDirection, state.direction());
    }

    @Test
    void test_canTurnLeft() {
        Position startPosition = new Position(1,1);
        Direction startDirection = Direction.NORTH;
        robotService.place(startPosition, startDirection);

        assertTrue(robotService.turnLeft());
        assertEquals(Direction.WEST, robotService.getReport().direction());
    }

    @Test
    void test_canTurnRight() {
        Position startPosition = new Position(1,1);
        Direction startDirection = Direction.NORTH;
        robotService.place(startPosition, startDirection);

        assertTrue(robotService.turnRight());
        assertEquals(Direction.EAST, robotService.getReport().direction());
    }

    @Test
    void test_commandsIgnored_WhenRobotNotOnTable() {
        Position startPosition = new Position(5,1);
        Direction startDirection = Direction.NORTH;

        assertThrows(RobotNotPlacedException.class,
                () -> robotService.place(startPosition, startDirection));
        assertFalse(robotService.getReport().isPlaced());

        // These should throw appropriate exceptions
        assertThrows(RobotNotAdjustedException.class,
                () -> robotService.turnRight());
        assertThrows(RobotNotAdjustedException.class,
                () -> robotService.move());
        assertThrows(RobotNotAdjustedException.class,
                () -> robotService.move());
        assertFalse(robotService.getReport().isPlaced());

        // Further valid movements allowed
        Position secondStartPosition = new Position(1,1);
        robotService.place(secondStartPosition, startDirection);
        robotService.turnLeft();
        robotService.move();

        Direction finalExpectedDirection = Direction.WEST;
        Position finalExpectedPosition = new Position(0,1);
        RobotState state = robotService.getReport();
        assertEquals(finalExpectedPosition, state.position());
        assertEquals(finalExpectedDirection, state.direction());
    }

    @Test
    void test_executeCommands_runsValidSequenceAndUpdatesState() {
        List<CommandDTO> commands = Arrays.asList(
                new CommandDTO("PLACE", 1, 1, Direction.NORTH),
                new CommandDTO("MOVE", null, null, null),
                new CommandDTO("RIGHT", null, null, null),
                new CommandDTO("MOVE", null, null, null)
        );

        RobotState finalState = robotService.executeCommands(commands);

        Position expectedFinalPosition = new Position(2, 2);
        Direction expectedFinalDirection = Direction.EAST;
        assertTrue(finalState.isPlaced());
        assertEquals(expectedFinalPosition, finalState.position());
        assertEquals(expectedFinalDirection, finalState.direction());
    }

    @Test
    void test_executeCommands_ignoresNullAndUnknownCommands() {
        List<CommandDTO> commands = Arrays.asList(
                null,
                new CommandDTO(null, null, null, null),
                new CommandDTO("SPIN", null, null, null),
                new CommandDTO("PLACE", 1, 1, Direction.NORTH),
                new CommandDTO("MOVE", null, null, null),
                new CommandDTO("WAVE", null, null, null)
        );

        RobotState finalState = robotService.executeCommands(commands);

        Position expectedFinalPosition = new Position(1, 2);
        Direction expectedFinalDirection = Direction.NORTH;
        assertTrue(finalState.isPlaced());
        assertEquals(expectedFinalPosition, finalState.position());
        assertEquals(expectedFinalDirection, finalState.direction());
    }

    @Test
    void test_executeCommands_handlesInvalidPlaceMissingFields() {
        List<CommandDTO> commands = Arrays.asList(
                new CommandDTO("PLACE", null, 1, Direction.NORTH), // missing x
                new CommandDTO("PLACE", 1, 1, Direction.NORTH),
                new CommandDTO("MOVE", null, null, null)
        );

        RobotState finalState = robotService.executeCommands(commands);

        Position expectedFinalPosition = new Position(1, 2);
        Direction expectedFinalDirection = Direction.NORTH;
        assertTrue(finalState.isPlaced());
        assertEquals(expectedFinalPosition, finalState.position());
        assertEquals(expectedFinalDirection, finalState.direction());
    }
}