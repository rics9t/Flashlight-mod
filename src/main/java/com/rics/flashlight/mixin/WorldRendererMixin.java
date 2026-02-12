package com.rics.flashlight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

public class FlashlightModClient implements ClientModInitializer {

    public static KeyBinding flashlightKey;
    public static boolean isFlashlightOn = false;

    // Data passed to the Renderer
    public static Vec3d lightHitPos = null;
    public static double playerDistToTarget = 0;

    // The list of blocks from the other mod that should glow
    public static final Set<String> GLOWING_BLOCK_IDS = Set.of(
            "spb-revamped:tiny_fluorescent_light",
            "spb-revamped:ceiling_light",
            "spb-revamped:thin_fluorescent_light",
            "spb-revamped:fluorescent_light"
    );

    @Override
    public void onInitializeClient() {
        // Register Key "R"
        flashlightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.flashlight.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.flashlight"
        ));

        // Tick Event: Check toggles and calculate raycast
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // Toggle Logic
            while (flashlightKey.wasPressed()) {
                isFlashlightOn = !isFlashlightOn;
                // Play a click sound (optional, helps feedback)
                client.player.playSound(net.minecraft.sound.SoundEvents.BLOCK_LEVER_CLICK, 0.5f, isFlashlightOn ? 0.6f : 0.5f);
            }

            // Raycast Logic (Beam Physics Calculation)
            if (isFlashlightOn) {
                // Raycast 30 blocks ahead
                HitResult hit = client.player.raycast(30.0D, 0.0F, false);
                
                if (hit.getType() != HitResult.Type.MISS) {
                    lightHitPos = hit.getPos();
                    playerDistToTarget = client.player.getEyePos().distanceTo(lightHitPos);
                } else {
                    lightHitPos = null; // Pointing at sky
                }
            } else {
                lightHitPos = null;
            }
        });
    }
}