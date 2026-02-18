# BridgeHelper
**Live timing feedback to help you actually get consistent at speed-bridging.**

Speed-bridging is all about rhythm, and rhythm is really hard to self-diagnose. Most tools just tell you that you fell—not *why*. BridgeHelper shows you your timing while you're mid-bridge, so you can make corrections in the moment instead of guessing after the fact.

It works by reading your raw keyboard input directly, which means the timing calculations are tied to your actual movement rather than the game's tick cycle. You can check the raw millisecond data if you're into that, or just use the plain-English feedback mode if you'd rather keep it simple. Either way, the goal is the same: help you find a consistent rhythm and actually keep it.

## Why it works
- **Real-time feedback** - placement windows are calculated from your actual velocity as you move, not from a fixed formula
- **Sub-tick detection** - your unsneak gets caught the moment it happens, before the next game tick is even processed
- **Fully customizable HUD** - reposition it, recolor it, add sound cues, whatever fits your setup
- **Faster muscle memory** - instant audio/visual feedback beats silent trial-and-error every time

## Installation
1. Make sure you have **Minecraft Forge 1.8.9** installed
2. Grab the latest `BridgeHelper-x.x.x.jar` from the [Releases](https://github.com/kavaliersdelikt/bridgehelper/releases) page
3. Drop the JAR into your `.minecraft/mods` folder
4. Launch and you're good

## How to use
- Hit `M` (rebindable) to open the settings menu
- You'll see a "DRAG ME" box when the menu is open — drag it to move your HUD wherever you want
- Start bridging. The mod detects it automatically and shows your timing feedback near your crosshair
- If your HUD goes missing or gets messed up, there's a "Reset Position" button in the UI tab

## License & Contributing
Licensed under MIT. PRs and bug reports are always welcome - just open an issue or submit a pull request on GitHub.
