# GameEngine2D
A simple 2D game engine built from scratch in Java using JavaFX — no external libraries.

## Features

- **Game Loop** — fixed-timestep loop via `AnimationTimer` with delta time
- **Input** — keyboard and mouse listeners with per-frame state tracking
- **Scene System** — swap between scenes at runtime (`LevelEditorScene`, `LevelScene`, etc.)
- **Sprite Rendering** — static sprites and animated spritesheets
- **Animation** — frame-based animation with state transitions (idle → walk → jump)
- **Camera** — smooth follow camera with FIXED/FOLLOW toggle
- **Asset Pool** — image cache to avoid redundant disk reads
- **Collision Detection** — AABB and circle colliders with trigger support
- **Audio** — background music and sound effects with master/music/SFX volume controls

## Requirements

- Java 21+
- Gradle (wrapper included)

## Running

```bash
./gradlew run        # Linux / Mac
gradlew.bat run      # Windows
```

## Project Structure

```
app/src/main/
├── java/gameEngine/
│   ├── engine/          # Core: Window, GameScene, GameObject, Transform, Camera
│   ├── physics/         # Colliders: AABBCollider, CircleCollider, CollisionWorld
│   ├── sprites/         # Sprite, SpriteSheet, Animation, Animator
│   └── audio/           # Sound, Music, AudioManager
└── resources/
    └── assets/
        ├── images/      # Sprite sheets and textures (.png)
        └── audio/       # Music and sound effects (.mp3)
```

## Controls (LevelEditorScene)

| Key | Action |
|-----|--------|
| WASD / Arrow keys | Move player |
| Space | Jump animation |
| C | Toggle camera mode (FOLLOW / FIXED) |

## Tech Stack

Pure Java + JavaFX only — no Box2D, no libGDX, no LWJGL.

| System | JavaFX API used |
|--------|----------------|
| Rendering | `Canvas` / `GraphicsContext` |
| Game loop | `AnimationTimer` |
| Input | `KeyEvent`, `MouseEvent`, `ScrollEvent` |
| Sprites | `Image`, `WritableImage` |
| Sound effects | `AudioClip` |
| Background music | `MediaPlayer` |
| UI controls | `Slider`, `VBox`, `StackPane` |
