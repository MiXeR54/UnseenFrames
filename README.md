# UnseenFrames

Invisible item frames for Paper 26.2. Shift + right-click a placed item frame with shears to hide
the frame while keeping its item visible. An implementation of the "Invisible frames" feature from
[wiki.lotus-land.net](https://wiki.lotus-land.net/info/server-functions#невидимые-рамки).

## Features

- **Shift + right-click with shears** in the main hand hides the frame. Repeat the gesture to turn it back into a regular frame.
  Works with regular and glow item frames and consumes shears durability.
- A hidden frame **with an item is invisible**; only the item shows. An **empty** hidden frame stays visible
  so it can be found and broken; it hides again as soon as an item is placed inside.
- **Right-clicking a hidden frame does not rotate the item.** Rotate with Shift + right-click.
- When a hidden frame hangs on a chest, barrel, shulker box, furnace or another container,
  **right-click opens the container**. A `PlayerInteractEvent` is fired first, so region protection
  and lock plugins keep working.
- Frames made invisible by another plugin or by commands are **adopted** on chunk load or first
  interaction and follow the same rules from then on.
- A broken frame drops as a regular item frame: the flag lives on the entity only.
- Folia-compatible, no global scheduler.

## Commands

`/unseenframes` (alias `/uf`), permission `unseenframes.admin`:

| Command | Action |
|---|---|
| `/uf reload` | Re-read `config.yml` and resync loaded frames |
| `/uf scan [radius]` | Highlight hidden frames with particles for a few seconds |
| `/uf toggle` | Toggle the frame you are looking at, no shears or sneaking needed |

## Permissions

| Permission | Default | Description |
|---|---|---|
| `unseenframes.use` | everyone | Hide and reveal frames with shears |
| `unseenframes.admin` | operators | `/unseenframes` command |

Without `unseenframes.use` a click with shears behaves like vanilla.

## Configuration

| Key | Default | Meaning |
|---|---|---|
| `tool` | `minecraft:shears` | Item used to toggle frames |
| `require-sneak` | `true` | Require Shift |
| `tool-damage` | `1` | Durability per toggle, `0` = never damage the tool |
| `reveal-when-empty` | `true` | Empty hidden frames become visible again |
| `lock-rotation` | `true` | Rotate only with Shift + right-click |
| `container-passthrough.enabled` | `true` | Right-click on a hidden frame opens the container behind it |
| `container-passthrough.check-protection` | `true` | Check protection via `PlayerInteractEvent` |
| `adopt-invisible-frames` | `true` | Adopt frames hidden by other means |
| `sound.key`, `sound.volume`, `sound.pitch` | `minecraft:item.shears.snip` | Toggle sound, empty key = silent |
| `scan.*` | `16` / `64` / `6` | Default radius, maximum radius, highlight duration |
| `messages.*` | | Messages in MiniMessage, empty string = do not show |

All player-facing messages live under `messages` and can be translated there.

## How it works

The "hidden" flag is stored in the frame entity's `PersistentDataContainer` (`unseenframes:hidden`).
Vanilla invisibility (`Invisible`) is not stored separately; it is derived from the flag and the presence
of an item: after a toggle, one tick after `PlayerItemFrameChangeEvent` (PLACE, REMOVE) and on chunk load
(`EntitiesLoadEvent`). That keeps frames consistent even when another plugin changes the item through the API.

The toggle listens to `PlayerInteractEntityEvent` at `HIGHEST` priority with `ignoreCancelled`,
so a region denial cancels the event before the plugin acts.

## Building

Requires JDK 25.

```
./gradlew build
```

The resulting jar is `build/libs/UnseenFrames-<version>.jar` (bStats is relocated inside).

Dev server with Paper 26.2 in the `run/` directory:

```
./gradlew runServer
```

Accept the EULA in `run/eula.txt` on the first start.

## bStats

Metrics are enabled once the plugin is registered on [bstats.org](https://bstats.org/):
put the service ID into `UnseenFrames.BSTATS_SERVICE_ID`. While the ID is `0`, metrics stay disabled.
