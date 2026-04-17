package gameEngine.sprites;

import java.util.LinkedHashMap;
import java.util.Map;

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
 * Overlay panel (toggle with J) that displays every registered SpriteSheet,
 * lets you inspect individual frames, and change frame-width / frame-height
 * per sheet at runtime.
 *
 * Usage – register sheets via SpriteSheetPanel.register(name, sheet)
 * and call SpriteSheetPanel.build() once from LevelEditorScene.init().
 * In LevelEditorScene.update() call SpriteSheetPanel.handleInput().
 */
public class SpriteSheetPanel {

    // ---------------------------------------------------------------
    // Registry
    // ---------------------------------------------------------------

    /** All sheets registered for display, keyed by a human-readable label. */
    private static final Map<String, SpriteSheet> registry = new LinkedHashMap<>();

    /**
     * Live frame dimensions per sheet (separate from the SpriteSheet object so
     * we can preview different sizes without mutating the running animation).
     */
    private static final Map<String, int[]> frameSizes = new LinkedHashMap<>();

    private static StackPane root;
    private static VBox panel;
    private static boolean visible = false;

    /** Register a sprite sheet so it appears in the panel. */
    public static void register(String name, SpriteSheet sheet) {
        registry.put(name, sheet);
        frameSizes.put(name, new int[] { sheet.getFrameWidth(), sheet.getFrameHeight() });
    }

    // ---------------------------------------------------------------
    // Build the panel once and attach it to the scene root
    // ---------------------------------------------------------------

    public static void build(StackPane sceneRoot) {
        root = sceneRoot;

        panel = new VBox(14);
        panel.setPadding(new Insets(18));
        panel.setMaxWidth(680);
        panel.setMaxHeight(540);
        panel.setStyle(
                "-fx-background-color: rgba(10,10,20,0.93);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(120,120,255,0.4);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;");

        Label title = new Label("◈  SPRITE SHEET INSPECTOR");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
        title.setTextFill(Color.web("#a0a8ff"));

        Label hint = new Label("press  J  to close   ·   edit W / H then hit ↵ to refresh frames");
        hint.setFont(Font.font("Monospace", 10));
        hint.setTextFill(Color.web("#555577"));

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setPrefHeight(460);

        VBox content = new VBox(20);
        content.setPadding(new Insets(4, 0, 4, 0));

        for (Map.Entry<String, SpriteSheet> entry : registry.entrySet()) {
            content.getChildren().add(sheetRow(entry.getKey(), entry.getValue(), content));
        }

        scroll.setContent(content);
        panel.getChildren().addAll(title, hint, new Separator(), scroll);

        StackPane.setAlignment(panel, Pos.CENTER);
        panel.setVisible(false);
        sceneRoot.getChildren().add(panel);
    }

    // ---------------------------------------------------------------
    // Per-sheet row
    // ---------------------------------------------------------------

    private static VBox sheetRow(String name, SpriteSheet sheet, VBox parentContent) {
        VBox row = new VBox(8);

        // --- sheet name header ---
        Label nameLabel = new Label("▸  " + name);
        nameLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        nameLabel.setTextFill(Color.web("#e8e8ff"));

        // --- size controls ---
        int[] size = frameSizes.get(name);

        Label wLabel = styledLabel("W:");
        TextField wField = sizedTextField(String.valueOf(size[0]));

        Label hLabel = styledLabel("H:");
        TextField hField = sizedTextField(String.valueOf(size[1]));

        // Frame grid — rebuilt whenever W or H change
        TilePane frameGrid = buildFrameGrid(sheet, size[0], size[1]);

        // Listener: pressing Enter in either field refreshes the grid
        Runnable refresh = () -> {
            try {
                int w = Math.max(1, Integer.parseInt(wField.getText().trim()));
                int h = Math.max(1, Integer.parseInt(hField.getText().trim()));
                size[0] = w;
                size[1] = h;

                // Rebuild sheet with new frame dimensions and refresh grid
                SpriteSheet newSheet = new SpriteSheet(sheet.getSourcePath(), w, h);
                registry.put(name, newSheet);

                TilePane newGrid = buildFrameGrid(newSheet, w, h);
                // Replace old grid in the row (last child)
                row.getChildren().remove(row.getChildren().size() - 1);
                row.getChildren().add(newGrid);
            } catch (NumberFormatException ignored) {
            }
        };

        wField.setOnAction(e -> refresh.run());
        hField.setOnAction(e -> refresh.run());

        HBox controls = new HBox(6, nameLabel,
                new Region() {
                    {
                        HBox.setHgrow(this, Priority.ALWAYS);
                    }
                },
                wLabel, wField, hLabel, hField);
        controls.setAlignment(Pos.CENTER_LEFT);

        row.getChildren().addAll(controls, frameGrid);
        return row;
    }

