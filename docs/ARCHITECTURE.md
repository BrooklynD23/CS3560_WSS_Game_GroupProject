# Architecture Overview

This document describes the current architecture of the Wilderness Survival (WSS) game at this git revision. The system is a console-based Java application organized around a few core domains: **control flow**, **player/AI**, **world generation**, **economy**, and **shared utilities**.

---

## High-Level System Diagram

```
            +----------------------+
            |  wss.controller.WSS  |
            |  (main entry point)  |
            +----------+-----------+
                       |
                       v
              +--------+--------+
              | GameParameters  |
              +--------+--------+
                       |
                       v
            +----------+-----------+
            |       Player         |
            +----+----------+------+
                 |          |
                 v          v
            Brain strategy  Vision strategy
                 |          |
                 v          v
               Direction    Visible squares
                 |
                 v
              World Map  <---- Terrain, Items, Traders
```

---

## Core Runtime Flow

### 1. Application Entry and Setup

- `WSS.main` prompts for map size, difficulty, vision type, and brain type, then creates the `Map` and `Player`. It also initializes exploration data and scales starting supplies based on map size.【F:src/main/java/wss/controller/WSS.java†L12-L103】【F:src/main/java/wss/actor/Player.java†L30-L58】
- `GameParameters` supplies turn limits and win/loss conditions (default win condition: reach the eastern edge of the map).【F:src/main/java/wss/controller/GameParameters.java†L12-L52】

### 2. Turn Loop

Each turn in `WSS`:

1. The player chooses a direction using the currently configured brain strategy.
2. The player moves (if allowed), pays terrain costs, collects items, and may trade.
3. A daily resource drain is applied.
4. Win/loss conditions are evaluated.
5. If the player is not using `AdaptiveBrain`, the system may recommend another strategy based on their current state.【F:src/main/java/wss/controller/WSS.java†L103-L156】【F:src/main/java/wss/actor/Player.java†L61-L224】

---

## Module-Level Architecture

### `wss.controller`

**Primary responsibility:** Owns the game loop and runtime configuration.

- `WSS`: Console entry point, prompts user inputs, constructs the map and player, and executes the turn loop with win/loss checks.【F:src/main/java/wss/controller/WSS.java†L12-L160】
- `GameParameters`: Holds max turns, win condition predicates, and the loss condition (out of resources). It also reads optional configuration from `GameConfig` with defaults if missing.【F:src/main/java/wss/controller/GameParameters.java†L7-L52】

### `wss.actor`

**Primary responsibility:** Player state and core turn logic.

- `Player`: Stores position, supplies, AI brain and vision settings, and encapsulates a full turn (move, trade, drain, report). Movement cost and resource drain rules are enforced here.【F:src/main/java/wss/actor/Player.java†L14-L224】

### `wss.actor.brain`

**Primary responsibility:** Movement strategies (Strategy Pattern).

- `Brain` is an abstract strategy with `makeMove(Player, Map)` returning a `Direction`.【F:src/main/java/wss/actor/brain/Brain.java†L1-L8】
- Concrete strategies:
  - `ConservativeBrain`: prioritize nearby food, otherwise head east.【F:src/main/java/wss/actor/brain/ConservativeBrain.java†L1-L15】
  - `ResourceOptimizingBrain`: prioritize food/water when low, otherwise head east.【F:src/main/java/wss/actor/brain/ResourceOptimizingBrain.java†L1-L20】
  - `GreedyBrain`: seek gold, then water/food if low.【F:src/main/java/wss/actor/brain/GreedyBrain.java†L1-L28】
  - `RiskTakingBrain`: generally pushes east, with a small chance to detour for food when starving.【F:src/main/java/wss/actor/brain/RiskTakingBrain.java†L1-L19】
  - `SurvivorBrain`: avoids harsh terrain (desert, mountain) when possible.【F:src/main/java/wss/actor/brain/SurvivorBrain.java†L1-L28】
  - `AdaptiveBrain`: dynamically selects another strategy based on the player's current state and distance to the goal. Thresholds are loaded from `BrainSelectionConfig` with defaults if missing.【F:src/main/java/wss/actor/brain/AdaptiveBrain.java†L1-L88】【F:src/main/java/wss/actor/brain/BrainSelectionConfig.java†L1-L53】

### `wss.actor.vision`

**Primary responsibility:** Constrains what the AI can observe (another strategy family).

- `Vision` defines a `scan()` method to return visible squares, then provides higher-level helpers to find nearby food, water, or gold by looking at items within those squares.【F:src/main/java/wss/actor/vision/Vision.java†L1-L55】
- Implementations:
  - `FullVision`: sees all 8 adjacent squares.【F:src/main/java/wss/actor/vision/FullVision.java†L1-L30】
  - `CautiousVision`: sees only cardinal directions (N/S/E/W).【F:src/main/java/wss/actor/vision/CautiousVision.java†L1-L29】
  - `DiagonalVision`: sees only diagonal squares.【F:src/main/java/wss/actor/vision/DiagonalVision.java†L1-L29】
  - `RandomVision`: scans a random subset of directions each turn.【F:src/main/java/wss/actor/vision/RandomVision.java†L1-L35】
  - `ConfigurableVision` + `VisionConfig`: lets you specify ranges and allowed directions (used for the "EXTENDED" vision option).【F:src/main/java/wss/actor/vision/ConfigurableVision.java†L1-L46】【F:src/main/java/wss/actor/vision/VisionConfig.java†L1-L48】

