package net.jwn.jwnprofile.util;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class Functions {
    public static List<FormattedCharSequence> wrapByCharacter(Component text, int maxWidth, Font font) {
        String raw = text.getString();
        List<FormattedCharSequence> result = new ArrayList<>();

        StringBuilder currentLine = new StringBuilder();

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            currentLine.append(c);

            int width = font.width(currentLine.toString());
            if (width > maxWidth) {
                currentLine.deleteCharAt(currentLine.length() - 1);
                result.add(font.split(Component.literal(currentLine.toString()), maxWidth).getFirst());
                currentLine.setLength(0);
                currentLine.append(c);
            }
        }
        if (!currentLine.isEmpty()) {
            result.add(font.split(Component.literal(currentLine.toString()), maxWidth).getFirst());
        }
        return result;
    }

    public static boolean isSafe(Player player) {
        AABB box = player.getBoundingBox().inflate(8.0);

        return player.level().getEntitiesOfClass(
                Monster.class,
                box,
                Entity::isAlive
        ).isEmpty();
    }
}
