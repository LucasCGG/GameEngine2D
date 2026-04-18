package gameEngine.sprites;

import java.util.LinkedHashMap;
import java.util.Map;

import gameEngine.entitys.Entity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
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
     * The sheet's frame dimensions are shown in the W/H fields.
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
        panel.setMaxWidth(680);
        panel.setMaxHeight(540);
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
                "J = close   ·   W/H + ↵ → reslice sheet   ·   click frame → preview on entity");
        hint.setFont(Font.font("Monospace", 10));
        hint.setTextFill(Color.web("#555577"));

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setPrefHeight(460);

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
     * W/H input fields, and the initial frame grid.
     *
     * @param name         display name for this sheet
     * @param initialSheet the sheet to display on first build
     * @param entity       the entity wired to this sheet, or null for display-only
     */
    private static VBox sheetRow(String name, SpriteSheet initialSheet, Entity entity) {
        VBox row = new VBox(8);

        String entityTag = (entity != null) ? "  [entity: " + entity.name + "]" : "";
        Label nameLabel = new Label("▸  " + name + entityTag);
        nameLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        nameLabel.setTextFill(entity != null ? Color.web("#90ffb0") : Color.web("#e8e8ff"));

        int[] size = frameSizes.get(name);
        Label wLabel = styledLabel("W:");
        TextField wField = sizedTextField(String.valueOf(size[0]));
        Label hLabel = styledLabel("H:");
        TextField hField = sizedTextField(String.valueOf(size[1]));

        SpriteSheet[] sheetRef = { initialSheet };

        StackPane[] selectedCellRef = { null };
        Region[] selectedBorderRef = { null };

        TilePane frameGrid = buildFrameGrid(
                sheetRef[0], size[0], size[1], entity, selectedCellRef, selectedBorderRef);
        row.getChildren().addAll(
                buildControlsRow(nameLabel, wLabel, wField, hLabel, hField), frameGrid);

        Runnable refresh = () -> {
            try {
                int w = Math.max(1, Integer.parseInt(wField.getText().trim()));
                int h = Math.max(1, Integer.parseInt(hField.getText().trim()));
                size[0] = w;
                size[1] = h;

                SpriteSheet newSheet = new SpriteSheet(sheetRef[0].getSourcePath(), w, h);
                sheetRef[0] = newSheet;
                registry.put(name, newSheet);

                if (entity != null) {
                    entity.getSprite().previewFrame(null);
                }
                selectedCellRef[0] = null;
                selectedBorderRef[0] = null;

                TilePane newGrid = buildFrameGrid(
                        newSheet, w, h, entity, selectedCellRef, selectedBorderRef);
                row.getChildren().remove(row.getChildren().size() - 1);
                row.getChildren().add(newGrid);

            } catch (NumberFormatException ignored) {
            }
        };

        wField.setOnAction(e -> refresh.run());
        hField.setOnAction(e -> refresh.run());

        return row;
    }

    /**
     * Builds a TilePane of thumbnail cells, one per frame in the sheet.
     * Frames are numbered 0 from top-left, increasing left-to-right then
     * top-to-bottom.
     * Hovering a cell shows its index; clicking it (when entity is set) previews it
     * live.
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
     */
    private static TilePane buildFrameGrid(
            SpriteSheet sheet, int frameWidth, int frameHeight,
            Entity entity,
            StackPane[] selectedCellRef,
            Region[] selectedBorderRef) {

        TilePane grid = new TilePane(4, 4);
        grid.setPrefTileWidth(frameWidth > 0 ? Math.min(frameWidth, 80) : 80);
        grid.setPrefTileHeight(frameHeight > 0 ? Math.min(frameHeight, 80) : 80);

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
                    + (entity != null ? "\nClick to preview on " + entity.name : "");
            Tooltip tip = new Tooltip(tipText);
            tip.setStyle("-fx-font-family: monospace; -fx-font-size: 16px;");
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

            if (entity != null) {
                cell.setOnMouseClicked(e -> {
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
                });
            }

            grid.getChildren().add(cell);
        }

        if (grid.getChildren().isEmpty()) {
            Label empty = styledLabel("(no frames — check frame size)");
            empty.setTextFill(Color.web("#884444"));
            grid.getChildren().add(empty);
        }

        return grid;
    }

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

    /**
     * Creates a muted monospace label used for field prefixes like "W:" and "H:".
     */
    private static Label styledLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Monospace", 12));
        l.setTextFill(Color.web("#8888bb"));
        return l;
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