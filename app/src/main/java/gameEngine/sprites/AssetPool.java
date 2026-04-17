package gameEngine.sprites;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class AssetPool {

    private static Map<String, Image> images = new HashMap<>();

    /**
     * Returns the image at the given path, loading and caching it on first
     * access.
     *
     * @param path resource path relative to the classpath root (without leading
     * slash)
     * @return the loaded Image, or {@code null} if loading failed
     */
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

    /**
     * Removes the cached image for the given path, forcing a reload on next
     * access.
     *
     * @param path the resource path to evict from the cache
     */
    public static void clearImage(String path) {
        images.remove(path);
    }

    /**
     * Clears the entire image cache.
     */
    public static void clearAll() {
        images.clear();
        System.out.println("AssetPool: cache cleared");
    }
}
