package com.flashlight.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(BlockRenderView.class)
public interface BlockLightMixin {
    Set<String> LIGHT_BLOCKS = new HashSet<String>() {{
        add("spb-revamped:tiny_fluorescent_light");
        add("spb-revamped:ceiling_light");
        add("spb-revamped:thin_fluorescent_light");
        add("spb-revamped:fluorescent_light");
    }};
    
    @Inject(method = "getLightLevel", at = @At("HEAD"), cancellable = true)
    default void addBlockLight(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        BlockRenderView world = (BlockRenderView) this;
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        
        Identifier blockId = Registries.BLOCK.getId(block);
        String blockIdString = blockId.toString();
        
        if (LIGHT_BLOCKS.contains(blockIdString)) {
            cir.setReturnValue(10);
        }
    }
}
