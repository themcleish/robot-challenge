package com.cat.robot.dto;

import com.cat.robot.model.Direction;

public record CommandDTO(
        String type,
        Integer x,
        Integer y,
        Direction direction
) {}
