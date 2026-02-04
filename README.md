# CS3560 WSS (Wilderness Survival) Game

WSS is a console-based survival game built for the CS3560 Object-Oriented Design & Analysis course at Cal Poly Pomona. The player navigates a procedurally generated wilderness map, balancing movement costs, limited supplies, and trading opportunities while pursuing the win condition of reaching the eastern edge of the map. The codebase emphasizes strategy patterns, configurable behaviors, and modular game systems written in Java.【F:src/main/java/wss/controller/WSS.java†L1-L120】

## Key Gameplay Features

- **Dynamic map generation** with difficulty-based terrain distribution, bonus items, and random traders.【F:src/main/java/wss/world/Map.java†L1-L55】【F:src/main/java/wss/world/terrain/TerrainDistribution.java†L1-L110】
- **Resource management** (strength, food, water, gold) with terrain costs, pickups, and end-of-turn drain.【F:src/main/java/wss/actor/Player.java†L61-L224】
- **AI brain strategies** that determine movement decisions, including an adaptive strategy selector.【F:src/main/java/wss/actor/brain/AdaptiveBrain.java†L1-L88】
- **Vision systems** that limit which nearby squares the player can consider when searching for resources.【F:src/main/java/wss/actor/vision/Vision.java†L1-L55】
- **Trading system** with trader personalities and reward strategies.【F:src/main/java/wss/economy/Trader.java†L1-L58】【F:src/main/java/wss/economy/TradeStrategyFactory.java†L1-L12】

## Project Structure

```
src/main/java/wss
├─ actor
│  ├─ Player.java
│  ├─ brain/          # Movement strategy implementations
│  └─ vision/         # Vision rules for scanning nearby squares
├─ controller
│  ├─ WSS.java        # Game entry point and main loop
│  └─ GameParameters.java
├─ economy            # Trading models and strategies
├─ util               # Shared enums (Direction, DifficultyLevel)
└─ world
   ├─ Map.java
   ├─ Square.java
   ├─ item/           # Resource pickup items
   └─ terrain/        # Terrain types and distribution rules
```

## Getting Started

There is no build tool committed to the repository, so the simplest way to run the game is directly with `javac` and `java`:

```bash
mkdir -p out
javac -d out $(find src/main/java -name "*.java")
java -cp out wss.controller.WSS
```

The game will prompt you for:

1. Map width and height.
2. Difficulty level (`EASY`, `MEDIUM`, `HARD`).【F:src/main/java/wss/controller/WSS.java†L16-L36】
3. Vision type (full, cautious, diagonal, random, or extended).【F:src/main/java/wss/controller/WSS.java†L39-L59】
4. Brain type (movement strategy).【F:src/main/java/wss/controller/WSS.java†L62-L80】

## How the Game Works (High-Level)

1. **Setup**: The game creates a map and a player with your selected brain and vision settings.【F:src/main/java/wss/controller/WSS.java†L82-L103】
2. **Turn loop**: Each turn, the player chooses a movement direction, spends resources based on terrain, picks up items, and optionally trades with a nearby trader.【F:src/main/java/wss/actor/Player.java†L61-L224】
3. **Win/Loss**: The default win condition is reaching the eastern edge of the map; you lose if you run out of strength, water, or food.【F:src/main/java/wss/controller/GameParameters.java†L14-L52】

## Configuration Notes

Some parameters (such as max turns and adaptive brain thresholds) are read from a `wss.config.GameConfig` utility, but that class is not present in the repository yet. The code safely falls back to defaults when configuration values are missing (e.g., 200 max turns).【F:src/main/java/wss/controller/GameParameters.java†L14-L22】【F:src/main/java/wss/actor/brain/BrainSelectionConfig.java†L1-L33】

## Architecture Documentation

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for a detailed breakdown of the current architecture, including module responsibilities, class relationships, and gameplay flow.
