package net.jwn.jwnprofile.util;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

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
}
