package me.bridge.helper.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BridgeDetector {
    private final Minecraft mc = Minecraft.getMinecraft();
    private double lastY = -1;

    public boolean isBridging(BlockPos placedPos) {
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return false;

        if (lastY != -1 && player.posY > lastY + 0.1) {
            lastY = player.posY;
            return false;
        }
        lastY = player.posY;

        float forward = player.movementInput.moveForward;
        if (forward >= 0) return false;

        BlockPos playerPos = new BlockPos(player.posX, player.posY - 1, player.posZ);
        double distSq = placedPos.distanceSq(player.posX, player.posY - 1, player.posZ);
        
        if (distSq > 4) return false;

        if (mc.objectMouseOver != null && mc.objectMouseOver.sideHit == EnumFacing.UP) {
            if (player.motionY > 0) return false;
        }

        return true;
    }
}
