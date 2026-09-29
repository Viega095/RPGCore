# ⚔️ RPGCore - Advanced MMORPG, Raids & Runeword Engine

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://openjdk.org)
[![Build](https://img.shields.io/badge/Build-Maven-orange.svg)](https://maven.apache.org)

RPGCore turns your Minecraft server into a fully fledged MMORPG featuring Classes, Skill Trees, Blacksmithing (+1 to +15), Diablo II style Runewords, Mythic+ Keystone Dungeons, Boss Telegraph ground indicators, World Boss Raids with Enrage phases, and Elemental Party Synergies.

---

## 🌟 Key Features

* **Blacksmithing & Upgrading (`/forge`)**: Upgrade equipment from +1 to +15 with scaling stats (+8% per level), sound effects, and risk mechanics.
* **Mythic+ Keystone Rifts (`/keystone`)**: Instanced wave dungeons with timer countdowns, scaling elite mobs, and weekly rotating affixes (*Sanguine*, *Volcanic*, *Tyrannical*, *Bolstering*).
* **Diablo II Runewords (`/runewords`)**: 33 unique runes (`El` to `Zod`) and socketing recipes:
  * `ENIGMA` (Jah + Ith + Ber) -> Teleportation Dash, +2 to All Skills.
  * `GRIEF` (Eth + Tir + Lo + Mal + Ral) -> +350 Flat Physical Damage, Armor Penetration.
  * `BREATH OF THE DYING` (Vex + Hel + El + Eld + Zod + Eth) -> +50% Life Steal, Unbreakable.
  * `FORTITUDE` (El + Sol + Dol + Lo) -> +300% Enhanced Armor, Chilling Ice Shield.
* **Substat Reforging (`/reforge`)**: Apply powerful legendary prefixes (*[PIADOSO]*, *[DEMONÍACO]*, *[TITÁNICO]*, *[ARCANO]*, *[FEROZ]*, *[MÍTICO]*).
* **Boss Telegraph Engine**: Red ground warning zones (circles and directional cones) prior to devastating area-of-effect abilities.
* **World Boss Raids (`/raid`)**: Massive open-world boss encounters with boss health bars, Enrage transitions at 25% HP, and damage contribution leaderboards.
* **Equipment Set Bonuses**: 2/4/6 piece bonuses for Shadow Assassin, Dragon Slayer, and Celestial Archmage sets.
* **Talent Specialization Trees (`/talents`)**: 3 distinct branches per class with persistent talent point spending.

---

## 📜 Commands

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/class` | Class selection and stats | `rpg.use` |
| `/talents` | Open class talent tree | `rpg.use` |
| `/forge` | Blacksmith forging menu | `rpg.use` |
| `/dungeon <list\|enter\|leave>` | Wave-based instanced rifts | `rpg.use` |
| `/keystone <give\|activate>` | Mythic+ Keystone challenge rifts | `rpg.use` |
| `/runewords <list\|socket\|give>` | Diablo II Runewords and runes | `rpg.use` |
| `/reforge` | Reforge item prefixes and stats | `rpg.use` |
| `/raid spawn` | Spawn a World Boss raid | `rpg.admin` |

---

## 🔨 Building from Source

```bash
git clone <REPO_URL>
cd Rpg
mvn clean package
```
Output JAR will be in `target/rpg-core-1.0-SNAPSHOT.jar`.
