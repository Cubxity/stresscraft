# StressCraft (work in progress)

[![License](https://img.shields.io/github/license/Cubxity/stresscraft?style=flat-square)](COPYING)
[![Discord](https://img.shields.io/badge/join-discord-blue?style=flat-square)](https://discord.gg/vxecYcWXyf)

State-of-the-art Minecraft stress testing software written in Kotlin.

## Disclaimer

StressCraft should **ONLY** be used in your own server environment. We do not endorse the use of StressCraft for any other purposes than testing your own infrastructure.

Please be aware that attempting to execute this with an external server as a target can be seen as **illegal** as it simulates a layer 7 DoS (denial-of-service) attack, which is against the law in most countries.

## How to use?

> **NOTE:** DO NOT DO THIS IN PRODUCTION, EVER.

- Ensure `max-players` (server.properties) is high enough for the number of bots you're planning to test
- Set `online-mode` (server.properties) to `false`
- Set `network-compression-threshold` (server.properties) to `-1`
- Set `connection-throttle` (bukkit.yml) to `-1`
- Increase `max-joins-per-tick` (paper.yml) to your liking
- Execute the jar with proper arguments

If you're on Velocity, you may also need to set `login-ratelimit` (velocity.toml) to `0`

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
