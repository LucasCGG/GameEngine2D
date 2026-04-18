package gameEngine.sprites;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// TODO: import "Entity" package from "game" logic instead of gameEngine 
// import gameEngine.entitys.Entity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Developer overlay panel for inspecting and previewing sprite sheets at
 * runtime.
 * Toggle visibility with the J key.
 *
 * Two registration paths:
 * register(name, sheet) - sheet only, no entity wiring.
 * registerEntity(name, entity, sheet) - full live inspection:
 * W/H + Enter reslices the sheet into new frame dimensions.
 * Click frame freezes that frame on the entity's sprite (click again to
 * resume).
 *
 * Each sheet row also contains an Animation Builder sub-panel where you can:
 * - Click frames to add them to a sequence.
 * - Reorder or remove frames from the sequence.
 * - Set playback speed with a slider.
 * - Preview the sequence playing inside the panel (independent of the entity).
 *
 * Call build(StackPane) once after all registrations, then handleInput() every
 * frame.
 */
public class SpriteSheetPanel {

    /** All registered sheets keyed by display name. */
    private static final Map<String, SpriteSheet> registry = new LinkedHashMap<>();

    /**
     * Current [frameWidth, frameHeight] per registered name. Mutated on W/H
     * refresh.
     */
    private static final Map<String, int[]> frameSizes = new LinkedHashMap<>();

    /** Entities registered via registerEntity, keyed by the same display name. */
    private static final Map<String, Entity> entityRegistry = new LinkedHashMap<>();

    private static StackPane root;
    private static VBox panel;
    private static boolean visible = false;

    // ── Registration API ──────────────────────────────────────────────────────

    /**
     * Registers a sprite sheet for display-only inspection.
     * Frame dimensions are editable but no live entity is updated.
     *
     * @param name  label shown in the panel header
     * @param sheet the sheet to inspect
     */
    public static void register(String name, SpriteSheet sheet) {
        registry.put(name, sheet);
        frameSizes.put(name, new int[] { sheet.getFrameWidth(), sheet.getFrameHeight() });
    }

    /**
     * Registers an entity's sprite sheet for full live inspection.
     * Pressing Enter reslices the sheet; clicking a frame previews it on the
     * entity.
     *
     * @param name   label shown in the panel header
     * @param entity the entity whose sprite receives previews
     * @param sheet  the sprite sheet to inspect
     */
    public static void registerEntity(String name, Entity entity, SpriteSheet sheet) {
        entityRegistry.put(name, entity);
        registry.put(name, sheet);
        frameSizes.put(name, new int[] { sheet.getFrameWidth(), sheet.getFrameHeight() });
    }

    // ── Build ─────────────────────────────────────────────────────────────────

    /**
     * Builds the overlay panel and attaches it to the scene root.
     * Must be called once after all registrations, typically from
     * LevelEditorScene.init().
     *
     * @param sceneRoot the root StackPane to attach the panel to
     */
    public static void build(StackPane sceneRoot) {
        root = sceneRoot;

        panel = new VBox(14);
        panel.setPadding(new Insets(18));
        panel.setMaxWidth(720);
        panel.setMaxHeight(580);
        panel.setStyle(
                "-fx-background-color: rgba(10,10,20,0.93);"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: rgba(120,120,255,0.4);"
                        + "-fx-border-width: 1;"
                        + "-fx-border-radius: 12;");

        Label title = new Label("◈  SPRITE SHEET INSPECTOR");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
        title.setTextFill(Color.web("#a0a8ff"));

        Label hint = new Label(
                "J = close   ·   W/H + ↵ → reslice   ·   click frame → preview on entity   ·   ▶ ANIM to build sequence");
        hint.setFont(Font.font("Monospace", 10));
        hint.setTextFill(Color.web("#cbcbd6"));

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setPrefHeight(500);

        VBox content = new VBox(20);
        content.setPadding(new Insets(4, 0, 4, 0));

        for (Map.Entry<String, SpriteSheet> entry : registry.entrySet()) {
            String key = entry.getKey();
            Entity entity = entityRegistry.get(key);
            content.getChildren().add(sheetRow(key, entry.getValue(), entity));
        }

        scroll.setContent(content);
        panel.getChildren().addAll(title, hint, new Separator(), scroll);
        StackPane.setAlignment(panel, Pos.CENTER);
        panel.setVisible(false);
        sceneRoot.getChildren().add(panel);
    }