### `wss.world`

**Primary responsibility:** The map, tiles, items, and terrain model.

- `Map`: 2D grid of `Square` objects, generated at construction time. The generation process assigns terrain, optional bonus items, and optional traders.【F:src/main/java/wss/world/Map.java†L1-L55】
- `Square`: container for terrain, items, and an optional trader.【F:src/main/java/wss/world/Square.java†L1-L22】

#### Terrain System

- `Terrain` is the abstract base class defining movement and resource costs.【F:src/main/java/wss/world/terrain/Terrain.java†L1-L22】
- `TerrainDistribution` generates terrains based on difficulty-provided probabilities, then instantiates a random terrain class per square.【F:src/main/java/wss/world/terrain/TerrainDistribution.java†L1-L103】
- Concrete terrain types: `PlainsTerrain`, `DesertTerrain`, `MountainTerrain`, `SwampTerrain`, `SnowTerrain`. Each provides different movement/food/water costs.【F:src/main/java/wss/world/terrain/PlainsTerrain.java†L1-L5】【F:src/main/java/wss/world/terrain/DesertTerrain.java†L1-L5】【F:src/main/java/wss/world/terrain/MountainTerrain.java†L1-L5】【F:src/main/java/wss/world/terrain/SwampTerrain.java†L1-L6】【F:src/main/java/wss/world/terrain/SnowTerrain.java†L1-L6】

#### Item System

- `Item` is the abstract base for map pickups; items apply their effects to the player via `applyTo(Player)`. It also includes convenience checks for item type (food, water, gold).【F:src/main/java/wss/world/item/Item.java†L1-L15】
- Concrete items: `FoodBonus`, `WaterBonus`, `GoldBonus` apply resource gains when collected.【F:src/main/java/wss/world/item/FoodBonus.java†L1-L10】【F:src/main/java/wss/world/item/WaterBonus.java†L1-L16】【F:src/main/java/wss/world/item/GoldBonus.java†L1-L16】

### `wss.economy`

**Primary responsibility:** Trading behaviors and negotiation logic.

- `Trader` encapsulates the negotiation process, including patience and reward calculation based on the attached `TradeStrategy`.【F:src/main/java/wss/economy/Trader.java†L1-L58】
- `TradeStrategy` defines how an offer is evaluated, patience, and reward conversion. Concrete strategies include:
  - `FriendlyTradeStrategy` (high patience, better rewards).【F:src/main/java/wss/economy/FriendlyTradeStrategy.java†L1-L35】
  - `NeutralTradeStrategy` (balanced defaults).【F:src/main/java/wss/economy/NeutralTradeStrategy.java†L1-L35】
  - `GrumpyTradeStrategy` (low patience, modest rewards).【F:src/main/java/wss/economy/GrumpyTradeStrategy.java†L1-L35】
- `TradeStrategyFactory` assigns a strategy based on `TraderType` (`FRIENDLY`, `NEUTRAL`, `GRUMPY`).【F:src/main/java/wss/economy/TradeStrategyFactory.java†L1-L12】【F:src/main/java/wss/economy/TraderType.java†L1-L3】

### `wss.util`

**Primary responsibility:** Shared enums and simple utility models.

- `Direction` models movement vectors and includes the cardinal and diagonal directions used by brains and vision logic.【F:src/main/java/wss/util/Direction.java†L1-L12】
- `DifficultyLevel` defines the difficulty options for map generation.【F:src/main/java/wss/util/DifficultyLevel.java†L1-L3】

---

## Architecture Notes & Extensibility

- **Strategy pattern in two places**: brains (movement decisions) and vision (what squares are visible). This makes it straightforward to add new behaviors by creating a new class implementing `Brain` or `Vision`.
- **Config hooks**: `GameParameters` and `BrainSelectionConfig` load values from `wss.config.GameConfig` if available; otherwise defaults are used. This allows future extension via property files or runtime configuration without altering core logic.【F:src/main/java/wss/controller/GameParameters.java†L12-L22】【F:src/main/java/wss/actor/brain/BrainSelectionConfig.java†L1-L33】
- **World expansion**: new terrain types or items can be introduced by extending `Terrain` or `Item` and updating `TerrainDistribution` or map generation logic as needed.【F:src/main/java/wss/world/terrain/Terrain.java†L1-L22】【F:src/main/java/wss/world/item/Item.java†L1-L15】【F:src/main/java/wss/world/Map.java†L18-L52】
