package me.bridge.helper.core;

import me.bridge.helper.config.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class TimingAnalyzer {
    private final SettingsManager settings = SettingsManager.getInstance();

    public TimingResult analyze(long unsneakTime, long placeTime, double avgSpeed, boolean sprinting) {
        if (avgSpeed <= 0) return null;

        long deltaTime = placeTime - unsneakTime;
        
        double baseDistance = settings.idealEdgeDistance;
        double speed = settings.movementCompensation ? avgSpeed : (sprinting ? 0.2806 : 0.2158);
        
        if (settings.diagonalAdjustment) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            if (player != null && player.movementInput.moveStrafe != 0) {
                baseDistance *= 1.20;
            }
        }

        speed = Math.max(0.15, Math.min(0.29, speed));

        double idealTicks = (baseDistance - settings.safeMargin) / speed;
        double idealMs = idealTicks * 50.0;

        double error = (double) deltaTime - idealMs + 5.0;

        int perfectWindow = settings.perfectWindow;
        if (sprinting && settings.sprintToleranceAdjustment) {
            perfectWindow += 3;
        }

        Classification classification;
        if (Math.abs(error) <= perfectWindow) {
            classification = Classification.PERFECT;
        } else if (error < 0) {
            classification = Classification.TOO_EARLY;
        } else {
            classification = Classification.TOO_LATE;
        }

        return new TimingResult(deltaTime, idealMs, error, classification);
    }

    public static class TimingResult {
        public final long deltaTime;
        public final double idealMs;
        public final double error;
        public final Classification classification;

        public TimingResult(long deltaTime, double idealMs, double error, Classification classification) {
            this.deltaTime = deltaTime;
            this.idealMs = idealMs;
            this.error = error;
            this.classification = classification;
        }
    }

    public enum Classification {
        PERFECT, TOO_EARLY, TOO_LATE
    }
}