    /**
     * Builds one labeled row for a registered sheet, containing the name header,
     * W/H input fields, the frame grid, and the animation builder sub-panel.
     *
     * @param name         display name for this sheet
     * @param initialSheet the sheet to display on first build
     * @param entity       the entity wired to this sheet, or null for display-only
     */
    private static VBox sheetRow(String name, SpriteSheet initialSheet, Entity entity) {
        VBox row = new VBox(8);

        // ── Collapse-toggle header ────────────────────────────────────────────────
        String entityTag = (entity != null) ? "  [entity: " + entity.name + "]" : "";
        Label nameLabel = new Label("▸  " + name + entityTag);
        nameLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        nameLabel.setTextFill(entity != null ? Color.web("#90ffb0") : Color.web("#e8e8ff"));
        nameLabel.setCursor(javafx.scene.Cursor.HAND);

        VBox rowBody = new VBox(8);
        rowBody.setVisible(false);
        rowBody.setManaged(false);

        nameLabel.setOnMouseClicked(e -> {
            boolean open = !rowBody.isVisible();
            rowBody.setVisible(open);
            rowBody.setManaged(open);
            String tag = (entity != null) ? "  [entity: " + entity.name + "]" : "";
            nameLabel.setText((open ? "▾" : "▸") + "  " + name + tag);
        });

        // ── Rest of setup (unchanged) ─────────────────────────────────────────────
        int[] size = frameSizes.get(name);
        Label wLabel = styledLabel("W:");
        TextField wField = sizedTextField(String.valueOf(size[0]));
        Label hLabel = styledLabel("H:");
        TextField hField = sizedTextField(String.valueOf(size[1]));

        SpriteSheet[] sheetRef = { initialSheet };
        StackPane[] selectedCellRef = { null };
        Region[] selectedBorderRef = { null };
        List<WritableImage> animSequence = new ArrayList<>();

        TilePane frameGrid = buildFrameGrid(
                sheetRef[0], size[0], size[1], entity,
                selectedCellRef, selectedBorderRef, animSequence);

        VBox animPanel = buildAnimPanel(animSequence, size, sheetRef);

        rowBody.getChildren().addAll(
                buildControlsRow(wLabel, wField, hLabel, hField),
                frameGrid,
                animPanel);

        row.getChildren().addAll(nameLabel, rowBody);

        Runnable refresh = () -> {
            try {
                int w = Math.max(1, Integer.parseInt(wField.getText().trim()));
                int h = Math.max(1, Integer.parseInt(hField.getText().trim()));
                size[0] = w;
                size[1] = h;

                SpriteSheet newSheet = new SpriteSheet(sheetRef[0].getSourcePath(), w, h);
                sheetRef[0] = newSheet;
                registry.put(name, newSheet);

                if (entity != null)
                    entity.getSprite().previewFrame(null);
                selectedCellRef[0] = null;
                selectedBorderRef[0] = null;

                TilePane newGrid = buildFrameGrid(
                        newSheet, w, h, entity,
                        selectedCellRef, selectedBorderRef, animSequence);

                rowBody.getChildren().remove(rowBody.getChildren().size() - 2);
                rowBody.getChildren().add(rowBody.getChildren().size() - 1, newGrid);

            } catch (NumberFormatException ignored) {
            }
        };

        wField.setOnAction(e -> refresh.run());
        hField.setOnAction(e -> refresh.run());

        return row;
    }

