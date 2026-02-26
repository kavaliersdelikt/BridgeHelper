# BridgeHelper
**Live timing feedback to help you actually get consistent at speed-bridging.**

Speed-bridging is all about rhythm, and rhythm is really hard to self-diagnose. Most tools just tell you that you fell—not *why*. BridgeHelper shows you your timing while you're mid-bridge, so you can make corrections in the moment instead of guessing after the fact.

It works by reading your raw movement state directly, which means the timing calculations are tied to your actual velocity rather than a fixed formula. You can check the raw millisecond data if you're into that, or just use the plain-English feedback mode if you'd rather keep it simple.

## Why it works
- **Real-time feedback** - placement windows are calculated from your actual velocity as you move, not from a fixed formula
- **Sneak-release detection** - your unsneak is caught as soon as the game state changes
- **Fully customizable HUD** - reposition it, recolor it, add sound cues, whatever fits your setup
- **Faster muscle memory** - instant audio/visual feedback beats silent trial-and-error every time

---

## Three builds — pick your version

| Version | Loader | Minecraft range | Directory |
|---------|--------|-----------------|-----------|
| **Forge 1.8.9** | Forge 11.15.1 | 1.8.9 only | `forge-1.8.9/` |
| **Fabric Legacy** | Fabric Loader ≥ 0.14 | 1.14.4 – 1.19.4 | `fabric-legacy/` |
| **Fabric Modern** | Fabric Loader ≥ 0.15 | 1.20.1 – 1.21.1 | *(repo root)* |

### Key API differences between builds

| Area | Forge 1.8.9 | Fabric Legacy (1.14–1.19.4) | Fabric Modern (1.20–1.21.1) |
|------|-------------|-----------------------------|-----------------------------|
| Entry point | `@Mod` + `FMLInitializationEvent` | `ClientModInitializer` | `ClientModInitializer` |
| Block interact event | `PlayerInteractEvent` (Forge bus) | `UseBlockCallback` (Fabric API) | `UseBlockCallback` (Fabric API) |
| HUD rendering | `RenderGameOverlayEvent.Post` | `HudRenderCallback(MatrixStack, float)` | `HudRenderCallback(DrawContext, RenderTickCounter)` |
| Rect drawing | `Gui.drawRect()` / `GlStateManager` | `DrawableHelper.fill(MatrixStack, …)` | `DrawContext.fill(…)` |
| Text rendering | `fontRendererObj.drawStringWithShadow()` | `textRenderer.drawWithShadow(MatrixStack, …)` | `DrawContext.drawText(…)` |
| GUI screen | `GuiScreen` + `drawScreen()` | `Screen` + `render(MatrixStack, …)` | `Screen` + `render(DrawContext, …)` |
| Sound | `player.playSound(String, …)` | `SoundEvents.UI_BUTTON_CLICK` (SoundEvent) | `SoundEvents.UI_BUTTON_CLICK.value()` (RegistryEntry) |
| Player ref | `mc.thePlayer` | `mc.player` | `mc.player` |
| Movement input | `player.movementInput.moveForward` | `player.input.pressingBack` | `player.input.pressingBack` |
| Config path | `mcDataDir/config/` | `FabricLoader.getConfigDir()` | `FabricLoader.getConfigDir()` |

---

## Building

### Forge 1.8.9

Requires **Gradle 4.4.1** (incompatible with newer Gradle due to ForgeGradle 2.1).

```bash
cd forge-1.8.9

# Generate the Gradle wrapper for this subproject (one-time step)
gradle wrapper --gradle-version 4.4.1

./gradlew build
# Output: forge-1.8.9/build/libs/BridgeHelper-1.0.0.jar
```

### Fabric Legacy (1.14.4 – 1.19.4)

Requires Java 17+. Uses Gradle 8.3 via the included wrapper config.

```bash
cd fabric-legacy

# Copy the wrapper jar from the root (one-time step) then generate the script:
mkdir -p gradle/wrapper
cp ../gradle/wrapper/gradle-wrapper.jar gradle/wrapper/
gradle wrapper --gradle-version 8.3   # or copy gradlew from root

./gradlew build
# Output: fabric-legacy/build/libs/BridgeHelper-1.0.0+1.19.4.jar
```

### Fabric Modern (1.20.1 – 1.21.1)

Requires Java 21+. Uses Gradle 8.8 via the root wrapper.

```bash
# From the repository root:
./gradlew build
# Output: build/libs/BridgeHelper-1.0.0+1.21.1.jar
```

---

## Installation

### Forge 1.8.9
1. Install **Minecraft Forge 1.8.9**
2. Drop the built JAR into `.minecraft/mods/`

### Fabric (both legacy and modern)
1. Install **Fabric Loader** for your Minecraft version
2. Install **Fabric API** (required dependency)
3. Drop the built JAR into `.minecraft/mods/`

---

## How to use
- Hit `M` (rebindable in Controls) to open the settings menu
- You'll see a **DRAG ME** box when the menu is open — drag it to position your HUD
- Start bridging. The mod detects it automatically and shows your timing near the crosshair
- If the HUD gets lost, hit **Reset Position** in the UI settings tab

## License & Contributing
Licensed under MIT. PRs and bug reports are always welcome — open an issue or submit a pull request.

