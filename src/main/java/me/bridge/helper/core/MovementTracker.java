package me.bridge.helper.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Deque;

public class MovementTracker {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private Vec3d lastPos = null;
    private final Deque<Double> speedHistory = new ArrayDeque<>();
    private double avgSpeed = 0.0;

    private boolean isSneaking = false;
    private boolean isSprinting = false;
    private long lastUnsneakTime = 0;
    private boolean sprintAtUnsneak = false;

    public void update() {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        Vec3d currentPos = player.getPos();
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

        boolean nowSneaking = player.isSneaking();
        if (isSneaking && !nowSneaking) {
            lastUnsneakTime = System.currentTimeMillis();
            sprintAtUnsneak = player.isSprinting();
        }
        isSneaking = nowSneaking;
        isSprinting = player.isSprinting();
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
        ClientPlayerEntity player = mc.player;
        if (player == null || player.input == null) return MovementDirection.STATIONARY;

        if (player.input.pressingBack) {
            if (player.input.pressingLeft) return MovementDirection.BACKWARD_LEFT;
            if (player.input.pressingRight) return MovementDirection.BACKWARD_RIGHT;
            return MovementDirection.BACKWARD;
        }
        return MovementDirection.OTHER;
    }

    public enum MovementDirection {
        BACKWARD, BACKWARD_LEFT, BACKWARD_RIGHT, STATIONARY, OTHER
    }
}

