# Releasing

The jar is published to [Hangar](https://hangar.papermc.io/MiXeR54/UnseenFrames) by the
[hangar-publish-plugin](https://github.com/HangarMC/hangar-publish-plugin), configured at the bottom of
`build.gradle.kts`. Building requires JDK 25.

## Cutting a release

1. Bump `version` in `gradle.properties`.
2. Add the new entry at the top of `CHANGELOG.md`. Its body, without the heading, becomes the Hangar changelog.
3. Commit and push.
4. `git tag v1.2.3 && git push --tags` - GitHub Actions builds the jar and uploads it to Hangar
   using the `HANGAR_API_TOKEN` repository secret.

Only tag a version that has not been published yet: Hangar rejects a duplicate version and the workflow fails.

## Publishing from a local machine

```
./gradlew publishPluginPublicationToHangar
```

The API token is read from the `HANGAR_API_TOKEN` environment variable and falls back to the git-ignored
`pat_hangar` file in the project root. Tokens are created in the Hangar user settings; uploading a version
needs the `create_version` permission, the page sync below needs `edit_page`.

## Project page

`hangar/page.md` is the text of the Hangar project page. It is not part of a release, upload it whenever it changes:

```
./gradlew syncAllPagesToHangar
```

## Not covered by the API

Creating the project and editing its settings - summary, license, category, keywords, tags - only works in the
Hangar web interface; the public API has no endpoints for them. The project icon is `assets/logo.png`,
the README banner is `assets/banner.png`; both are rendered from the SVG next to them, see the comment in each file.
