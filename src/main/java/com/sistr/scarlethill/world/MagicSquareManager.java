package com.sistr.scarlethill.world;

import com.google.common.collect.Maps;
import com.sistr.scarlethill.item.MagicSquare;

import java.util.Map;
import java.util.UUID;

public class MagicSquareManager {
    public static final Map<UUID, MagicSquare> MAGIC_SQUARES = Maps.newHashMap();

    public static void tick() {
        //二回entrySet取ってるのが気になる
        MAGIC_SQUARES.entrySet().stream()
                .filter(entry -> entry.getValue().removed)
        .forEach(entry -> MAGIC_SQUARES.remove(entry.getKey(), entry.getValue()));
        MAGIC_SQUARES.forEach((uuid, magicSquare) -> magicSquare.tick());
    }
}
