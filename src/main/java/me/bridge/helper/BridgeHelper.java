package me.bridge.helper;

import me.bridge.helper.config.SettingsManager;
import me.bridge.helper.core.BridgeDetector;
import me.bridge.helper.core.MovementTracker;
import me.bridge.helper.core.TimingAnalyzer;
import me.bridge.helper.ui.ClickGUI;
import me.bridge.helper.ui.FeedbackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "bridgehelper", name = "BridgeHelper", version = "1.0.0", clientSideOnly = true)
public class BridgeHelper {
    private final SettingsManager settings = SettingsManager.getInstance();
    private final MovementTracker movementTracker = new MovementTracker();
    private final BridgeDetector bridgeDetector = new BridgeDetector();
    private final TimingAnalyzer timingAnalyzer = new TimingAnalyzer();
    private final FeedbackRenderer feedbackRenderer = new FeedbackRenderer();

    private static KeyBinding guiKey;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(feedbackRenderer);

        guiKey = new KeyBinding("Open Settings", Keyboard.KEY_M, "BridgeHelper");
        ClientRegistry.registerKeyBinding(guiKey);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().thePlayer != null) {
            movementTracker.update();
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.entityPlayer == Minecraft.getMinecraft().thePlayer) {
            if (!settings.enabled) return;

            if (bridgeDetector.isBridging(event.pos)) {
                long unsneakTime = movementTracker.getLastUnsneakTime();
                long placeTime = System.currentTimeMillis();
                
                if (placeTime - unsneakTime < 1000) {
                    TimingAnalyzer.TimingResult result = timingAnalyzer.analyze(
                            unsneakTime,
                            placeTime,
                            movementTracker.getAvgSpeed(),
                            movementTracker.wasSprintingAtUnsneak()
                    );

                    if (result != null) {
                        feedbackRenderer.postFeedback(result, movementTracker.wasSprintingAtUnsneak());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (guiKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ClickGUI());
        }

        int sneakKeyCode = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
        if (Keyboard.getEventKey() == sneakKeyCode) {
            movementTracker.onSneakKey(Keyboard.getEventKeyState());
        }
    }
}
