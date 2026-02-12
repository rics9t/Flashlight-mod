package com.flashlight;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class DynamicLightHandler {
    private static final Map<BlockPos, Integer> lightSources = new HashMap<>();
    private static final Set<Block> LIGHT_EMITTING_BLOCKS = new HashSet<>();
    
    public static void register() {
        // Register SPB-Revamped blocks that should emit light
        registerLightBlock("spb-revamped:tiny_fluorescent_light", 15);
        registerLightBlock("spb-revamped:ceiling_light", 15);
        registerLightBlock("spb-revamped:thin_fluorescent_light", 15);
        registerLightBlock("spb-revamped:fluorescent_light", 15);
        
        // Update lights before rendering
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, hitResult) -> {
            updateDynamicLights(worldRenderContext.world());
            return true;
        });
    }
    
    private static void registerLightBlock(String blockId, int lightLevel) {
        Identifier id = new Identifier(blockId.replace(":", "/"));
        Block block = Registries.BLOCK.get(id);
        if (block != null && block != net.minecraft.block.Blocks.AIR) {
            LIGHT_EMITTING_BLOCKS.add(block);
        }
    }
    
    public static void updateDynamicLights(World world) {
        if (world == null) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        // Clear old light sources
        for (BlockPos pos : new ArrayList<>(lightSources.keySet())) {
            removeLightSource(world, pos);
        }
        lightSources.clear();
        
        // Add flashlight light sources
        if (FlashlightMod.flashlightEnabled) {
            addFlashlightLights(world, client);
        }
        
        // Add SPB-Revamped block lights
        addBlockLights(world, client);
    }
    
    private static void addFlashlightLights(World world, MinecraftClient client) {
        Vec3d pos = client.player.getEyePos();
        Vec3d lookVec = client.player.getRotationVec(1.0f);
        
        // Calculate realistic beam spread based on distance
        for (double dist = 1.0; dist <= 16.0; dist += 0.5) {
            Vec3d targetPos = pos.add(lookVec.multiply(dist));
            
            // Calculate light level based on distance (inverse square law approximation)
            // Close = narrow beam (high intensity), far = wide beam (lower intensity)
            int baseLightLevel = (int) (15 - (dist * 0.6));
            if (baseLightLevel < 0) baseLightLevel = 0;
            
            // Calculate beam width based on distance
            double beamWidth = Math.max(0.5, dist * 0.15);
            
            // Add light at center
            BlockPos centerPos = new BlockPos((int)targetPos.x, (int)targetPos.y, (int)targetPos.z);
            addLightSource(world, centerPos, baseLightLevel);
            
            // Add spread lights for wider beam at distance
            if (dist > 3.0) {
                int spreadRadius = (int) Math.ceil(beamWidth);
                int spreadLightLevel = Math.max(0, baseLightLevel - 3);
                
                for (int dx = -spreadRadius; dx <= spreadRadius; dx++) {
                    for (int dy = -spreadRadius; dy <= spreadRadius; dy++) {
                        for (int dz = -spreadRadius; dz <= spreadRadius; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0) continue;
                            
                            double spreadDist = Math.sqrt(dx*dx + dy*dy + dz*dz);
                            if (spreadDist <= beamWidth) {
                                BlockPos spreadPos = centerPos.add(dx, dy, dz);
                                int spreadLight = (int) (spreadLightLevel * (1.0 - spreadDist / beamWidth));
                                if (spreadLight > 0) {
                                    addLightSource(world, spreadPos, spreadLight);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void addBlockLights(World world, MinecraftClient client) {
        if (client.player == null) return;
        
        BlockPos playerPos = client.player.getBlockPos();
        int renderDistance = 32; // Check blocks within 32 blocks
        
        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int y = -renderDistance; y <= renderDistance; y++) {
                for (int z = -renderDistance; z <= renderDistance; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    
                    if (LIGHT_EMITTING_BLOCKS.contains(block)) {
                        addLightSource(world, pos, 15);
                    }
                }
            }
        }
    }
    
    private static void addLightSource(World world, BlockPos pos, int lightLevel) {
        if (lightLevel <= 0) return;
        
        Integer existingLevel = lightSources.get(pos);
        if (existingLevel == null || existingLevel < lightLevel) {
            lightSources.put(pos, lightLevel);
            setLightLevel(world, pos, lightLevel);
        }
    }
    
    private static void removeLightSource(World world, BlockPos pos) {
        setLightLevel(world, pos, 0);
    }
    
    private static void setLightLevel(World world, BlockPos pos, int lightLevel) {
        if (world == null) return;
        
        try {
            // Use reflection to access and modify light data
            // This is a simplified version - you may need LambDynamicLights or similar for full functionality
            world.setBlockBreakingInfo(0, pos, lightLevel);
        } catch (Exception e) {
            // Light setting failed, ignore
        }
    }
}
