# Essential Auto Sprint (EAS)

<div align="center">

**Automatically sprint when pressing W — no Ctrl, no double-tap.**

[![Modrinth](https://img.shields.io/badge/Modrinth-EAS-00AF5C?style=for-the-badge&logo=modrinth)](https://modrinth.com/mod/essential-auto-sprint-(eas))
[![License: LGPL-3.0](https://img.shields.io/badge/License-LGPL--3.0-blue?style=for-the-badge)](LICENSE)
[![Fabric](https://img.shields.io/badge/Loader-Fabric-DBA213?style=for-the-badge)](https://fabricmc.net/)

</div>

---

## What is EAS?

**Essential Auto Sprint (EAS)** is a lightweight Fabric client mod that automatically enables sprinting the moment you press `W`. No need to hold `Ctrl`, no need to double-tap — just move forward and you're sprinting.

---

## Features

- ⚡ **Instant auto-sprint** — Sprint activates the moment you press `W`
- 🔑 **Configurable toggle keybind** — Press `H` (default) at any time to enable or disable the mod mid-game
- 🔔 **HUD notification** — A brief message above the hotbar shows the current state when toggled
- ⚙️ **Mod Menu integration** — Full config screen accessible via Mod Menu (optional)
- 🌐 **Broad version support** — Works on Minecraft `1.14.4` through `1.21.11`
- 💾 **Persistent settings** — Your preferences are saved between sessions in `config/eas.json`

---

## Usage

| Action | Default |
|---|---|
| Toggle auto-sprint on/off | `H` |
| Change toggle key | Open Config Screen → Keybind |
| Open config screen | Mod Menu → EAS → Configure |

After installation, auto-sprint is **enabled by default**. Press `H` at any time while in-game to toggle it. A brief message will appear above your hotbar confirming the new state.

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. *(Optional)* Install [Mod Menu](https://modrinth.com/mod/modmenu) for in-game configuration
3. Download EAS from [Modrinth](https://modrinth.com/mod/essential-auto-sprint-(eas))
4. Place the `.jar` in your `mods/` folder

> **Note:** EAS is a **client-side only** mod. It does not need to be installed on servers.

---

## Supported Versions

| Minecraft | Status |
|---|---|
| 1.14.4 – 1.21.11 | ✅ Supported |
| 26.1.2+ | ✅ Supported (no Mod Menu UI yet) |

---

## Configuration

The config file is stored at `.minecraft/config/eas.json`:

```json
{
  "enabled": true,
  "toggleKey": 72
}
```

| Field | Type | Default | Description |
|---|---|---|---|
| `enabled` | boolean | `true` | Whether auto-sprint is active |
| `toggleKey` | int | `72` (H) | GLFW key code for the toggle keybind |

Common GLFW key codes: `H=72`, `J=74`, `G=71`, `N=78`, `M=77`

---

## For Developers

This project uses a multi-version setup powered by [Fallen-Breath/preprocessor](https://github.com/Fallen-Breath/preprocessor).

### Building

> ⚠️ **Builds are done exclusively via GitHub Actions.** Do not build locally.

Push changes to `main` and the workflow will build all 18 subprojects automatically.

```bash
git push origin main
```

Artifacts will appear under the **Actions** tab on GitHub.

---

## Authors

- **GBX Team** — Lead development
- **DevGBX9** — Core contributor

---

## License

LGPL-3.0 — see [LICENSE](LICENSE) for details.
