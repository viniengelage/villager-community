# Concepts — Sides (NeoForge)

Local copy of: https://docs.neoforged.net/docs/concepts/sides

## Overview

Minecraft (and NeoForge) distinguishes between physical and logical sides:

- Physical side: the running program — `Dist.CLIENT` (physical client) or `Dist.DEDICATED_SERVER` (physical server).
- Logical side: where game logic runs — `LogicalSide.CLIENT` (logical client) or `LogicalSide.SERVER` (logical server). The logical server may run inside a physical client in singleplayer.

## Key checks and patterns

- `Level#isClientSide()` — the most common runtime check to determine the logical side for game logic.
- `FMLEnvironment.dist` — check the physical side (useful when registering client-only listeners).
- Use `@Mod(dist = Dist.CLIENT)` or separate client-only mod classes to isolate client-only code.

## Tips

- Test on dedicated servers to catch class-not-found errors due to client-only classes being referenced on the server.
- Avoid static fields that are accessed from both sides without care.

---

Snapshot for local reference.