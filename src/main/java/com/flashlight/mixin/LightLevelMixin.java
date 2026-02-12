package com.flashlight.mixin;

import com.flashlight.DynamicLightHandler;
import com.flashlight.FlashlightMod;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderView.class)
public interface LightLevelMixin {
    @Inject(method = "getLightLevel", at = @At("RETURN"), cancellable = true)
    default void getDynamicLightLevel(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        int originalLight = cir.getReturnValue();
        int dynamicLight = calculateDynamicLight(pos, client);
        
        if (dynamicLight > originalLight) {
            cir.setReturnValue(dynamicLight);
        }
    }
    
    default int calculateDynamicLight(BlockPos pos, MinecraftClient client) {
        int maxLight = 0;
        
        // Check flashlight
        if (FlashlightMod.flashlightEnabled && client.player != null) {
            Vec3d playerEye = client.player.getEyePos();
            Vec3d lookVec = client.player.getRotationVec(1.0f);
            
            Vec3d blockCenter = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            double distToPlayer = playerEye.distanceTo(blockCenter);
            
            if (distToPlayer <= 16.0) {
                // Check if block is in flashlight beam
                Vec3d toBlock = blockCenter.subtract(playerEye).normalize();
                double dotProduct = toBlock.dotProduct(lookVec);
                
                if (dotProduct > 0.9) { // Within cone
                    // Calculate light based on distance
                    int lightLevel = (int) (15 - (distToPlayer * 0.6));
                    
                    // Calculate spread factor
                    double beamWidth = Math.max(0.5, distToPlayer * 0.15);
                    double offsetFromCenter = blockCenter.subtract(playerEye.add(lookVec.multiply(distToPlayer))).length();
                    
                    if (offsetFromCenter <= beamWidth) {
                        double spreadFactor = 1.0 - (offsetFromCenter / beamWidth);
                        lightLevel = (int) (lightLevel * spreadFactor);
                        maxLight = Math.max(maxLight, Math.max(0, lightLevel));
                    }
                }
            }
        }
        
        return maxLight;
    }
}
