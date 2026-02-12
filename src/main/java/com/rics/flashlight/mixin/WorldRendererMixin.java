package com.example.ricsashlight.mixin;

import com.example.ricsashlight.ricsashlightModClient;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I", at = @At("RETURN"), cancellable = true)
    private static void modifyLightmapCoordinates(BlockRenderView world, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        
        int originalLight = cir.getReturnValue();
        int skyLight = (originalLight >> 20) & 15;
        int blockLight = (originalLight >> 4) & 15;

        boolean modified = false;

        // --- FEATURE 1: SPB-REVAMPED GLOW ---
        // Get the string ID of the block being rendered
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        if (ricsashlightModClient.GLOWING_BLOCK_IDS.contains(id.toString())) {
            // Force light level 10 (or higher if the world is naturally brighter)
            blockLight = Math.max(blockLight, 10);
            modified = true;
        }

        // --- FEATURE 2: ricsASHLIGHT BEAM ---
        if (ricsashlightModClient.isricsashlightOn && ricsashlightModClient.lightHitPos != null) {
            
            // Calculate distance from this block to the point the ricsashlight hit
            double distSq = pos.getSquaredDistance(ricsashlightModClient.lightHitPos.x, ricsashlightModClient.lightHitPos.y, ricsashlightModClient.lightHitPos.z);
            double distPlayerToTarget = ricsashlightModClient.playerDistToTarget;

            // BEAM PHYSICS LOGIC:
            // 1. If we are close (dist < 5), the beam is focused (radius small), light is bright (15).
            // 2. If we are far (dist > 20), the beam spreads (radius large), light is dimmer (11-12).
            
            // Calculate "Spread Radius" based on distance
            // At 0 distance, radius is 1.5. At 20 distance, radius is 4.5.
            double spreadRadius = 1.5 + (distPlayerToTarget * 0.15); 

            // Check if this block is within the ricsashlight circle
            if (distSq < (spreadRadius * spreadRadius)) {
                
                // Calculate Falloff (center is brightest, edges are dim)
                double falloff = 1.0 - (Math.sqrt(distSq) / spreadRadius);
                
                // Base intensity drops as player gets farther away
                int baseIntensity = (distPlayerToTarget < 5) ? 15 : 12;
                
                // Calculate final added light for this specific block
                int addedLight = (int) (baseIntensity * falloff);
                
                // Apply the light (Keep the highest value so we don't darken already bright blocks)
                blockLight = Math.max(blockLight, addedLight);
                modified = true;
            }
        }

        // Re-pack the light value if we changed anything
        if (modified) {
            // Clamp to 15
            blockLight = MathHelper.clamp(blockLight, 0, 15);
            // Reconstruct the integer: (Sky << 20) | (Block << 4)
            int newLight = (skyLight << 20) | (blockLight << 4);
            cir.setReturnValue(newLight);
        }
    }
}