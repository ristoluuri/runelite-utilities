# RuneLite Automation & Vision Research Toolkit

## Disclaimer:
This repository does not include automation or gameplay-control code.
All files here are for educational and portfolio purposes only — demonstrating vision logic, game object detection, command pipelines, and modular plugin structure.
No content violates RuneLite / Jagex rules.

This project showcases components of a private research project exploring game data parsing, vision-based detection, modular plugin design, and remote command processing.

# The public portion of the project includes:

Vision helpers for locating objects on screen

Example RuneLite plugin scripts (non-interactive / no clicking)

WebSocket command listener example (no game input actions)

Architectural notes & diagrams


# Key Features
Vision Modules (Object Recognition)

Image-processing utilities used to:

Detect game objects via pattern scanning

Identify tile/cloud/object regions

Extract relevant pixel zones efficiently

Calculate coordinates for data logging

Demonstrates: OpenCV + NumPy pipeline logic (or your actual tooling)

# RuneLite Plugin Examples

Lightweight example plugins showing:

Event subscriptions (graphics, tile updates)

Reading game state safely

Overlay debugging

Logging interesting in-game data

Focus on understanding & extending the RuneLite client API

# WebSocket Command Listener

A minimal WebSocket infrastructure module demonstrating:

Async WebSocket client in Python

Command parsing pipeline

Message routing for processing tasks

No mouse control or gameplay code is included.
This demonstrates backend communication flow only.

# Skills Demonstrated
Category	Tech / Concepts
Computer Vision	Pixel scanning, region detection, template matching
Game Client Plugins	RuneLite API, event hooks, overlays
Backend Comms	WebSockets, async commands
Software Design	Modular structure, helpers, plugin system
Security & Ethics	No automation, clean educational extraction