package com.rics.flashlight.mixin;

import com.rics.flashlight.FlashlightModClient;
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
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        if (FlashlightModClient.GLOWING_BLOCK_IDS.contains(id.toString())) {
            blockLight = Math.max(blockLight, 10);
            modified = true;
        }

        // --- FEATURE 2: FLASHLIGHT BEAM ---
        if (FlashlightModClient.isFlashlightOn && FlashlightModClient.lightHitPos != null) {
            
            double distSq = pos.getSquaredDistance(FlashlightModClient.lightHitPos.x, FlashlightModClient.lightHitPos.y, FlashlightModClient.lightHitPos.z);
            double distPlayerToTarget = FlashlightModClient.playerDistToTarget;

            // Math to simulate beam width
            double spreadRadius = 1.5 + (distPlayerToTarget * 0.15); 

            if (distSq < (spreadRadius * spreadRadius)) {
                double falloff = 1.0 - (Math.sqrt(distSq) / spreadRadius);
                int baseIntensity = (distPlayerToTarget < 5) ? 15 : 12;
                int addedLight = (int) (baseIntensity * falloff);
                
                blockLight = Math.max(blockLight, addedLight);
                modified = true;
            }
        }

        if (modified) {
            blockLight = MathHelper.clamp(blockLight, 0, 15);
            int newLight = (skyLight << 20) | (blockLight << 4);
            cir.setReturnValue(newLight);
        }
    }
}