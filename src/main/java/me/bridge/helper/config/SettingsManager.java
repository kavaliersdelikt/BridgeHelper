package me.bridge.helper.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(Minecraft.getMinecraft().mcDataDir, "config/BridgeHelper.json");
    private static SettingsManager instance;

    // Config version for migration support. Bump when defaults change.
    public int configVersion = 2;

    public boolean enabled = true;
    public int feedbackDuration = 800;
    public float feedbackScale = 1.0f;
    public boolean animationsEnabled = true;
    public boolean soundEnabled = false;
    public float soundVolume = 0.5f;
    public boolean userFriendlyMode = true;

    public int perfectWindow = 15;
    public int earlyTolerance = 25;
    public int lateTolerance = 25;
    public float idealEdgeDistance = 0.80f;
    public float safeMargin = 0.18f;
    public boolean diagonalAdjustment = true;
    public boolean movementCompensation = true;
    public boolean sprintToleranceAdjustment = true;

    public int accentColor = 0xFF55FF55;
    public int textColor = 0xFFFFFFFF;
    public boolean roundedCorners = true;
    public boolean shadowEnabled = true;
    public float uiScale = 1.0f;
    public boolean sprintIndicator = true;
    public float posX = 0.5f;
    public float posY = 0.6f;

    public boolean showRawDeltaTime = false;
    public boolean showIdealMs = false;
    public boolean showAvgSpeed = false;
    public boolean debugMode = false;
    public boolean tickVisualization = false;
    public boolean sprintStabilityRequirement = true;

    public void resetToDefaults() {
        this.enabled = true;
        this.feedbackDuration = 800;
        this.feedbackScale = 1.0f;
        this.animationsEnabled = true;
        this.soundEnabled = false;
        this.soundVolume = 0.5f;
        this.userFriendlyMode = true;
        this.perfectWindow = 15;
        this.earlyTolerance = 25;
        this.lateTolerance = 25;
        this.idealEdgeDistance = 0.80f;
        this.safeMargin = 0.18f;
        this.diagonalAdjustment = true;
        this.movementCompensation = true;
        this.sprintToleranceAdjustment = true;
        this.accentColor = 0xFF55FF55;
        this.textColor = 0xFFFFFFFF;
        this.roundedCorners = true;
        this.shadowEnabled = true;
        this.uiScale = 1.0f;
        this.sprintIndicator = true;
        this.posX = 0.5f;
        this.posY = 0.6f;
        this.showRawDeltaTime = false;
        this.showIdealMs = false;
        this.showAvgSpeed = false;
        this.debugMode = false;
        this.tickVisualization = false;
        this.sprintStabilityRequirement = true;
        this.configVersion = 2;
        save();
    }

    public void applyPreset(Preset preset) {
        switch (preset) {
            case DEFAULT:
                resetToDefaults();
                break;
            case SENSITIVE:
                resetToDefaults();
                this.perfectWindow = 8;
                this.earlyTolerance = 15;
                this.lateTolerance = 15;
                this.safeMargin = 0.12f;
                this.soundEnabled = true;
                break;
            case CASUAL:
                resetToDefaults();
                this.perfectWindow = 25;
                this.earlyTolerance = 40;
                this.lateTolerance = 40;
                this.feedbackDuration = 1200;
                this.userFriendlyMode = true;
                break;
        }
        save();
    }

    public enum Preset {
        DEFAULT, SENSITIVE, CASUAL
    }

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
            instance.load();
        }
        return instance;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            SettingsManager loaded = GSON.fromJson(reader, SettingsManager.class);
            if (loaded != null) {
                this.enabled = loaded.enabled;
                this.feedbackDuration = loaded.feedbackDuration;
                this.feedbackScale = loaded.feedbackScale;
                this.animationsEnabled = loaded.animationsEnabled;
                this.soundEnabled = loaded.soundEnabled;
                this.soundVolume = loaded.soundVolume;
                this.userFriendlyMode = loaded.userFriendlyMode;
                this.perfectWindow = loaded.perfectWindow;
                this.earlyTolerance = loaded.earlyTolerance;
                this.lateTolerance = loaded.lateTolerance;
                this.idealEdgeDistance = loaded.idealEdgeDistance;
                this.safeMargin = loaded.safeMargin;
                this.diagonalAdjustment = loaded.diagonalAdjustment;
                this.movementCompensation = loaded.movementCompensation;
                this.sprintToleranceAdjustment = loaded.sprintToleranceAdjustment;
                this.accentColor = loaded.accentColor;
                this.textColor = loaded.textColor;
                this.roundedCorners = loaded.roundedCorners;
                this.shadowEnabled = loaded.shadowEnabled;
                this.uiScale = loaded.uiScale;
                this.sprintIndicator = loaded.sprintIndicator;
                this.posX = loaded.posX;
                this.posY = loaded.posY;
                this.showRawDeltaTime = loaded.showRawDeltaTime;
                this.showIdealMs = loaded.showIdealMs;
                this.showAvgSpeed = loaded.showAvgSpeed;
                this.debugMode = loaded.debugMode;
                this.tickVisualization = loaded.tickVisualization;
                this.sprintStabilityRequirement = loaded.sprintStabilityRequirement;

                // Migrate from v1 configs (no configVersion field defaults to 0)
                if (loaded.configVersion < 2) {
                    this.uiScale = 1.0f;
                    this.configVersion = 2;
                    save();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
