package me.bridge.helper.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BridgeDetector {
    private final Minecraft mc = Minecraft.getMinecraft();
    private double lastY = -1;
    private int consecutiveBridgeTicks = 0;

    public boolean isBridging(BlockPos placedPos) {
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return false;

        // Allow slight upward motion (jump-boosting onto blocks) but reject clear upward movement
        if (lastY != -1 && player.posY > lastY + 0.3) {
            lastY = player.posY;
            consecutiveBridgeTicks = 0;
            return false;
        }
        lastY = player.posY;

        // Must be moving backward (or backward-diagonal) — forward >= 0 means not bridging
        float forward = player.movementInput.moveForward;
        if (forward >= 0) {
            consecutiveBridgeTicks = 0;
            return false;
        }

        BlockPos playerPos = new BlockPos(player.posX, player.posY - 1, player.posZ);
        double distSq = placedPos.distanceSq(player.posX, player.posY - 1, player.posZ);
        
        // Allow blocks up to 5 blocks away (diagonal bridging can place further)
        if (distSq > 5) {
            consecutiveBridgeTicks = 0;
            return false;
        }

        // Require 2 consecutive ticks of bridging posture to avoid false positives
        consecutiveBridgeTicks++;
        if (consecutiveBridgeTicks < 2) return false;

        if (mc.objectMouseOver != null && mc.objectMouseOver.sideHit == EnumFacing.UP) {
            if (player.motionY > 0.1) return false;
        }

        return true;
    }
}
