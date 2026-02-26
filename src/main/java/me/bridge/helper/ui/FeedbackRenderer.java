package me.bridge.helper.ui;

import me.bridge.helper.config.SettingsManager;
import me.bridge.helper.core.TimingAnalyzer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.awt.*;

public class FeedbackRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final SettingsManager settings = SettingsManager.getInstance();

    private TimingAnalyzer.Classification currentClassification = null;
    private TimingAnalyzer.TimingResult lastResult = null;
    private long lastFeedbackTime = 0;
    private boolean showSprintIcon = false;

    public void register() {
        HudRenderCallback.EVENT.register(this::onHudRender);
    }

    public void postFeedback(TimingAnalyzer.TimingResult result, boolean sprinting) {
        this.lastResult = result;
        this.currentClassification = result.classification;
        this.lastFeedbackTime = System.currentTimeMillis();
        this.showSprintIcon = sprinting;

        if (settings.soundEnabled && mc.player != null) {
            float pitch;
            if (result.classification == TimingAnalyzer.Classification.PERFECT) pitch = 1.5f;
            else if (result.classification == TimingAnalyzer.Classification.TOO_EARLY) pitch = 0.8f;
            else pitch = 0.5f;

            mc.getSoundManager().play(
                    PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), pitch, settings.soundVolume)
            );
        }
    }

    private void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        if (!settings.enabled || currentClassification == null) return;

        long elapsed = System.currentTimeMillis() - lastFeedbackTime;
        if (elapsed > settings.feedbackDuration) {
            currentClassification = null;
            return;
        }

        render(context, elapsed);
    }

    private void render(DrawContext context, long elapsed) {
        String text;
        if (settings.userFriendlyMode) {
            if (currentClassification == TimingAnalyzer.Classification.PERFECT) {
                text = "PERFECT!";
            } else {
                double error = lastResult.error;
                if (error < -15) text = "WAY TOO EARLY";
                else if (error < 0) text = "SLIGHTLY EARLY";
                else if (error > 15) text = "WAY TOO LATE";
                else text = "SLIGHTLY LATE";
            }
        } else {
            text = currentClassification.name().replace("_", " ");
        }

        int color = getClassificationColor(currentClassification);

        float progress = (float) elapsed / settings.feedbackDuration;
        float alpha = 1.0f;
        if (progress > 0.8f) {
            alpha = 1.0f - (progress - 0.8f) / 0.2f;
        }

        float scaleProgress = Math.min(1.0f, (float) elapsed / 200f);
        float scale = easeOutBack(scaleProgress) * settings.feedbackScale;

        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();
        int x = (int) (screenW * settings.posX);
        int y = (int) (screenH * settings.posY);

        int textWidth = mc.textRenderer.getWidth(text);
        int textHeight = mc.textRenderer.fontHeight;

        int finalColor = (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1);

        if (settings.roundedCorners) {
            int padding = 4;
            int extraLines = (settings.showRawDeltaTime ? 1 : 0)
                    + (settings.showIdealMs ? 1 : 0)
                    + (settings.showAvgSpeed ? 1 : 0);
            int bgH = textHeight + (extraLines > 0 ? extraLines * 10 + 2 : 0);
            int bgAlpha = (int) (alpha * 0xAA);
            context.fill(
                    -textWidth / 2 - padding, -textHeight / 2 - padding,
                    textWidth / 2 + padding, textHeight / 2 + bgH + padding,
                    bgAlpha << 24
            );
        }

        context.drawText(mc.textRenderer, text,
                -textWidth / 2, -textHeight / 2, finalColor, settings.shadowEnabled);

        int extraY = textHeight / 2 + 2;
        if (settings.showRawDeltaTime) {
            context.drawText(mc.textRenderer,
                    "Delta: " + lastResult.deltaTime + "ms",
                    -textWidth / 2, extraY, 0xAAFFFFFF, true);
            extraY += 10;
        }
        if (settings.showIdealMs) {
            context.drawText(mc.textRenderer,
                    "Ideal: " + String.format("%.1f", lastResult.idealMs) + "ms",
                    -textWidth / 2, extraY, 0xAAFFFFFF, true);
            extraY += 10;
        }
        if (settings.showAvgSpeed) {
            context.drawText(mc.textRenderer,
                    "Speed: " + String.format("%.2f", lastResult.error),
                    -textWidth / 2, extraY, 0xAAFFFFFF, true);
            extraY += 10;
        }

        if (settings.tickVisualization) {
            int barW = 40;
            int barH = 2;
            context.fill(-barW / 2, extraY, barW / 2, extraY + barH, 0x55FFFFFF);
            int markerPos = (int) ((lastResult.error / 50.0) * (barW / 2f));
            markerPos = Math.max(-barW / 2, Math.min(barW / 2, markerPos));
            context.fill(markerPos - 1, extraY - 1, markerPos + 1, extraY + barH + 1, finalColor);
        }

        if (settings.sprintIndicator && showSprintIcon) {
            String sprintChar = "[S]";
            int sprintW = mc.textRenderer.getWidth(sprintChar);
            context.drawText(mc.textRenderer, sprintChar, -sprintW / 2, extraY + 5, finalColor, false);
        }

        context.getMatrices().pop();
    }

    private int getClassificationColor(TimingAnalyzer.Classification classification) {
        switch (classification) {
            case PERFECT: return Color.GREEN.getRGB();
            case TOO_EARLY: return Color.YELLOW.getRGB();
            case TOO_LATE: return Color.RED.getRGB();
            default: return -1;
        }
    }

    private float easeOutBack(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
    }
}

