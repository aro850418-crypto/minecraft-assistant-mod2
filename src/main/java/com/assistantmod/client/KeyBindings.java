package com.assistantmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static final KeyMapping SHOW_COORDS = new KeyMapping(
        "key.assistantmod.show_coords",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_H,
        "key.categories.assistantmod"
    );

    public static void register() {
        net.minecraftforge.client.ClientRegistry.registerKeyBinding(SHOW_COORDS);
    }
}
