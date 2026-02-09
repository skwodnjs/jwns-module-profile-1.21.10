package net.jwn.jwnprofile.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class Keybinding {
    public static final String KEY_PLAYER_LIST = "key.jwnprofile.player_list";

    public static final KeyMapping PLAYER_LIST_KEY = new KeyMapping(KEY_PLAYER_LIST, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, KeyMapping.Category.MISC);
}
