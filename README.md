# StressCraft (W.I.P)

[![License](https://img.shields.io/github/license/Cubxity/stresscraft?style=flat-square)](COPYING)
[![Discord](https://img.shields.io/badge/join-discord-blue?style=flat-square)](https://discord.gg/vxecYcWXyf)

State-of-art Minecraft stressing software written in Kotlin.

## Disclaimer

StressCraft should **ONLY** be used in your own server environment. We do not endorse the use of StressCraft for any
other purposes than testing your own servers.

## How to use?

> **NOTE:** DO NOT DO THIS IN PRODUCTION, EVER.

- Set `online-mode` to false on your server(s)
- Set `network-compression-threshold` to `-1`
- Set `connection-throttle` (spigot) to `-1`
- Increase `max-joins-per-tick` (paper) to your liking
- Execute the jar with proper arguments

## Who needs this?

- Michael
- "Cloud-native Minecraft" enthusiasts
- Reliability engineers

## Roadmap

*(in no particular order)*

- [x] Performant stresser
- [ ] Chat flooder
- [ ] Scripting?
- [ ] Physics simulation
- [ ] Random movements
- [ ] Non-TTY support 
- [ ] Velocity forwarding?
- [ ] Dockerfile
- [ ] Helm chart?
- [ ] GUI Frontend?
- [ ] Prometheus exporter?