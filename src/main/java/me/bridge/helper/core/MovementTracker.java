package me.bridge.helper.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.ArrayDeque;
import java.util.Deque;

public class MovementTracker {
    private final Minecraft mc = Minecraft.getMinecraft();
    private Vec3 lastPos = null;
    private final Deque<Double> speedHistory = new ArrayDeque<>();
    private double avgSpeed = 0.0;

    private boolean isSneaking = false;
    private boolean isSprinting = false;
    private long lastUnsneakTime = 0;
    private boolean sprintAtUnsneak = false;

    public void update() {
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return;

        Vec3 currentPos = new Vec3(player.posX, player.posY, player.posZ);
        if (lastPos != null) {
            double distance = currentPos.distanceTo(lastPos);
            if (distance > 0.001) {
                speedHistory.addLast(distance);
                if (speedHistory.size() > 5) {
                    speedHistory.removeFirst();
                }

                double sum = 0;
                for (double s : speedHistory) {
                    sum += s;
                }
                avgSpeed = sum / speedHistory.size();
            }
        }
        lastPos = currentPos;
        isSneaking = player.isSneaking();
        isSprinting = player.isSprinting();
    }

    public void onSneakKey(boolean pressed) {
        if (isSneaking && !pressed) {
            lastUnsneakTime = System.currentTimeMillis();
            if (mc.thePlayer != null) {
                sprintAtUnsneak = mc.thePlayer.isSprinting();
            }
        }
        isSneaking = pressed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public long getLastUnsneakTime() {
        return lastUnsneakTime;
    }

    public boolean wasSprintingAtUnsneak() {
        return sprintAtUnsneak;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public boolean isSneaking() {
        return isSneaking;
    }

    public MovementDirection getDirection() {
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return MovementDirection.STATIONARY;

        float forward = player.movementInput.moveForward;
        float strafe = player.movementInput.moveStrafe;

        if (forward < 0) {
            if (strafe > 0) return MovementDirection.BACKWARD_LEFT;
            if (strafe < 0) return MovementDirection.BACKWARD_RIGHT;
            return MovementDirection.BACKWARD;
        }
        return MovementDirection.OTHER;
    }

    public enum MovementDirection {
        BACKWARD, BACKWARD_LEFT, BACKWARD_RIGHT, STATIONARY, OTHER
    }
}
