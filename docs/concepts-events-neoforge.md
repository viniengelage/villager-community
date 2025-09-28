# Concepts — Events (NeoForge)

Local copy of: https://docs.neoforged.net/docs/concepts/events

## Overview

NeoForge provides an event system. Handlers can be registered via `IEventBus#addListener`, `@SubscribeEvent` (and registering the containing instance or class), or `@EventBusSubscriber` for static handlers.

## Registering handlers

- IEventBus#addListener: register method references (e.g., `modBus.addListener(this::onSetup)`).
- @SubscribeEvent: annotate methods and register instance/class on the appropriate bus (`NeoForge.EVENT_BUS` or mod bus).
- @EventBusSubscriber: auto-register static handlers; optionally specify `modid` and `value=Dist.CLIENT` for sided auto-registration.

## Event options

- Fields/methods provide event context (e.g., entity, level).
- Event hierarchy: prefer listening to concrete subevents (not abstract super events).
- Cancellable events: implement `ICancellableEvent`; use `setCanceled(true)` to cancel.
- TriState/Result: some events have 3-way results controllable via `set*` methods.
- Priority: `EventPriority` controls ordering; higher priorities run first.
- Sided events: some events are only fired on a side — register accordingly.

## Event buses

- Main game bus: `NeoForge.EVENT_BUS` (game events).
- Mod event bus: passed into mod constructor; used for lifecycle and registration events (many run in parallel).
- Lifecycle: mod constructor, `FMLConstructModEvent`, registry events, `FMLCommonSetupEvent`, sided setup events (`FMLClientSetupEvent` / `FMLDedicatedServerSetupEvent`), `FMLLoadCompleteEvent`.

## InterModComms

- Use `InterModComms.sendTo` during `InterModEnqueueEvent`; process messages during `InterModProcessEvent`.

---

Snapshot for local reference.