# Riftbound 0.1.1

A fantasy loot mod for Minecraft **1.21.1** (NeoForge) with Path of Exile–inspired itemization.

## Features (0.1.1)

- **Shard Dust** — crafting currency (mob drops, smelting ore)
- **Shard Ore** — mineable ore (creative / `/setblock` for now; worldgen planned)
- **Shard Blade** — base weapon with item rarity (normal / magic)
- **5 affixes** on magic blades (damage, attack speed, fire aspect, and more)
- **Mob loot** — hostile mobs can drop dust and blades
- **Shards tab** in the player inventory (survival and creative): two input slots and a deterministic result preview
- **Transmutation** — shard dust + normal shard blade → magic blade (slot order does not matter)
- **Localization** — English and Russian in-game strings

## Requirements

- **Java 21** (JDK)
- **NeoForge** for Minecraft 1.21.1
- **Gradle** (wrapper included) or an IDE with Gradle support (IntelliJ IDEA recommended)

## Development setup

1. Clone the repository and open it as a Gradle project in your IDE.
2. Wait for Gradle sync (first run may take 10–30 minutes while Minecraft dependencies download).
3. Run the client:
   ```bat
   gradlew.bat runClient
   ```
4. In-game: open the **Riftbound** creative tab to grab items.

## Testing loot and transmutation

1. `/gamemode survival`
2. Kill a hostile mob (zombie, skeleton, etc.) — chance to drop shard dust or a shard blade
3. Open inventory (`E`) → **Shards** tab
4. Place **shard dust** and a **normal shard blade** in the two input slots
5. Take the magic blade from the result slot

## Building the mod JAR

```bat
gradlew.bat build
```

Output: `build/libs/riftbound-0.1.1.jar` — copy into your NeoForge 1.21.1 `mods` folder.

## Project layout

```
src/main/java/com/riftbound/
  RiftboundMod.java           — mod entry point
  registry/                   — blocks, items, menus, creative tab
  loot/                       — rarity, affixes, item generation
  transmutation/              — combine rules and deterministic seeds
  menu/                       — transmutation container
  client/                     — screens and inventory tabs
  event/                      — mob loot drops
  network/                    — client/server tab switching
```

## Roadmap (0.2+)

- Rare (yellow) items
- Crafting altar block
- Shard ore world generation
- Reroll currency (second crafting orb)

## License

See `TEMPLATE_LICENSE.txt` (MDK template). Mod license: All Rights Reserved (see `gradle.properties`).
