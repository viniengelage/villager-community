# NeoForge — Getting Started (resumo)

Fonte: https://docs.neoforged.net/docs/gettingstarted/ (NeoForge Documentation — content © 2025, MIT License)

Este arquivo reúne os pontos mais importantes do guia "Getting Started" do NeoForge para referência rápida no repositório. Use como auxílio durante o desenvolvimento do mod.

## Visão geral

NeoForge é uma API de modding para Minecraft baseada no Forge. O MDK (Mod Developer Kit) do NeoForge gera um projeto Gradle com estruturas úteis para registrar blocos, itens, eventos e rodar o Minecraft em um ambiente de desenvolvimento.

Principais recursos citados no guia:
- Templates/MDK gerador de projetos (mod-generator)
- Plugins Gradle para desenvolvimento (ModDevGradle / NeoGradle)
- Configurações básicas em `gradle.properties`, `build.gradle` e `settings.gradle`
- Run configurations geradas para `runClient`, `runServer`, etc.

## Pré-requisitos

- JDK 21 (64-bit) — NeoForge recomenda builds modernos (p.ex. Microsoft OpenJDK). Verifique com `java -version`.
- IDE de sua preferência: IntelliJ IDEA, Eclipse, VS Code, etc.
- Gradle (usa wrapper `gradlew` incluso no MDK).

## Estrutura do projeto (expectativa do MDK)

- `src/main/java` — código do mod
- `src/main/resources` — assets, localizações, `META-INF/neoforge.mods.toml`
- `build.gradle`, `gradle.properties`, `settings.gradle` — configurações do build
- `gradlew` / `gradlew.bat` — wrapper Gradle
- `run/` — gerada quando você executa `runClient`/`runServer`

## Configurando o workspace

1. Gere o MDK no site do NeoForge/mod-generator ou inicie com o template.
2. Extraia o projeto e abra na sua IDE.
3. Ao abrir, o Gradle fará download das dependências (inclui Minecraft e mappings). Isso pode demorar na primeira vez.
4. Se modificar arquivos Gradle, recarregue o projeto Gradle na IDE.

## Principais arquivos que você verá

- `META-INF/neoforge.mods.toml` — metadados do mod (mod id, versão, main class)
- Classe principal anotada com `@Mod` — ponto de entrada do mod (ex.: `VillagersCommunity`)
- `DeferredRegister` e `DeferredItem/Block` — utilitários para registrar conteúdo (itens, blocos, tabs)
- Event bus:
  - `modEventBus.addListener(this::commonSetup)` para lifecycle events
  - `NeoForge.EVENT_BUS.register(...)` ou `@SubscribeEvent` para game events

## Execução e testes

- Compilar classes Java:

```powershell
./gradlew.bat classes
```

- Rodar client de desenvolvimento:

```powershell
./gradlew.bat runClient
```

- Rodar server de desenvolvimento:

```powershell
./gradlew.bat runServer
```

Observações:
- O servidor de desenvolvimento pode pedir que aceite o EULA (`run/eula.txt`) e ajustar `server.properties` (por ex. `online-mode=false`) para testes locais.
- A pasta `run/` contém `logs/`, `mods/`, `config/` e é onde o mundo de teste é gerado.

## Eventos e compatibilidade

- Os nomes de classes de eventos e alguns métodos podem variar entre versões/mappings. Use os eventos expostos pelo NeoForge/Forge apropriados para sua versão. Se você escrever código que precise ser agnóstico, considere fallback/reflexão com cuidado (mas evite em produção se possível).
- O EventBus do NeoForge valida assinaturas de `@SubscribeEvent` — métodos anotados precisam receber um tipo de evento compatível.

## Boas práticas rápidas

- Prefira usar `DeferredRegister` para registrar blocos/itens/tabs.
- Separe lógica de registro (construção) da lógica de runtime (event handlers). Use `modEventBus` para registro e `NeoForge.EVENT_BUS` para eventos de jogo, quando apropriado.
- Mantenha strings de mod id centralizadas (ex.: `public static final String MODID = "yourmodid"`).
- Teste frequentemente com `runClient`/`runServer`.

## Links úteis

- Getting Started: https://docs.neoforged.net/docs/gettingstarted/
- NeoForge GitHub: https://github.com/neoforged
- NeoForge docs home: https://docs.neoforged.net/

---

Se quiser, eu posso adicionar outros resumos (ex.: exemplos de handlers de evento, templates de `DeferredRegister`, ou trechos úteis copiados da doc com referências) dentro da pasta `docs/`.
