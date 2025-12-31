# BloodForged

A deep, realism-leaning Minecraft mod centered around metalworking, forging, and modular equipment.

**Minecraft Version:** 1.21.5  
**Mod Loader:** Fabric  
**License:** MIT

## Vision

BloodForged transforms tool and weapon crafting into a meaningful, skill-based experience:

- **Tools are crafted, not spawned** — every weapon tells a story of its materials, forging process, and the skill of the smith
- **Materials matter** — physical properties like hardness, toughness, and flexibility drive stats
- **Progression is earned** — master smithing through practice, not grinding
- **No "best" weapon** — emergent stats from modular parts create trade-offs

## Features

### Modular Equipment System
All weapons and tools are composed of parts (blade, handle, guard). Each part is made from a material and contributes to the final item's stats and traits.

### Material-Driven Design
Materials define physical properties (hardness, toughness, flexibility, density) that translate into tool stats. Current materials:
- **Bronze** — Easy to forge, balanced stats (Tier 1)
- **Iron** — Strong but rust-prone (Tier 2)
- **Steel** — Superior but demanding to produce (Tier 3)

### Smithing & Forging
- Ores must be crushed, washed, and smelted
- Alloying requires correct ratios and temperature control
- Anvil smithing minigame affects quality and stats
- Quality percentage (0-120%) scales final stats

## Current Progress

| Phase | Status |
|-------|--------|
| Phase 1: Core Materials | ✅ Done |
| Phase 2: Core Items & Blocks | ✅ Done |
| Phase 3: Smithing System | 🔄 In Progress |
| Phase 4: Tool Assembly | Planned |
| Phase 5: Advanced Features | Planned |
| Phase 6: World & NPCs | Planned |

### Implemented
- Material system with registry and stats
- Data components for material/quality storage
- Basic blocks: Smithing Anvil, Forge, Open Furnace
- Ore items: Copper, Tin, Bronze, Iron, Steel
- Tool part items: Blade, Handle, Guard

## Building

```bash
./gradlew build
```

Output jar will be in `build/libs/`.

## Requirements

- Java 21+
- Fabric Loader 0.16.14+
- Fabric API

## License

MIT License — see [LICENSE](LICENSE)
