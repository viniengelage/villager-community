# Concepts — Registries (NeoForge)

Local copy of: https://docs.neoforged.net/docs/concepts/registries

## Overview

Registries are mappings from registry names (ResourceLocations) to singleton objects (blocks, items, entities, etc.). Proper registration is essential to avoid crashes.

## Methods for registering

- DeferredRegister (recommended): create a `DeferredRegister` for the desired registry (e.g., `BuiltInRegistries.BLOCKS`) and register `Supplier`/`DeferredHolder` entries. Attach it to the mod `IEventBus` via `.register(modBus)` in your mod constructor.
- RegisterEvent: lower-level event-based registration by listening to `RegisterEvent` on the mod bus and manually calling `registry.register(...)`.

## Querying registries

- Use `BuiltInRegistries.<REGISTRY>.getValue(ResourceLocation)` or `.getKey(...)` to lookup entries. Only query registries after registration is complete.
- Use `.containsKey(...)` to check for the presence of third-party mod entries.

## Custom registries

- You can create custom registries with `ResourceKey` and `RegistryBuilder`, register them during `NewRegistryEvent`, and then use `DeferredRegister` or `RegisterEvent` to add entries.

## Datapack registries

- Datapack registries load from datapack JSONs at world load and require a Codec for serialization. Use `RegistrySetBuilder` and data providers to generate JSON data.

---

This is a snapshot for local reference.