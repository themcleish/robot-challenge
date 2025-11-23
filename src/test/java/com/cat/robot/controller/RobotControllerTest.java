package com.cat.robot.controller;

import com.cat.robot.model.Direction;
import com.cat.robot.model.Position;
import com.cat.robot.model.RobotState;
import com.cat.robot.service.RobotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RobotController.class)
class RobotControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RobotService robotService;

    @Test
    void test_place_returnsOkAndState_whenPlacementSucceeds() throws Exception {
        RobotState state = new RobotState(new Position(1, 1), Direction.NORTH, true);

        when(robotService.place(eq(new Position(1, 1)), eq(Direction.NORTH))).thenReturn(true);
        when(robotService.getReport()).thenReturn(state);

        String json = """
                {
                  "x": 1,
                  "y": 1,
                  "direction": "NORTH"
                }
                """;

        mockMvc.perform(post("/robot/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position.x").value(1))
                .andExpect(jsonPath("$.position.y").value(1))
                .andExpect(jsonPath("$.direction").value("NORTH"))
                .andExpect(jsonPath("$.isPlaced").value(true));
    }

    @Test
    void test_place_returnsBadRequest_whenPlacementRejected() throws Exception {
        when(robotService.place(any(Position.class), eq(Direction.NORTH))).thenReturn(false);

        String json = """
                {
                  "x": 5,
                  "y": 1,
                  "direction": "NORTH"
                }
                """;

        mockMvc.perform(post("/robot/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_move_returnsOkAndState_whenMoveSucceeds() throws Exception {
        RobotState stateAfterMove = new RobotState(new Position(1, 2), Direction.NORTH, true);

        when(robotService.move()).thenReturn(true);
        when(robotService.getReport()).thenReturn(stateAfterMove);

        mockMvc.perform(post("/robot/move"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position.x").value(1))
                .andExpect(jsonPath("$.position.y").value(2))
                .andExpect(jsonPath("$.direction").value("NORTH"))
                .andExpect(jsonPath("$.isPlaced").value(true));
    }

    @Test
    void test_move_returnsBadRequest_whenMoveRejected() throws Exception {
        when(robotService.move()).thenReturn(false);

        mockMvc.perform(post("/robot/move"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_left_returnsOkAndState_whenTurnLeftSucceeds() throws Exception {
        RobotState stateAfterTurn = new RobotState(new Position(1, 1), Direction.WEST, true);

        when(robotService.turnLeft()).thenReturn(true);
        when(robotService.getReport()).thenReturn(stateAfterTurn);

        mockMvc.perform(post("/robot/left"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direction").value("WEST"))
                .andExpect(jsonPath("$.isPlaced").value(true));
    }

    @Test
    void test_left_returnsBadRequest_whenTurnLeftRejected() throws Exception {
        when(robotService.turnLeft()).thenReturn(false);

        mockMvc.perform(post("/robot/left"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_right_returnsOkAndState_whenTurnRightSucceeds() throws Exception {
        RobotState stateAfterTurn = new RobotState(new Position(1, 1), Direction.EAST, true);

        when(robotService.turnRight()).thenReturn(true);
        when(robotService.getReport()).thenReturn(stateAfterTurn);

        mockMvc.perform(post("/robot/right"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direction").value("EAST"))
                .andExpect(jsonPath("$.isPlaced").value(true));
    }

    @Test
    void test_right_returnsBadRequest_whenTurnRightRejected() throws Exception {
        when(robotService.turnRight()).thenReturn(false);

        mockMvc.perform(post("/robot/right"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_report_returnsOkAndState_whenRobotPlaced() throws Exception {
        RobotState state = new RobotState(new Position(0, 0), Direction.NORTH, true);

        when(robotService.getReport()).thenReturn(state);

        mockMvc.perform(get("/robot/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position.x").value(0))
                .andExpect(jsonPath("$.position.y").value(0))
                .andExpect(jsonPath("$.direction").value("NORTH"))
                .andExpect(jsonPath("$.isPlaced").value(true));
    }

    @Test
    void test_report_returnsOkWithUnplacedState_whenRobotNotPlaced() throws Exception {
        RobotState state = new RobotState(null, null, false);

        when(robotService.getReport()).thenReturn(state);

        mockMvc.perform(get("/robot/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").doesNotExist())
                .andExpect(jsonPath("$.direction").doesNotExist())
                .andExpect(jsonPath("$.isPlaced").value(false));
    }

    @Test
    void test_executeCommands_returnsOk() throws Exception {
        RobotState state = new RobotState(new Position(2, 2), Direction.EAST, true);

        when(robotService.executeCommands(any())).thenReturn(state);

        String json = """
                [
                  { "type": "PLACE", "x": 1, "y": 1, "direction": "NORTH" },
                  { "type": "MOVE" },
                  { "type": "RIGHT" },
                  { "type": "MOVE" }
                ]
                """;

        mockMvc.perform(post("/robot/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position.x").value(2))
                .andExpect(jsonPath("$.position.y").value(2))
                .andExpect(jsonPath("$.direction").value("EAST"))
                .andExpect(jsonPath("$.isPlaced").value(true));
    }
}
