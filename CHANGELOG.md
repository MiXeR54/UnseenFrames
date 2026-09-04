# Changelog

All notable changes to UnseenFrames are documented in this file. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).
The topmost entry is published as the Hangar changelog of the release.

## [1.0.0] - 2026-09-04

First public release for Paper 26.2.

- Shift + right-click a placed item frame with shears to hide the frame; the item inside stays visible.
- Empty hidden frames stay visible so they can be found and broken, and hide again once an item is placed.
- Rotation of a hidden frame is locked behind Shift + right-click.
- Right-click on a hidden frame that hangs on a container opens the container, respecting protection plugins.
- Frames made invisible by other plugins or commands are adopted on chunk load, on interaction and on `/uf reload`.
- Commands `/uf reload`, `/uf scan [radius]` and `/uf toggle`, permissions `unseenframes.use` and `unseenframes.admin`.
- Everything configurable in `config.yml`, all messages in MiniMessage.
- Folia-compatible, anonymous usage statistics via bStats.
