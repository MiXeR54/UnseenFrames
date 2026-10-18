# Releasing

The jar is published to [Hangar](https://hangar.papermc.io/MiXeR54/UnseenFrames) by the
[hangar-publish-plugin](https://github.com/HangarMC/hangar-publish-plugin) and to
[Modrinth](https://modrinth.com/plugin/unseenframes) by [Minotaur](https://github.com/modrinth/minotaur),
both configured at the bottom of `build.gradle.kts`. Building requires JDK 25.

## Cutting a release

1. Bump `version` in `gradle.properties`.
2. Add the new entry at the top of `CHANGELOG.md`. Its body, without the heading, becomes the changelog
   on Hangar and Modrinth.
3. Commit and push.
4. `git tag v1.2.3 && git push --tags` - GitHub Actions builds the jar and uploads it to Hangar and Modrinth
   using the `HANGAR_API_TOKEN` and `MODRINTH_TOKEN` repository secrets.

Only tag a version that has not been published yet: both sites reject a duplicate version and the workflow fails.

## Publishing from a local machine

```
./gradlew publishPluginPublicationToHangar
./gradlew modrinth
```

The API tokens are read from the `HANGAR_API_TOKEN` and `MODRINTH_TOKEN` environment variables and fall back
to the git-ignored `pat_hangar` and `pat_modrinth` files in the project root. Hangar tokens are created in the
Hangar user settings; uploading a version needs the `create_version` permission, the page sync below needs
`edit_page`. Modrinth tokens are personal access tokens from the Modrinth settings; they need the
`Create versions` and `Write projects` scopes.

## Project page

`hangar/page.md` is the text of the project page on both sites. It is not part of a release, upload it
whenever it changes:

```
./gradlew syncAllPagesToHangar modrinthSyncBody
```

## Not covered by the API

Hangar: creating the project and editing its settings - summary, license, category, keywords, tags - only
works in the web interface; the public API has no endpoints for them. Modrinth has an API for all of that;
its project `unseenframes` (ID `Iif9taOX`) was created through it.

The project icon on both sites is `assets/logo.png`, the README banner is `assets/banner.png`; both are
rendered from the SVG next to them, see the comment in each file.
