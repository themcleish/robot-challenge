package com.cat.robot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        assertFalse(robotService.place(startPosition, startDirection));
        assertFalse(robotService.getReport().isPlaced());
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

        assertFalse(robotService.move());

        RobotState state = robotService.getReport();
        assertEquals(startPosition, state.position());
        assertEquals(startDirection, state.direction());
    }

    @Test
    void test_canTurnLeft() {
        Position startPosition = new Position(1,1);
        Direction startDirection = Direction.NORTH;
        robotService.place(startPosition, startDirection);

        robotService.turnLeft();

        assertEquals(Direction.WEST, robotService.getReport().direction());
    }

    @Test
    void test_canTurnRight() {
        Position startPosition = new Position(1,1);
        Direction startDirection = Direction.NORTH;
        robotService.place(startPosition, startDirection);

        robotService.turnRight();

        assertEquals(Direction.EAST, robotService.getReport().direction());
    }

    @Test
    void test_commandsIgnored_WhenRobotNotOnTable() {
        Position startPosition = new Position(5,1);
        Direction startDirection = Direction.NORTH;

        // Never actually placed as out of bounds.
        robotService.place(startPosition, startDirection);

        // These shouldn't block anything just log attempts
        robotService.turnRight();
        robotService.move();
        robotService.move();

        assertFalse(robotService.getReport().isPlaced());
    }
}