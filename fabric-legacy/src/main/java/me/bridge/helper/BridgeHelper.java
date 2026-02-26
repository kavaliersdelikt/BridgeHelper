package me.bridge.helper;

import me.bridge.helper.config.SettingsManager;
import me.bridge.helper.core.BridgeDetector;
import me.bridge.helper.core.MovementTracker;
import me.bridge.helper.core.TimingAnalyzer;
import me.bridge.helper.ui.ClickGUI;
import me.bridge.helper.ui.FeedbackRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.lwjgl.glfw.GLFW;

public class BridgeHelper implements ClientModInitializer {
    private final SettingsManager settings = SettingsManager.getInstance();
    private final MovementTracker movementTracker = new MovementTracker();
    private final BridgeDetector bridgeDetector = new BridgeDetector();
    private final TimingAnalyzer timingAnalyzer = new TimingAnalyzer();
    private final FeedbackRenderer feedbackRenderer = new FeedbackRenderer();

    private static KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bridgehelper.open_settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.bridgehelper"));

        feedbackRenderer.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null)
                return;
            movementTracker.update();
            if (guiKey.wasPressed()) {
                client.setScreen(new ClickGUI());
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient())
                return ActionResult.PASS;
            if (hand != Hand.MAIN_HAND)
                return ActionResult.PASS;
            BlockHitResult blockHit = hitResult;
            if (player != MinecraftClient.getInstance().player)
                return ActionResult.PASS;
            if (!settings.enabled)
                return ActionResult.PASS;

            if (bridgeDetector.isBridging(blockHit.getBlockPos())) {
                long unsneakTime = movementTracker.getLastUnsneakTime();
                long placeTime = System.currentTimeMillis();

                if (placeTime - unsneakTime < 1000) {
                    TimingAnalyzer.TimingResult result = timingAnalyzer.analyze(
                            unsneakTime,
                            placeTime,
                            movementTracker.getAvgSpeed(),
                            movementTracker.wasSprintingAtUnsneak());

                    if (result != null) {
                        feedbackRenderer.postFeedback(result, movementTracker.wasSprintingAtUnsneak());
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
