# GameEngine2D

A simple 2D game engine built from scratch in Java using JavaFX — no external libraries.

## Features

- **Game Loop** — `AnimationTimer`-driven loop with delta time
- **Input** — keyboard and mouse listeners with per-frame state tracking (`isKeyDown`, `isKeyJustPressed`)
- **Scene System** — swap between scenes at runtime (`LevelEditorScene`, `LevelScene`)
- **Sprite Rendering** — static sprites and animated spritesheets with an `AssetPool` image cache
- **Animation** — frame-based animation with named state transitions (idle → walk → jump)
- **Camera** — smooth lerp follow camera with FIXED / FOLLOW toggle (`C`)
- **Collision Detection** — AABB and circle colliders, trigger support, per-frame `CollisionWorld` broadphase
- **Audio** — background music and sound effects with independent master / music / SFX volume sliders
- **Attributes & Levelling** — HP, damage, defense, speed — scales on level-up via XP gain
- **Sprite Sheet Inspector** — in-game debug overlay (`J`) to inspect frames and tweak dimensions at runtime

## Requirements

- Java 21+
- Gradle (wrapper included — no separate install needed)

## Running

```bash
./gradlew run        # Linux / macOS
gradlew.bat run      # Windows
```

## Project Structure
app/src/main/
├── java/gameEngine/
│   ├── core/        # Window, GameScene, GameObject, Transform, Camera, KeyListener, MouseListener
│   ├── entitys/     # Entity, Player
│   ├── attributes/  # Attributes (HP, XP, levelling)
│   ├── physics/     # AABBCollider, CircleCollider, CollisionWorld, CollisionResult
│   ├── sprites/     # Sprite, SpriteSheet, Animation, Animator, AssetPool, SpriteSheetPanel
│   ├── audio/       # Sound, Music, AudioManager
│   └── utils/       # Time
└── resources/
└── assets/
├── images/  # Sprite sheets and textures (.png)
└── audio/   # Music and sound effects (.mp3)

## Controls

| Key              | Action                              |
|------------------|-------------------------------------|
| WASD / Arrow keys | Move player                        |
| C                | Toggle camera mode (FIXED / FOLLOW) |
| J                | Toggle sprite sheet inspector       |
| L                | Grant 200 XP (debug)                |

## Tech Stack

Pure Java + JavaFX — no Box2D, no libGDX, no LWJGL.

| System           | JavaFX API                                  |
|------------------|---------------------------------------------|
| Rendering        | `Canvas` / `GraphicsContext`                |
| Game loop        | `AnimationTimer`                            |
| Input            | `KeyEvent`, `MouseEvent`, `ScrollEvent`     |
| Sprites          | `Image`, `WritableImage`, `PixelReader`     |
| Sound effects    | `AudioClip`                                 |
| Background music | `MediaPlayer`                               |
| UI overlays      | `Slider`, `Label`, `VBox`, `StackPane`      |
