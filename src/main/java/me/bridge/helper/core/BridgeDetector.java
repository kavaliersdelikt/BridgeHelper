package me.bridge.helper.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BridgeDetector {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private double lastY = -1;

    public boolean isBridging(BlockPos placedPos) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return false;

        double playerY = player.getY();
        if (lastY != -1 && playerY > lastY + 0.1) {
            lastY = playerY;
            return false;
        }
        lastY = playerY;

        if (player.input == null || !player.input.pressingBack) return false;

        double dx = placedPos.getX() + 0.5 - player.getX();
        double dy = placedPos.getY() - (player.getY() - 1);
        double dz = placedPos.getZ() + 0.5 - player.getZ();
        double distSq = dx * dx + dy * dy + dz * dz;
        if (distSq > 4) return false;

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) mc.crosshairTarget;
            if (blockHit.getSide() == Direction.UP) {
                if (player.getVelocity().y > 0) return false;
            }
        }

        return true;
    }
}