    // ---------------------------------------------------------------
    // Frame grid
    // ---------------------------------------------------------------

    private static TilePane buildFrameGrid(SpriteSheet sheet, int fw, int fh) {
        TilePane grid = new TilePane(4, 4);
        grid.setPrefTileWidth(fw > 0 ? Math.min(fw, 80) : 80);
        grid.setPrefTileHeight(fh > 0 ? Math.min(fh, 80) : 80);

        // How many total frames can we extract?
        int totalFrames = sheet.getTotalFrames();

        for (int i = 0; i < totalFrames; i++) {
            WritableImage frame = sheet.getFrame(i);
            if (frame == null)
                continue;

            ImageView iv = new ImageView(frame);
            iv.setPreserveRatio(true);
            iv.setFitWidth(Math.min(fw, 80));
            iv.setFitHeight(Math.min(fh, 80));
            iv.setStyle(
                    "-fx-effect: dropshadow(gaussian, rgba(80,80,255,0.25), 4, 0, 0, 1);");

            // Tooltip with frame index
            Tooltip tip = new Tooltip("Frame " + i + "  (" + fw + "×" + fh + ")");
            tip.setStyle("-fx-font-family: monospace; -fx-font-size: 11px;");
            Tooltip.install(iv, tip);

            // Highlight border on hover
            StackPane cell = new StackPane(iv);
            cell.setStyle("-fx-border-color: transparent; -fx-border-width: 1;");
            cell.setOnMouseEntered(e -> cell.setStyle("-fx-border-color: #7080ff; -fx-border-width: 1;"));
            cell.setOnMouseExited(e -> cell.setStyle("-fx-border-color: transparent; -fx-border-width: 1;"));

            grid.getChildren().add(cell);
        }

        if (grid.getChildren().isEmpty()) {
            Label empty = styledLabel("(no frames — check frame size)");
            empty.setTextFill(Color.web("#884444"));
            grid.getChildren().add(empty);
        }

        return grid;
    }

    // ---------------------------------------------------------------
    // Toggle
    // ---------------------------------------------------------------

    /** Call from LevelEditorScene.update() */
    public static void handleInput(gameEngine.core.KeyListener keys) {
        if (keys.isKeyJustPressed(javafx.scene.input.KeyCode.J)) {
            toggle();
        }
    }

    public static void toggle() {
        visible = !visible;
        panel.setVisible(visible);
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private static Label styledLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Monospace", 12));
        l.setTextFill(Color.web("#8888bb"));
        return l;
    }

    private static TextField sizedTextField(String val) {
        TextField tf = new TextField(val);
        tf.setPrefWidth(54);
        tf.setStyle(
                "-fx-background-color: rgba(30,30,60,0.9);" +
                        "-fx-text-fill: #c8c8ff;" +
                        "-fx-border-color: rgba(100,100,200,0.4);" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-font-family: monospace;" +
                        "-fx-font-size: 12px;");
        return tf;
    }
}