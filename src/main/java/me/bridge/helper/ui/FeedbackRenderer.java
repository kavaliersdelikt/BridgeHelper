package me.bridge.helper.ui;

import me.bridge.helper.config.SettingsManager;
import me.bridge.helper.core.TimingAnalyzer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class FeedbackRenderer extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final SettingsManager settings = SettingsManager.getInstance();

    private TimingAnalyzer.Classification currentClassification = null;
    private TimingAnalyzer.TimingResult lastResult = null;
    private long lastFeedbackTime = 0;
    private boolean showSprintIcon = false;

    public void postFeedback(TimingAnalyzer.TimingResult result, boolean sprinting) {
        this.lastResult = result;
        this.currentClassification = result.classification;
        this.lastFeedbackTime = System.currentTimeMillis();
        this.showSprintIcon = sprinting;

        if (settings.soundEnabled && mc.thePlayer != null) {
            float pitch = 1.0f;
            if (result.classification == TimingAnalyzer.Classification.PERFECT) pitch = 1.5f;
            else if (result.classification == TimingAnalyzer.Classification.TOO_EARLY) pitch = 0.8f;
            else if (result.classification == TimingAnalyzer.Classification.TOO_LATE) pitch = 0.5f;
            
            mc.thePlayer.playSound("gui.button.press", settings.soundVolume, pitch);
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!settings.enabled || currentClassification == null || event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        long elapsed = System.currentTimeMillis() - lastFeedbackTime;
        if (elapsed > settings.feedbackDuration) {
            currentClassification = null;
            return;
        }

        render(event.resolution, elapsed);
    }

    private void render(ScaledResolution res, long elapsed) {
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

        int x = (int) (res.getScaledWidth() * settings.posX);
        int y = (int) (res.getScaledHeight() * settings.posY);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int textWidth = mc.fontRendererObj.getStringWidth(text);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;

        if (settings.roundedCorners) {
            int padding = 4;
            int bgH = textHeight + (settings.showRawDeltaTime || settings.showIdealMs || settings.showAvgSpeed ? 20 : 0);
            drawRect(-textWidth / 2 - padding, -textHeight / 2 - padding, textWidth / 2 + padding, textHeight / 2 + bgH + padding, (int) (alpha * 0xAA) << 24);
        }

        int finalColor = (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
        
        if (settings.shadowEnabled) {
            mc.fontRendererObj.drawStringWithShadow(text, -textWidth / 2f, -textHeight / 2f, finalColor);
        } else {
            mc.fontRendererObj.drawString(text, -textWidth / 2, -textHeight / 2, finalColor);
        }

        float extraY = textHeight / 2f + 2;
        if (settings.showRawDeltaTime) {
            mc.fontRendererObj.drawStringWithShadow("Delta: " + lastResult.deltaTime + "ms", -textWidth / 2f, extraY, 0xAAFFFFFF);
            extraY += 10;
        }
        if (settings.showIdealMs) {
            mc.fontRendererObj.drawStringWithShadow("Ideal: " + String.format("%.1f", lastResult.idealMs) + "ms", -textWidth / 2f, extraY, 0xAAFFFFFF);
            extraY += 10;
        }
        if (settings.showAvgSpeed) {
            mc.fontRendererObj.drawStringWithShadow("Speed: " + String.format("%.2f", lastResult.error), -textWidth / 2f, extraY, 0xAAFFFFFF);
            extraY += 10;
        }

        if (settings.tickVisualization) {
            int barW = 40;
            int barH = 2;
            drawRect(-barW / 2, (int) extraY, barW / 2, (int) extraY + barH, 0x55FFFFFF);
            int markerPos = (int) ((lastResult.error / 50.0) * (barW / 2f));
            markerPos = Math.max(-barW / 2, Math.min(barW / 2, markerPos));
            drawRect(markerPos - 1, (int) extraY - 1, markerPos + 1, (int) extraY + barH + 1, finalColor);
        }

        if (settings.sprintIndicator && showSprintIcon) {
            String sprintChar = "[S]";
            int sprintW = mc.fontRendererObj.getStringWidth(sprintChar);
            mc.fontRendererObj.drawString(sprintChar, -sprintW / 2, (int)extraY + 5, finalColor);
        }

        GlStateManager.popMatrix();
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
