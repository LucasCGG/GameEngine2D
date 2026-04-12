package gameEngine.sprites;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class AssetPool {

    private static Map<String, Image> images = new HashMap<>();

    public static Image getImage(String path) {
        if (!images.containsKey(path)) {
            try {
                Image image = new Image(AssetPool.class.getResourceAsStream("/" + path));
                if (image.isError()) {
                    System.err.println("AssetPool: failed to load image: " + path);
                    return null;
                }
                images.put(path, image);
                System.out.println("AssetPool: loaded '" + path + "'");
            } catch (Exception e) {
                System.err.println("AssetPool: exception loading '" + path + "': " + e.getMessage());
                return null;
            }
        }
        return images.get(path);
    }

    public static void clearImage(String path) {
        images.remove(path);
    }

    public static void clearAll() {
        images.clear();
        System.out.println("AssetPool: cache cleared");
    }
}
