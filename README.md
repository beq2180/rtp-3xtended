# RTPExtended — Paper 1.21.11

Standalone random teleport + named homes plugin. No Vault, Essentials, LuckPerms, or other plugin dependency.

## Commands

### RTP
- `/rtp` — random Overworld location
- `/rtp o` / `/rtp overworld`
- `/rtp n` / `/rtp nether`
- `/rtp e` / `/rtp end`

### Homes
- `/sethome <name>` — set or replace a home
- `/home <name>` — teleport to a home
- `/homes` — list homes and your current limit
- `/delhome <name>` — delete a home

Default maximum homes: **3**.

### Admin
- `/setmaxhomes <player> <amount>` — change an individual player's home limit
- Permission: `rtpextended.admin` (default OP)

The global default is configured in `config.yml` with `default-max-homes: 3`.

RTP radii, retry count, and basic safety checks are configurable in `config.yml`.

Homes are persisted in `plugins/RTPExtended/homes.yml`.

## Build

GitHub Actions uses Java 21 and Gradle 9.1.0. The generated plugin JAR is uploaded as an Actions artifact.
