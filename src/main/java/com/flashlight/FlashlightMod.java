package com.flashlight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FlashlightMod implements ClientModInitializer {
    public static final String MOD_ID = "flashlight";
    
    private static KeyBinding flashlightKey;
    public static boolean flashlightEnabled = false;
    
    @Override
    public void onInitializeClient() {
        // Register keybinding
        flashlightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.flashlight.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.flashlight"
        ));
        
        // Register tick event to check for key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (flashlightKey.wasPressed()) {
                flashlightEnabled = !flashlightEnabled;
            }
        });
        
        // Register the dynamic light handler
        DynamicLightHandler.register();
    }
}
