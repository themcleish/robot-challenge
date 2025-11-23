package com.cat.robot;

public record CommandDTO(
        String type,
        Integer x,
        Integer y,
        Direction direction
) {}