    private static HBox buildControlsRow(
            Label wLabel, TextField wField,
            Label hLabel, TextField hField) {

        HBox controls = new HBox(6,
                new Region() {
                    {
                        HBox.setHgrow(this, Priority.ALWAYS);
                    }
                },
                wLabel, wField, hLabel, hField);
        controls.setAlignment(Pos.CENTER_LEFT);
        return controls;
    }

    // ── Animation builder panel ───────────────────────────────────────────────

    /**
     * Builds the animation builder sub-panel for a sheet row.
     * Contains a collapse toggle, a sequence strip, speed slider, and a preview
     * ImageView.
     *
     * The sequence is populated when the user clicks a frame cell in the grid while
     * holding no special key — that adds it to the end of the sequence.
     * Frames in the sequence strip can be removed by clicking the × button on each.
     * Playback runs entirely inside the panel via a JavaFX Timeline.
     *
     * @param animSequence shared mutable list of frames; populated by the frame
     *                     grid
     * @param size         shared [fw, fh] array used to read current frame
     *                     dimensions
     */
    private static VBox buildAnimPanel(List<WritableImage> animSequence, int[] size, SpriteSheet[] sheetRef) {
        Label header = new Label("▸  ANIMATION BUILDER");
        header.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        header.setTextFill(Color.web("#c0b8ff"));
        header.setCursor(javafx.scene.Cursor.HAND);

        VBox body = new VBox(8);
        body.setVisible(false);
        body.setManaged(false);

        header.setOnMouseClicked(e -> {
            boolean open = !body.isVisible();
            body.setVisible(open);
            body.setManaged(open);
            header.setText((open ? "▾" : "▸") + "  ANIMATION BUILDER");
        });

        // ── Preview ImageView ─────────────────────────────────────────────────────
        ImageView preview = new ImageView();
        preview.setFitWidth(80);
        preview.setFitHeight(80);
        preview.setPreserveRatio(true);
        preview.setStyle("-fx-effect: dropshadow(gaussian, rgba(80,80,255,0.3), 6, 0, 0, 1);");
        Label previewLabel = styledLabel("preview");
        VBox previewBox = new VBox(4, previewLabel, preview);
        previewBox.setAlignment(Pos.CENTER);

        // ── Sequence strip ────────────────────────────────────────────────────────
        HBox strip = new HBox(4);
        strip.setStyle(
                "-fx-background-color: rgba(0,0,0,0.3);"
                        + "-fx-background-radius: 6;"
                        + "-fx-padding: 6;");
        strip.setMinHeight(60);

        Label emptyHint = styledLabel("← shift+click frames above to add them here");
        emptyHint.setFont(Font.font("Monospace", 10));
        strip.getChildren().add(emptyHint);
        Label nameLabel = styledLabel("Name:");
        TextField nameField = sizedTextField("preview");
        nameField.setPrefWidth(120);

        HBox nameRow = new HBox(8, nameLabel, nameField);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        ScrollPane stripScroll = new ScrollPane(strip);
        stripScroll.setFitToHeight(true);
        stripScroll.setPrefHeight(80);
        stripScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // ── Animator + AnimationTimer — uses the real engine classes ─────────────
        // The Animator holds one Animation named "preview" built from animSequence.
        // AnimationTimer drives it with real nanosecond deltaTime, same as the game
        // loop.
        float[] frameDurationRef = { 0.1f };
        boolean[] loopRef = { true };
        Animator[] animatorRef = { null };
        javafx.animation.AnimationTimer[] timerRef = { null };

        Runnable rebuildAnimator = () -> {
            if (timerRef[0] != null) {
                timerRef[0].stop();
                timerRef[0] = null;
            }
            animatorRef[0] = null;
            preview.setImage(null);

            if (animSequence.isEmpty())
                return;

            // Wrap the WritableImage list into a minimal SpriteSheet-like source.
            // Animation requires a SpriteSheet, so we build a throwaway one-frame-each
            // sheet per image — but that's heavy. Instead, we construct Animation
            // directly via its cachedFrames by subclassing with a factory method.
            // Since Animation caches frames from the sheet at construction time,
            // we can feed it a synthetic sheet that returns our WritableImages by index.
            WritableImage[] frames = animSequence.toArray(new WritableImage[0]);
            int[] indices = new int[frames.length];
            for (int i = 0; i < indices.length; i++)
                indices[i] = i;

            SpriteSheet syntheticSheet = new SpriteSheet(null, 1, 1) {
                @Override
                public WritableImage getFrame(int index) {
                    if (index < 0 || index >= frames.length)
                        return null;
                    return frames[index];
                }

                @Override
                public int getTotalFrames() {
                    return frames.length;
                }
            };

            // frameDuration is driven by the speed slider — stored in a wrapper so
            // the AnimationTimer lambda can read the latest value
            String animName = nameField.getText().trim().isEmpty() ? "preview" : nameField.getText().trim();
            Animation anim = new Animation(animName, syntheticSheet, indices, frameDurationRef[0], true);

            Animator animator = new Animator();
            animator.addAnimation(anim);
            animator.play(animName);

            animatorRef[0] = animator;

            // AnimationTimer gives nanoTime; we compute deltaTime in seconds
            long[] lastTime = { -1L };
            timerRef[0] = new javafx.animation.AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (lastTime[0] < 0) {
                        lastTime[0] = now;
                        return;
                    }
                    float deltaTime = (now - lastTime[0]) / 1_000_000_000f;
                    lastTime[0] = now;
                    animatorRef[0].update(deltaTime);
                    WritableImage frame = animatorRef[0].getCurrentFrame();
                    if (frame != null)
                        preview.setImage(frame);
                }
            };
            // Don't auto-start — user presses Play
        };

        // ── Speed slider ──────────────────────────────────────────────────────────
        // frameDuration = 1/fps — we need to reach into the running Animation to
        // update it. We expose a setter via a wrapper so the slider can call it.

        Label speedLabel = styledLabel("Speed (duration for each frame):  10");
        Slider speedSlider = new Slider(0.01, 1.0, 0.1); // 0.01s (fast) → 1.0s (slow) per frame
        speedSlider.setPrefWidth(200);
        speedSlider.setStyle("-fx-control-inner-background: rgba(30,30,60,0.9);");

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            frameDurationRef[0] = (float) newVal.doubleValue();
            speedLabel.setText(String.format("Frame duration:  %.2fs", frameDurationRef[0]));
            if (!animSequence.isEmpty() && animatorRef[0] != null) {
                boolean wasRunning = timerRef[0] != null;
                rebuildAnimator.run();
                if (wasRunning)
                    timerRef[0].start();
            }
        });

        // ── Sequence strip rebuild ────────────────────────────────────────────────
        Runnable[] rebuildStrip = { null };
        rebuildStrip[0] = () -> {
            strip.getChildren().clear();

            if (animSequence.isEmpty()) {
                strip.getChildren().add(emptyHint);
                if (timerRef[0] != null) {
                    timerRef[0].stop();
                    timerRef[0] = null;
                }
                animatorRef[0] = null;
                preview.setImage(null);
                return;
            }

            for (int i = 0; i < animSequence.size(); i++) {
                final int idx = i;
                WritableImage img = animSequence.get(i);

                ImageView thumb = new ImageView(img);
                thumb.setFitWidth(40);
                thumb.setFitHeight(40);
                thumb.setPreserveRatio(true);

                Label badge = new Label(String.valueOf(i));
                badge.setFont(Font.font("Monospace", FontWeight.BOLD, 9));
                badge.setTextFill(Color.WHITE);
                badge.setStyle(
                        "-fx-background-color: rgba(0,0,0,0.6); -fx-padding: 1 2 1 2; -fx-background-radius: 2;");
                StackPane.setAlignment(badge, Pos.TOP_LEFT);

                Button removeBtn = new Button("×");
                removeBtn.setFont(Font.font("Monospace", FontWeight.BOLD, 9));
                removeBtn.setStyle(
                        "-fx-background-color: rgba(200,60,60,0.8);"
                                + "-fx-text-fill: white;"
                                + "-fx-padding: 0 3 0 3;"
                                + "-fx-background-radius: 2;"
                                + "-fx-cursor: hand;");
                removeBtn.setOnAction(e -> {
                    boolean wasRunning = timerRef[0] != null;
                    if (timerRef[0] != null) {
                        timerRef[0].stop();
                        timerRef[0] = null;
                    }
                    animSequence.remove(idx);
                    rebuildStrip[0].run();
                    rebuildAnimator.run();
                    if (wasRunning && !animSequence.isEmpty())
                        timerRef[0].start();
                });
                StackPane.setAlignment(removeBtn, Pos.TOP_RIGHT);

                StackPane cell = new StackPane(thumb, badge, removeBtn);
                cell.setStyle(
                        "-fx-border-color: rgba(150,150,255,0.4);"
                                + "-fx-border-width: 1;"
                                + "-fx-border-radius: 4;");
                cell.setPrefSize(44, 44);
                strip.getChildren().add(cell);
            }

            rebuildAnimator.run();
        };

        // ── Buttons ───────────────────────────────────────────────────────────────
        Button playBtn = new Button("▶  Play");
        Button stopBtn = new Button("■  Stop");
        Button clearBtn = new Button("✕  Clear");
        Button loopBtn = new Button("⟳  Loop");
        Button copyBtn = new Button("Copy as Code");

        for (Button b : new Button[] { playBtn, stopBtn, clearBtn, copyBtn }) {
            b.setFont(Font.font("Monospace", 11));
            b.setStyle(
                    "-fx-background-color: rgba(40,40,80,0.9);"
                            + "-fx-text-fill: #c8c8ff;"
                            + "-fx-border-color: rgba(100,100,200,0.4);"
                            + "-fx-border-radius: 4;"
                            + "-fx-background-radius: 4;"
                            + "-fx-cursor: hand;");
        }

        loopBtn.setFont(Font.font("Monospace", 11));
        loopBtn.setStyle(
                "-fx-background-color: rgba(40,80,40,0.9);"
                        + "-fx-text-fill: #80ff80;"
                        + "-fx-border-color: rgba(60,180,60,0.4);"
                        + "-fx-border-radius: 4;"
                        + "-fx-background-radius: 4;"
                        + "-fx-cursor: hand;");

        playBtn.setOnAction(e -> {
            if (animSequence.isEmpty())
                return;
            rebuildAnimator.run();
            timerRef[0].start();
        });

        stopBtn.setOnAction(e -> {
            if (timerRef[0] != null) {
                timerRef[0].stop();
                timerRef[0] = null;
            }
        });

        clearBtn.setOnAction(e -> {
            if (timerRef[0] != null) {
                timerRef[0].stop();
                timerRef[0] = null;
            }
            animSequence.clear();
            rebuildStrip[0].run();
        });

        copyBtn.setOnAction(e -> {
            if (animSequence.isEmpty())
                return;

            String animName = nameField.getText().trim().isEmpty() ? "preview" : nameField.getText().trim();

            StringBuilder indicesStr = new StringBuilder("{ ");
            for (int i = 0; i < animSequence.size(); i++) {
                indicesStr.append(i);
                if (i < animSequence.size() - 1)
                    indicesStr.append(", ");
            }
            indicesStr.append(" }");

            SpriteSheet currentSheet = sheetRef[0];
            String sheetArg = String.format(
                    "new SpriteSheet(\"%s\", %d, %d)",
                    currentSheet.getSourcePath(),
                    currentSheet.getFrameWidth(),
                    currentSheet.getFrameHeight());

            String code = String.format(
                    "Animation %s = new Animation(\"%s\", %s, new int[] %s, %.2ff, %b);",
                    animName, animName, sheetArg, indicesStr, frameDurationRef[0], loopRef[0]);

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(code);
            clipboard.setContent(content);

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(1.5));
            copyBtn.setText("✓  Copied!");
            copyBtn.setStyle(
                    "-fx-background-color: rgba(40,100,40,0.9);"
                            + "-fx-text-fill: #80ff80;"
                            + "-fx-border-color: rgba(60,180,60,0.4);"
                            + "-fx-border-radius: 4;"
                            + "-fx-background-radius: 4;"
                            + "-fx-cursor: hand;");
            pause.setOnFinished(ev -> {
                copyBtn.setText("⧉  Copy");
                copyBtn.setStyle(
                        "-fx-background-color: rgba(40,40,80,0.9);"
                                + "-fx-text-fill: #c8c8ff;"
                                + "-fx-border-color: rgba(100,100,200,0.4);"
                                + "-fx-border-radius: 4;"
                                + "-fx-background-radius: 4;"
                                + "-fx-cursor: hand;");
            });
            pause.play();
        });

        loopBtn.setOnAction(e -> {
            loopRef[0] = !loopRef[0];
            if (loopRef[0]) {
                loopBtn.setText("⟳  Loop: ON");
                loopBtn.setStyle(
                        "-fx-background-color: rgba(40,80,40,0.9);"
                                + "-fx-text-fill: #80ff80;"
                                + "-fx-border-color: rgba(60,180,60,0.4);"
                                + "-fx-border-radius: 4;"
                                + "-fx-background-radius: 4;"
                                + "-fx-cursor: hand;");
            } else {
                loopBtn.setText("⟳  Loop: OFF");
                loopBtn.setStyle(
                        "-fx-background-color: rgba(80,40,40,0.9);"
                                + "-fx-text-fill: #ff8080;"
                                + "-fx-border-color: rgba(180,60,60,0.4);"
                                + "-fx-border-radius: 4;"
                                + "-fx-background-radius: 4;"
                                + "-fx-cursor: hand;");
            }

            // Rebuild with updated loop value — restart if already playing
            if (!animSequence.isEmpty() && animatorRef[0] != null) {
                boolean wasRunning = timerRef[0] != null;
                rebuildAnimator.run();
                if (wasRunning)
                    timerRef[0].start();
            }
        });

        HBox buttons = new HBox(6, playBtn, stopBtn, clearBtn, copyBtn, loopBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);
        HBox speedRow = new HBox(8, speedLabel, speedSlider);
        speedRow.setAlignment(Pos.CENTER_LEFT);
        HBox previewRow = new HBox(12, previewBox, new VBox(6, speedRow, buttons));
        previewRow.setAlignment(Pos.CENTER_LEFT);

        Label stripHint = styledLabel("Sequence  (shift+click frames above to append):");
        stripHint.setFont(Font.font("Monospace", 10));

        body.getChildren().addAll(new Separator(), nameRow, stripHint, stripScroll, previewRow);
        body.setUserData(rebuildStrip);

        VBox wrapper = new VBox(4, header, body);
        wrapper.setStyle(
                "-fx-background-color: rgba(20,18,40,0.6);"
                        + "-fx-background-radius: 8;"
                        + "-fx-padding: 8;");
        return wrapper;
    }

    /**
     * Builds a TilePane of thumbnail cells, one per frame in the sheet.
     * Frames are numbered 0 from top-left, increasing left-to-right then
     * top-to-bottom.
     *
     * Left-click on a cell (entity registered):
     * First click — freezes that frame on the entity via previewFrame().
     * Second click — clears the preview and resumes normal animation.
     *
     * Shift+click on a cell appends the frame to the animation sequence strip.
     *
     * @param sheet             the sheet to extract frames from
     * @param frameWidth        frame width used for thumbnail sizing
     * @param frameHeight       frame height used for thumbnail sizing
     * @param entity            entity to preview frames on, or null for
     *                          display-only
     * @param selectedCellRef   single-element array holding the currently selected
     *                          cell
     * @param selectedBorderRef single-element array holding the selected cell's
     *                          border Region
     * @param animSequence      shared mutable list; Shift+click appends to this
     */
    private static TilePane buildFrameGrid(
            SpriteSheet sheet, int frameWidth, int frameHeight,
            Entity entity,
            StackPane[] selectedCellRef,
            Region[] selectedBorderRef,
            List<WritableImage> animSequence) {

        TilePane grid = new TilePane(4, 4);
        grid.setPrefTileWidth(frameWidth > 0 ? Math.min(frameWidth, 80) : 80);
        grid.setPrefTileHeight(frameHeight > 0 ? Math.min(frameHeight, 80) : 80);

        grid.setUserData("frameGrid");

        int totalFrames = sheet.getTotalFrames();

        for (int i = 0; i < totalFrames; i++) {
            WritableImage frame = sheet.getFrame(i);
            if (frame == null)
                continue;

            ImageView iv = new ImageView(frame);
            iv.setPreserveRatio(true);
            iv.setFitWidth(Math.min(frameWidth, 80));
            iv.setFitHeight(Math.min(frameHeight, 80));
            iv.setStyle("-fx-effect: dropshadow(gaussian, rgba(80,80,255,0.25), 4, 0, 0, 1);");

            Label indexLabel = new Label(String.valueOf(i));
            indexLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
            indexLabel.setTextFill(Color.web("#ffffff"));
            indexLabel.setStyle(
                    "-fx-background-color: rgba(0,0,0,0.55);"
                            + "-fx-padding: 1 3 1 3;"
                            + "-fx-background-radius: 3;");
            indexLabel.setVisible(false);
            StackPane.setAlignment(indexLabel, Pos.TOP_LEFT);

            String tipText = "Frame " + i + "  (" + frameWidth + "×" + frameHeight + ")"
                    + (entity != null ? "\nClick → preview on " + entity.name : "")
                    + "\nShift+Click → append to animation";
            Tooltip tip = new Tooltip(tipText);
            tip.setStyle("-fx-font-family: monospace; -fx-font-size: 13px;");
            Tooltip.install(iv, tip);

            Region border = makeBorderOverlay();
            StackPane cell = new StackPane(iv, indexLabel, border);

            cell.setOnMouseEntered(e -> {
                if (cell != selectedCellRef[0])
                    applyHoverBorder(border);
                indexLabel.setVisible(true);
            });
            cell.setOnMouseExited(e -> {
                if (cell != selectedCellRef[0])
                    applyIdleBorder(border);
                indexLabel.setVisible(false);
            });

            cell.setOnMouseClicked(e -> {
                if (e.isShiftDown()) {
                    animSequence.add(frame);
                    javafx.scene.Node parent = grid.getParent();
                    if (parent instanceof VBox rowVBox) {
                        for (javafx.scene.Node child : rowVBox.getChildren()) {
                            if (child instanceof VBox wrapper) {
                                for (javafx.scene.Node wChild : wrapper.getChildren()) {
                                    if (wChild instanceof VBox body && body.getUserData() instanceof Runnable[] rs) {
                                        rs[0].run();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else if (entity != null) {
                    if (selectedCellRef[0] == cell) {
                        entity.getSprite().previewFrame(null);
                        applyIdleBorder(border);
                        selectedCellRef[0] = null;
                        selectedBorderRef[0] = null;
                    } else {
                        if (selectedBorderRef[0] != null)
                            applyIdleBorder(selectedBorderRef[0]);
                        entity.getSprite().previewFrame(frame);
                        applySelectedBorder(border);
                        selectedCellRef[0] = cell;
                        selectedBorderRef[0] = border;
                    }
                }
            });

            grid.getChildren().add(cell);
        }

        if (grid.getChildren().isEmpty()) {
            Label empty = styledLabel("(no frames — check frame size)");
            empty.setTextFill(Color.web("#884444"));
            grid.getChildren().add(empty);
        }

        return grid;
    }

    // ── Cell border helpers ───────────────────────────────────────────────────

    /** Idle state: faint dashed blue border. */
    private static final String IDLE_BORDER = "-fx-border-color: #97c7fd96;"
            + "-fx-border-width: 1;"
            + "-fx-border-style: segments(3, 8) line-cap round;";

    /** Hover state: brighter dashed blue border. */
    private static final String HOVER_BORDER = "-fx-border-color: #7080ff;"
            + "-fx-border-width: 1;"
            + "-fx-border-style: segments(3, 8) line-cap round;";

    /** Selected state: solid green border — only one cell at a time. */
    private static final String SELECTED_BORDER = "-fx-border-color: #40ff80;"
            + "-fx-border-width: 2;"
            + "-fx-border-style: solid;";

    /**
     * Creates a transparent Region that overlays a cell with the idle dashed
     * border.
     * Mouse-transparent so all input events pass through to the cell beneath.
     */
    private static Region makeBorderOverlay() {
        Region region = new Region();
        region.setMouseTransparent(true);
        region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        region.setStyle(IDLE_BORDER);
        return region;
    }

    /** Resets a border overlay to the idle dashed style. */
    private static void applyIdleBorder(Region border) {
        border.setStyle(IDLE_BORDER);
    }

    /** Applies the hover dashed style to a border overlay. */
    private static void applyHoverBorder(Region border) {
        border.setStyle(HOVER_BORDER);
    }

    /** Applies the selected solid green style to a border overlay. */
    private static void applySelectedBorder(Region border) {
        border.setStyle(SELECTED_BORDER);
    }

    // ── Controls row ──────────────────────────────────────────────────────────

    /**
     * Builds the HBox row containing the sheet name label and W/H text fields.
     *
     * @param nameLabel label showing the sheet/entity name
     * @param wLabel    "W:" label
     * @param wField    frame width input field
     * @param hLabel    "H:" label
     * @param hField    frame height input field
     */
    private static HBox buildControlsRow(
            Label nameLabel,
            Label wLabel, TextField wField,
            Label hLabel, TextField hField) {

        HBox controls = new HBox(6, nameLabel,
                new Region() {
                    {
                        HBox.setHgrow(this, Priority.ALWAYS);
                    }
                },
                wLabel, wField, hLabel, hField);
        controls.setAlignment(Pos.CENTER_LEFT);
        return controls;
    }

    // ── Input / toggle ────────────────────────────────────────────────────────

    /**
     * Checks for the J key and toggles the panel. Call every frame from
     * LevelEditorScene.update().
     *
     * @param keys the active KeyListener
     */
    public static void handleInput(gameEngine.core.KeyListener keys) {
        if (keys.isKeyJustPressed(javafx.scene.input.KeyCode.J)) {
            toggle();
        }
    }

    /** Toggles the panel between visible and hidden. */
    public static void toggle() {
        visible = !visible;
        panel.setVisible(visible);
    }

    // ── Style helpers ─────────────────────────────────────────────────────────

    /** Creates a muted monospace label used for field prefixes and hints. */
    private static Label styledLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospace", 12));
        label.setTextFill(Color.web("#ffffff"));
        return label;
    }

    /** Creates a compact dark-themed text field pre-filled with val. */
    private static TextField sizedTextField(String val) {
        TextField textField = new TextField(val);
        textField.setPrefWidth(54);
        textField.setStyle(
                "-fx-background-color: rgba(30,30,60,0.9);"
                        + "-fx-text-fill: #c8c8ff;"
                        + "-fx-border-color: rgba(100,100,200,0.4);"
                        + "-fx-border-radius: 4;"
                        + "-fx-background-radius: 4;"
                        + "-fx-font-family: monospace;"
                        + "-fx-font-size: 12px;");
        return textField;
    }
}