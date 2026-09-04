![UnseenFrames](https://raw.githubusercontent.com/MiXeR54/UnseenFrames/main/assets/banner.png)

# UnseenFrames

Hide item frames while keeping what is inside them visible. Hold **shears**, **Shift + right-click**
a placed item frame — the frame disappears, the item stays. Repeat the gesture to bring the frame back.

Works with regular and glow item frames, on Paper and Folia.

## Why players like it

- Item shops, storage rooms and map walls without the frame border.
- An **empty** hidden frame stays visible, so nothing gets lost: it hides again the moment an item is put in.
- **Right-click no longer rotates** a hidden frame by accident — rotation needs Shift + right-click.
- A hidden frame on a chest, barrel or furnace **opens that container** on right-click. Protection plugins are asked first,
  so claims and locks keep working.
- Breaking a hidden frame drops an ordinary item frame.

## For server owners

- Everything is configurable: the tool, whether Shift is required, durability cost, sound, rotation lock,
  container pass-through and the messages (MiniMessage, easy to translate).
- Frames that were made invisible by another plugin or by `/data` commands are **adopted** automatically,
  so switching from another solution needs no migration.
- Permissions: `unseenframes.use` (everyone) for the gesture, `unseenframes.admin` (operators) for the command.
- `/uf reload`, `/uf scan [radius]` (highlights hidden frames with particles) and `/uf toggle`.
- No dependencies. Anonymous usage statistics via bStats, switchable off in `plugins/bStats/config.yml`.

## Links

- Source, full documentation and issues: [GitHub](https://github.com/MiXeR54/UnseenFrames)
- [Changelog](https://github.com/MiXeR54/UnseenFrames/blob/main/CHANGELOG.md)
- Licensed under [MIT](https://github.com/MiXeR54/UnseenFrames/blob/main/LICENSE)
