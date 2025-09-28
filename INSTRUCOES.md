## Visão geral do projeto

Este repositório é um mod para Minecraft usando Neo Forge (uma variação/ecossistema do Forge). O mod de exemplo tem as estruturas mínimas para registrar blocos, itens, abas criativas e configurar telas de configuração.

Arquivos principais lidos:
- `src/main/java/com/example/villagerscommunity/VillagersCommunity.java` — classe principal do mod; registra blocos, itens, abas, listeners e faz setup comum.
- `src/main/java/com/example/villagerscommunity/VillagersCommunityClient.java` — código específico do client: registra a tela de configuração e faz setup client-side.
- `src/main/java/com/example/villagerscommunity/Config.java` — definição do arquivo de configuração do mod usando a API de config do Neo Forge.
- `src/main/resources/assets/villagerscommunity/lang/en_us.json` — traduções/linguagem (keys usadas pela Creative Tab, nomes de itens e strings de config).
- `src/main/templates/META-INF/neoforge.mods.toml` — template do TOML com metadados do mod (id, versão, dependências).

Estrutura e pontos de entrada (conceitos curtos):
- Mod class (`@Mod`): equivalente ao ponto de entrada do mod. Neo Forge cria a instância e injeta `IEventBus` e `ModContainer`.
- DeferredRegister: mecanismo para registrar blocos/itens/abas de forma declarativa e segura durante o tempo de registro do jogo.
- Event Bus: sistema de eventos (lifecycle events: common setup, client setup; e eventos de jogo como server starting).
- Client-only class: anotada com `dist = Dist.CLIENT` — usada para código que só roda no cliente (render, GUIs).

Contrato rápido (inputs/outputs) do mod atual
- Inputs: configurações definidas em `Config` (booleano, int, lista de strings de items).
- Outputs esperados: items/blocks registrados sob o namespace `villagerscommunity`, mensagens de log no setup, uma aba criativa com o item de exemplo.

Mapeamento Java -> TypeScript/Node (analogia para você que tem background em TS):
- Classe Java com campos estáticos (public static final): parecido com `export const` em TS — valores globais usados em todo o projeto.
- Construtor do mod com injeção de `IEventBus`: parecido com um módulo que recebe um EventEmitter/Bus e registra handlers.
- DeferredRegister.register(modEventBus): similar a chamar `register()` em um plugin para adicionar rotas/handlers antes do servidor subir.
- Anotações como `@Mod`, `@SubscribeEvent`: anotações são metadados estáticos que o framework usa para descobrir classes e métodos, similar a decorators em TypeScript (ex.: NestJS `@Controller`).
- Tipos imutáveis e fortes: Java tem tipagem estática como TypeScript, mas com diferenças (checked vs unchecked). Use a analogia de interface/typing do TS para compreender assinaturas de métodos.

Como compilar e rodar localmente
1) Requisitos
   - JDK compatível (veja `gradle.properties` / template do neoforge; normalmente JDK 17+ para versões modernas de Minecraft/Forge). Instale o JDK e assegure `JAVA_HOME` apontando para ele.
   - Gradle wrapper disponível: use `gradlew.bat` no Windows.

2) Comandos úteis (PowerShell / Windows)
   - Compilar o mod (gera JAR em `build/libs`): execute `./gradlew build` ou no Windows PowerShell `./gradlew.bat build` (ou apenas `gradlew build`).
   - Rodar o Minecraft com o ambiente de desenvolvimento (se configurado no build): `./gradlew runClient` (pode variar conforme a configuração do template). No Windows: `./gradlew.bat runClient`.
   - Limpar build: `./gradlew.bat clean`.

3) Observações
   - Os nomes de tasks (`runClient`) dependem da configuração do plugin de desenvolvimento (ex.: Loom). Se uma task não existir, verifique `build.gradle`.

Como o mod usa Neo Forge (resumo técnico)
- Registros (DeferredRegister) criam ids `villagerscommunity:example_item` e `villagerscommunity:example_block`. O registro é ligado ao event bus do mod no construtor.
- `Config` usa `ModConfigSpec` para declarar opções e um `Spec` que é registrado como config do mod via `modContainer.registerConfig(...)`.
- `VillagersCommunityClient` registra um `IConfigScreenFactory` com `ConfigurationScreen::new` para permitir editar configurações via GUI do mod.
- Eventos: `commonSetup` é registrado no `modEventBus` com `modEventBus.addListener(this::commonSetup)`. Anotações `@SubscribeEvent` são usadas para métodos que serão invocados pelo `NeoForge.EVENT_BUS`.

Boas práticas e próximos passos sugeridos
- Documente a versão do JDK necessária em `README.md`.
- Adicione traduções PT-BR em `en_us.json` criando `pt_br.json` com chaves relevantes.
- Comece com alterações simples: trocar `example_item` por um item temático do mod, adicionar propriedades e testar o processo de build/exec.
- Se quiser ajuda para implementar features (ex.: villager interactions, trades), descreva o comportamento desejado e eu posso escrever o esqueleto Java para você com analogias a TS.

Notas sobre aprender Java com base em TypeScript
- Estruturas principais que você vai usar: classes, métodos, campos estáticos, pacotes (namespaces), herança e interfaces.
- Diferenças importantes:
  - Tipos primitivos (int, boolean) vs wrapper classes (Integer, Boolean) — prefira primitivos quando possível.
  - Null/Optional: Java tem `null` e desde Java 8 `Optional<T>`; tenha cuidado com NPEs.
  - Exceções verificadas (checked exceptions) exigem tratamento (try/catch) ou declaração `throws`.
- Convenção: nomes de pacotes em lowercase, classes em PascalCase, métodos e campos em camelCase.

Status das tasks desta sessão
- "Ler e analisar arquivos do projeto": concluído.
- "Resumir base Neo Forge": em progresso (este arquivo cobre grande parte).
- "Gerar arquivo de instruções": este arquivo foi criado como `INSTRUCOES.md`.

Se quiser, os próximos passos que eu posso executar automaticamente:
- Criar `pt_br.json` de tradução inicial com as chaves existentes.
- Adicionar um script Gradle/README.md com instruções detalhadas e checklist de desenvolvimento.
- Implementar um exemplo simples extra (ex.: um item que emite mensagem no chat ao usar) com testes básicos.

Diga qual desses próximos passos prefere que eu faça e eu executo em seguida.

----
Arquivo gerado automaticamente: este `INSTRUCOES.md` foi criado para ajudar você a entender a base do mod e acelerar as primeiras mudanças.
