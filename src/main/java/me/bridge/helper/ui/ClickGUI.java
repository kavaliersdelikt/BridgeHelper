package me.bridge.helper.ui;

import me.bridge.helper.config.SettingsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Screen {
    private final SettingsManager settings = SettingsManager.getInstance();
    private final List<Component> components = new ArrayList<>();
    private final int guiX = 50;
    private final int guiY = 50;
    private final int guiWidth = 350;
    private final int guiHeight = 250;
    private Category currentCategory = Category.FEEDBACK;
    private boolean draggingPreview = false;

    public ClickGUI() {
        super(Text.literal("BridgeHelper"));
    }

    @Override
    public void init() {
        components.clear();
        int compX = guiX + 110;
        int compY = guiY + 40;

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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000);
        context.fill(guiX, guiY, guiX + guiWidth, guiY + guiHeight, 0xDD111111);
        context.fill(guiX, guiY, guiX + 100, guiY + guiHeight, 0xDD222222);
        context.drawText(this.textRenderer, "BridgeHelper", guiX + 10, guiY + 10, settings.accentColor, true);

        int catY = guiY + 40;
        for (Category cat : Category.values()) {
            int color = (cat == currentCategory) ? settings.accentColor : 0xFFFFFFFF;
            context.drawText(this.textRenderer, cat.name(), guiX + 10, catY, color, true);
            catY += 20;
        }

        for (Component comp : components) {
            comp.draw(context, mouseX, mouseY);
        }

        int previewX = (int) (this.width * settings.posX);
        int previewY = (int) (this.height * settings.posY);
        context.fill(previewX - 20, previewY - 10, previewX + 20, previewY + 10, 0xAAFF5555);
        context.drawText(this.textRenderer, "DRAG ME", previewX - 18, previewY - 4, 0xFFFFFFFF, true);
        if (draggingPreview) {
            settings.posX = (float) mouseX / this.width;
            settings.posY = (float) mouseY / this.height;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX;
        int my = (int) mouseY;

        int catY = guiY + 40;
        for (Category cat : Category.values()) {
            if (mx >= guiX + 10 && mx <= guiX + 90 && my >= catY && my <= catY + 15) {
                currentCategory = cat;
                this.init();
                return true;
            }
            catY += 20;
        }

        int previewX = (int) (this.width * settings.posX);
        int previewY = (int) (this.height * settings.posY);
        if (mx >= previewX - 20 && mx <= previewX + 20 && my >= previewY - 10 && my <= previewY + 10) {
            draggingPreview = true;
            return true;
        }

        for (Component comp : components) {
            comp.mouseClicked(mx, my, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingPreview = false;
        for (Component comp : components) {
            comp.mouseReleased((int) mouseX, (int) mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void removed() {
        settings.save();
    }

    @Override
    public boolean shouldPause() {
        return false;
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
        public abstract void draw(DrawContext context, int mouseX, int mouseY);
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
        public void draw(DrawContext context, int mouseX, int mouseY) {
            context.drawText(MinecraftClient.getInstance().textRenderer, name, x, y, 0xFFFFFFFF, false);
            int toggleX = x + 150;
            context.fill(toggleX, y, toggleX + 10, y + 10,
                    getter.get() ? SettingsManager.getInstance().accentColor : 0xFF555555);
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
        public void draw(DrawContext context, int mouseX, int mouseY) {
            context.drawText(MinecraftClient.getInstance().textRenderer,
                    name + ": " + String.format("%.2f", value), x, y, 0xFFFFFFFF, false);
            int sliderX = x + 150;
            int sliderWidth = 80;
            context.fill(sliderX, y + 4, sliderX + sliderWidth, y + 6, 0xFF555555);
            float pos = (value - min) / (max - min);
            int knobX = sliderX + (int) (pos * sliderWidth);
            context.fill(knobX - 2, y, knobX + 2, y + 10, SettingsManager.getInstance().accentColor);
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
        public void draw(DrawContext context, int mouseX, int mouseY) {
            int w = 100;
            int h = 15;
            boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
            context.fill(x, y, x + w, y + h, hovered ? 0xFF666666 : 0xFF444444);
            context.drawText(MinecraftClient.getInstance().textRenderer, name, x + 5, y + 4, 0xFFFFFFFF, false);
        }
        @Override
        public void mouseClicked(int mouseX, int mouseY, int button) {
            int w = 100;
            int h = 15;
            if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
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
