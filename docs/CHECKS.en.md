# Implemented checks

Five observe-only checks are registered. Each adds evidence to an independent violation level (VL); none cancels actions or punishes players.

| Check | Evidence rule | Main limitation |
|---|---|---|
| Speed | Horizontal distance over a 45–250 ms window exceeds 0.85 blocks per 50 ms | Event-based, not full movement prediction |
| Fly | Ten airborne samples with total vertical change within 0.03 blocks | Special collision or custom physics can resemble hovering |
| Reach | Attacker eye to closest point of target player bounding box exceeds 4.5 blocks | No target position history or lag compensation |
| AutoClicker | At least 22 distinct attack-damage events in 1000 ms | Measures successful damage events, not all clicks |
| FastPlace | Successful block placements 1–54 ms apart | Legitimate rapid building can trigger evidence |

High ping, low TPS, game modes, flight, vehicles, gliding, swimming, selected potion effects, non-default walk speed, teleport/respawn/velocity grace, and special terrain suppress the relevant checks. VL decays over time and staff alerts are throttled per player and check. Configure only `enabled`, `weight`, `decay`, and `alert-threshold` for each implemented check. See the [detailed Russian reference](CHECKS.md) and [limitations](KNOWN_LIMITATIONS.md). Packet checks and the larger roadmap are not implemented.
