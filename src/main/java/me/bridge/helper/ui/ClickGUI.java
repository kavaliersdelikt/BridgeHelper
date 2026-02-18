package me.bridge.helper.ui;

import me.bridge.helper.config.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {
    private final SettingsManager settings = SettingsManager.getInstance();
    private final List<Component> components = new ArrayList<>();
    private int startX = 50;
    private int startY = 50;
    private int width = 350;
    private int height = 250;
    private Category currentCategory = Category.FEEDBACK;

    @Override
    public void initGui() {
        components.clear();
        int compX = startX + 110;
        int compY = startY + 40;

        switch (currentCategory) {
            case FEEDBACK:
                components.add(new Toggle("Enabled", compX, compY, () -> settings.enabled, (v) -> settings.enabled = v));
                components.add(new Toggle("Friendly Feedback", compX, compY + 25, () -> settings.userFriendlyMode, (v) -> settings.userFriendlyMode = v));
                components.add(new Slider("Duration", compX, compY + 50, 100, 1500, (float) settings.feedbackDuration, (v) -> settings.feedbackDuration = v.intValue()));
                components.add(new Slider("Scale", compX, compY + 75, 0.5f, 3.0f, settings.feedbackScale, (v) -> settings.feedbackScale = v));
                components.add(new Toggle("Animations", compX, compY + 100, () -> settings.animationsEnabled, (v) -> settings.animationsEnabled = v));
                components.add(new Toggle("Sound", compX, compY + 125, () -> settings.soundEnabled, (v) -> settings.soundEnabled = v));
                components.add(new Slider("Volume", compX, compY + 150, 0.0f, 1.0f, settings.soundVolume, (v) -> settings.soundVolume = v));
                break;
            case TIMING:
                components.add(new Slider("Perfect Window", compX, compY, 5, 40, (float) settings.perfectWindow, (v) -> settings.perfectWindow = v.intValue()));
                components.add(new Slider("Early Tolerance", compX, compY + 25, 5, 100, (float) settings.earlyTolerance, (v) -> settings.earlyTolerance = v.intValue()));
                components.add(new Slider("Late Tolerance", compX, compY + 50, 5, 100, (float) settings.lateTolerance, (v) -> settings.lateTolerance = v.intValue()));
                components.add(new Slider("Ideal Edge Dist", compX, compY + 75, 0.6f, 1.0f, settings.idealEdgeDistance, (v) -> settings.idealEdgeDistance = v));
                components.add(new Slider("Safe Margin", compX, compY + 100, 0.0f, 0.5f, settings.safeMargin, (v) -> settings.safeMargin = v));
                components.add(new Toggle("Diagonal Adj", compX, compY + 125, () -> settings.diagonalAdjustment, (v) -> settings.diagonalAdjustment = v));
                components.add(new Toggle("Move Compensation", compX, compY + 150, () -> settings.movementCompensation, (v) -> settings.movementCompensation = v));
                components.add(new Toggle("Sprint Adjustment", compX, compY + 175, () -> settings.sprintToleranceAdjustment, (v) -> settings.sprintToleranceAdjustment = v));
                break;
            case UI:
                components.add(new Toggle("Rounded Corners", compX, compY, () -> settings.roundedCorners, (v) -> settings.roundedCorners = v));
                components.add(new Toggle("Shadow", compX, compY + 25, () -> settings.shadowEnabled, (v) -> settings.shadowEnabled = v));
                components.add(new Slider("UI Scale", compX, compY + 50, 0.5f, 2.0f, settings.uiScale, (v) -> settings.uiScale = v));
                components.add(new Toggle("Sprint Indicator", compX, compY + 75, () -> settings.sprintIndicator, (v) -> settings.sprintIndicator = v));
                components.add(new Slider("Accent Hue", compX, compY + 100, 0f, 1f, getHue(settings.accentColor), (v) -> settings.accentColor = Color.HSBtoRGB(v, 0.8f, 1f)));
                components.add(new Slider("Pos X", compX, compY + 125, 0.0f, 1.0f, settings.posX, (v) -> settings.posX = v));
                components.add(new Slider("Pos Y", compX, compY + 150, 0.0f, 1.0f, settings.posY, (v) -> settings.posY = v));
                components.add(new Button("Reset Position", compX, compY + 175, () -> {
                    settings.posX = 0.5f;
                    settings.posY = 0.6f;
                }));
                break;
            case ADVANCED:
                components.add(new Toggle("Raw Delta", compX, compY, () -> settings.showRawDeltaTime, (v) -> settings.showRawDeltaTime = v));
                components.add(new Toggle("Ideal MS", compX, compY + 25, () -> settings.showIdealMs, (v) -> settings.showIdealMs = v));
                components.add(new Toggle("Avg Speed", compX, compY + 50, () -> settings.showAvgSpeed, (v) -> settings.showAvgSpeed = v));
                components.add(new Toggle("Debug", compX, compY + 75, () -> settings.debugMode, (v) -> settings.debugMode = v));
                components.add(new Toggle("Tick Vis", compX, compY + 100, () -> settings.tickVisualization, (v) -> settings.tickVisualization = v));
                components.add(new Toggle("Sprint Stability", compX, compY + 125, () -> settings.sprintStabilityRequirement, (v) -> settings.sprintStabilityRequirement = v));
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        net.minecraft.client.gui.Gui.drawRect(startX, startY, startX + width, startY + height, 0xDD111111);
        net.minecraft.client.gui.Gui.drawRect(startX, startY, startX + 100, startY + height, 0xDD222222);
        mc.fontRendererObj.drawStringWithShadow("BridgeHelper", startX + 10, startY + 10, settings.accentColor);
        int catY = startY + 40;
        for (Category cat : Category.values()) {
            int color = (cat == currentCategory) ? settings.accentColor : 0xFFFFFFFF;
            mc.fontRendererObj.drawStringWithShadow(cat.name(), startX + 10, catY, color);
            catY += 20;
        }
        for (Component comp : components) {
            comp.draw(mouseX, mouseY);
        }
        ScaledResolution sr = new ScaledResolution(mc);
        int previewX = (int) (sr.getScaledWidth() * settings.posX);
        int previewY = (int) (sr.getScaledHeight() * settings.posY);
        net.minecraft.client.gui.Gui.drawRect(previewX - 20, previewY - 10, previewX + 20, previewY + 10, 0xAAFF5555);
        mc.fontRendererObj.drawStringWithShadow("DRAG ME", previewX - 18, previewY - 4, 0xFFFFFFFF);
        if (draggingPreview) {
            settings.posX = (float) mouseX / sr.getScaledWidth();
            settings.posY = (float) mouseY / sr.getScaledHeight();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private boolean draggingPreview = false;

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int catY = startY + 40;
        for (Category cat : Category.values()) {
            if (mouseX >= startX + 10 && mouseX <= startX + 90 && mouseY >= catY && mouseY <= catY + 15) {
                currentCategory = cat;
                initGui();
                return;
            }
            catY += 20;
        }
        ScaledResolution sr = new ScaledResolution(mc);
        int previewX = (int) (sr.getScaledWidth() * settings.posX);
        int previewY = (int) (sr.getScaledHeight() * settings.posY);
        if (mouseX >= previewX - 20 && mouseX <= previewX + 20 && mouseY >= previewY - 10 && mouseY <= previewY + 10) {
            draggingPreview = true;
            return;
        }
        for (Component comp : components) {
            comp.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggingPreview = false;
        for (Component comp : components) {
            comp.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void onGuiClosed() {
        settings.save();
    }

    private enum Category {
        FEEDBACK, TIMING, UI, ADVANCED
    }

    private abstract static class Component {
        protected String name;
        protected int x, y;
        public Component(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
        public abstract void draw(int mouseX, int mouseY);
        public abstract void mouseClicked(int mouseX, int mouseY, int button);
        public void mouseReleased(int mouseX, int mouseY, int button) {}
    }

    private static class Toggle extends Component {
        private final Getter<Boolean> getter;
        private final Setter<Boolean> setter;
        public Toggle(String name, int x, int y, Getter<Boolean> getter, Setter<Boolean> setter) {
            super(name, x, y);
            this.getter = getter;
            this.setter = setter;
        }
        @Override
        public void draw(int mouseX, int mouseY) {
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(name, x, y, 0xFFFFFFFF);
            int toggleX = x + 150;
            net.minecraft.client.gui.Gui.drawRect(toggleX, y, toggleX + 10, y + 10, getter.get() ? SettingsManager.getInstance().accentColor : 0xFF555555);
        }
        @Override
        public void mouseClicked(int mouseX, int mouseY, int button) {
            if (mouseX >= x + 150 && mouseX <= x + 160 && mouseY >= y && mouseY <= y + 10) {
                setter.set(!getter.get());
            }
        }
    }

    private static class Slider extends Component {
        private final float min, max;
        private float value;
        private final Setter<Float> setter;
        private boolean dragging = false;
        public Slider(String name, int x, int y, float min, float max, float value, Setter<Float> setter) {
            super(name, x, y);
            this.min = min;
            this.max = max;
            this.value = value;
            this.setter = setter;
        }
        @Override
        public void draw(int mouseX, int mouseY) {
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(name + ": " + String.format("%.2f", value), x, y, 0xFFFFFFFF);
            int sliderX = x + 150;
            int sliderWidth = 80;
            net.minecraft.client.gui.Gui.drawRect(sliderX, y + 4, sliderX + sliderWidth, y + 6, 0xFF555555);
            float pos = (value - min) / (max - min);
            int knobX = sliderX + (int) (pos * sliderWidth);
            net.minecraft.client.gui.Gui.drawRect(knobX - 2, y, knobX + 2, y + 10, SettingsManager.getInstance().accentColor);
            if (dragging) {
                float newPos = (float) (mouseX - sliderX) / (float) sliderWidth;
                newPos = Math.max(0, Math.min(1, newPos));
                value = min + newPos * (max - min);
                setter.set(value);
            }
        }
        @Override
        public void mouseClicked(int mouseX, int mouseY, int button) {
            int sliderX = x + 150;
            int sliderWidth = 80;
            if (mouseX >= sliderX && mouseX <= sliderX + sliderWidth && mouseY >= y && mouseY <= y + 10) {
                dragging = true;
            }
        }
        @Override
        public void mouseReleased(int mouseX, int mouseY, int button) {
            dragging = false;
        }
    }

    private static class Button extends Component {
        private final Runnable action;
        public Button(String name, int x, int y, Runnable action) {
            super(name, x, y);
            this.action = action;
        }
        @Override
        public void draw(int mouseX, int mouseY) {
            int width = 100;
            int height = 15;
            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            net.minecraft.client.gui.Gui.drawRect(x, y, x + width, y + height, hovered ? 0xFF666666 : 0xFF444444);
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(name, x + 5, y + 4, 0xFFFFFFFF);
        }
        @Override
        public void mouseClicked(int mouseX, int mouseY, int button) {
            int width = 100;
            int height = 15;
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                action.run();
            }
        }
    }

    private float getHue(int color) {
        float[] hsb = new float[3];
        Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, hsb);
        return hsb[0];
    }

    private interface Getter<T> { T get(); }
    private interface Setter<T> { void set(T value); }
}
