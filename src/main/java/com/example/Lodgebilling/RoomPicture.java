package com.example.Lodgebilling;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Picture of a room type with a name badge.
 * <p>
 * If you add real photos to src/main/resources/com/example/Lodgebilling/rooms/
 * named standard.jpg, deluxe.jpg and family.jpg (.png also works), they are used.
 * Otherwise an illustrated room is drawn with JavaFX shapes.
 */
public final class RoomPicture {

    private static final double BASE_W = 300;
    private static final double BASE_H = 170;
    private static final Map<String, Image> CACHE = new HashMap<>();

    private RoomPicture() {
    }

    public static Node create(String roomType, double width, double height) {
        String type = roomType == null ? "Standard" : roomType;

        Node content;
        Image photo = loadPhoto(type);
        if (photo != null) {
            content = photo(photo, width, height);
        } else {
            content = drawn(type, width, height);
        }

        Label badge = new Label(type + " room");
        badge.getStyleClass().add("room-badge");
        StackPane.setAlignment(badge, Pos.BOTTOM_LEFT);
        StackPane.setMargin(badge, new Insets(0, 0, 10, 10));

        StackPane box = new StackPane(content, badge);
        box.setPrefSize(width, height);
        box.setMinSize(width, height);
        box.setMaxSize(width, height);

        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(18);
        clip.setArcHeight(18);
        box.setClip(clip);
        return box;
    }

    // ------------------------------------------------------------------
    // Real photos (optional)
    // ------------------------------------------------------------------
    private static Image loadPhoto(String type) {
        String key = type.toLowerCase();
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }
        Image found = null;
        for (String ext : new String[]{".jpg", ".jpeg", ".png"}) {
            try (InputStream in = RoomPicture.class.getResourceAsStream("rooms/" + key + ext)) {
                if (in != null) {
                    Image img = new Image(in);
                    if (!img.isError()) {
                        found = img;
                        break;
                    }
                }
            } catch (IOException ignored) {
                // try the next extension
            }
        }
        CACHE.put(key, found);
        return found;
    }

    /** Fills the box and crops from the centre so the photo is never stretched. */
    private static Node photo(Image img, double w, double h) {
        ImageView view = new ImageView(img);
        view.setFitWidth(w);
        view.setFitHeight(h);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        double ratio = w / h;
        double iw = img.getWidth();
        double ih = img.getHeight();
        if (iw / ih > ratio) {
            double vw = ih * ratio;
            view.setViewport(new Rectangle2D((iw - vw) / 2, 0, vw, ih));
        } else {
            double vh = iw / ratio;
            view.setViewport(new Rectangle2D(0, (ih - vh) / 2, iw, vh));
        }
        return view;
    }

    // ------------------------------------------------------------------
    // Illustrated rooms (fallback)
    // ------------------------------------------------------------------
    private static Node drawn(String type, double w, double h) {
        Group art = new Group();

        Rectangle wall = rect(0, 0, BASE_W, 118, 0, "#2e3037");
        wall.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2e3037")), new Stop(1, Color.web("#1e1f24"))));
        Rectangle floor = rect(0, 118, BASE_W, 52, 0, "#3b3124");
        floor.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#3b3124")), new Stop(1, Color.web("#241d14"))));
        Rectangle baseboard = rect(0, 114, BASE_W, 5, 0, "#121316");
        art.getChildren().addAll(wall, floor, baseboard);

        switch (type) {
            case "Deluxe" -> deluxe(art);
            case "Family" -> family(art);
            default -> standard(art);
        }

        art.getTransforms().add(new Scale(w / BASE_W, h / BASE_H, 0, 0));
        Pane pane = new Pane(art);
        pane.setPrefSize(w, h);
        pane.setMinSize(w, h);
        pane.setMaxSize(w, h);
        return pane;
    }

    private static void standard(Group art) {
        window(art, 212, 26, 60, 52);
        frame(art, 128, 30, 52, 20);
        nightstand(art, 58, 92, 26, 28);
        art.getChildren().add(rect(98, 56, 112, 40, 6, "#4a3b26"));          // headboard
        art.getChildren().add(rect(94, 84, 120, 36, 8, "#e6e8ec"));          // mattress
        art.getChildren().add(rect(104, 78, 42, 16, 7, "#ffffff"));          // pillows
        art.getChildren().add(rect(160, 78, 42, 16, 7, "#ffffff"));
        art.getChildren().add(rect(94, 98, 120, 24, 6, "#5b6470"));          // blanket
        art.getChildren().add(rect(94, 98, 120, 5, 0, "#7b8593"));
        art.getChildren().add(rect(98, 120, 6, 8, 2, "#121316"));            // legs
        art.getChildren().add(rect(204, 120, 6, 8, 2, "#121316"));
    }

    private static void deluxe(Group art) {
        window(art, 200, 20, 80, 64);
        art.getChildren().add(rect(190, 16, 16, 78, 4, "#6b4e2a"));          // curtains
        art.getChildren().add(rect(274, 16, 16, 78, 4, "#6b4e2a"));
        frame(art, 88, 26, 44, 18);
        frame(art, 140, 26, 44, 18);
        nightstand(art, 22, 92, 26, 28);
        nightstand(art, 218, 92, 24, 28);
        art.getChildren().add(rect(60, 44, 150, 52, 8, "#3a2f1f"));          // headboard
        art.getChildren().add(rect(60, 44, 150, 4, 0, "#d4af6a"));           // brass trim
        art.getChildren().add(rect(56, 84, 158, 38, 8, "#f0f1f4"));          // mattress
        for (int i = 0; i < 4; i++) {
            art.getChildren().add(rect(66 + i * 36, 76, 32, 16, 7, "#ffffff")); // pillows
        }
        art.getChildren().add(rect(56, 100, 158, 22, 6, "#7a5c2e"));         // blanket
        art.getChildren().add(rect(56, 100, 158, 6, 0, "#d4af6a"));
        art.getChildren().add(rect(248, 98, 42, 22, 6, "#2f3b4a"));          // armchair
        art.getChildren().add(rect(248, 84, 42, 18, 6, "#3a4858"));
    }

    private static void family(Group art) {
        window(art, 120, 20, 60, 50);
        // bed A
        art.getChildren().add(rect(24, 58, 96, 36, 6, "#4a3b26"));
        art.getChildren().add(rect(20, 84, 104, 36, 8, "#e6e8ec"));
        art.getChildren().add(rect(28, 78, 34, 14, 6, "#ffffff"));
        art.getChildren().add(rect(20, 98, 104, 22, 6, "#3f7d7a"));
        art.getChildren().add(rect(20, 98, 104, 5, 0, "#5aa5a1"));
        // bed B
        art.getChildren().add(rect(180, 58, 96, 36, 6, "#4a3b26"));
        art.getChildren().add(rect(176, 84, 104, 36, 8, "#e6e8ec"));
        art.getChildren().add(rect(238, 78, 34, 14, 6, "#ffffff"));
        art.getChildren().add(rect(176, 98, 104, 22, 6, "#8a5a3a"));
        art.getChildren().add(rect(176, 98, 104, 5, 0, "#b07d56"));
        nightstand(art, 132, 92, 36, 28);
        Ellipse rug = new Ellipse(150, 148, 78, 12);                          // rug
        rug.setFill(Color.web("#4a3b26", 0.85));
        art.getChildren().add(rug);
    }

    // ---- shared pieces ----
    private static void window(Group art, double x, double y, double w, double h) {
        art.getChildren().add(rect(x - 3, y - 3, w + 6, h + 6, 4, "#d4af6a"));
        Rectangle glass = rect(x, y, w, h, 2, "#1c3a52");
        glass.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2b5a80")), new Stop(1, Color.web("#122a3d"))));
        art.getChildren().add(glass);
        art.getChildren().add(rect(x + w / 2 - 1, y, 2, h, 0, "#d4af6a"));
        art.getChildren().add(rect(x, y + h / 2 - 1, w, 2, 0, "#d4af6a"));
    }

    private static void frame(Group art, double x, double y, double w, double h) {
        art.getChildren().add(rect(x, y, w, h, 2, "#d4af6a"));
        art.getChildren().add(rect(x + 3, y + 3, w - 6, h - 6, 1, "#2a2c33"));
    }

    private static void nightstand(Group art, double x, double y, double w, double h) {
        double cx = x + w / 2;
        Circle glow = new Circle(cx, y - 14, 24);
        glow.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ecd29b", 0.35)), new Stop(1, Color.TRANSPARENT)));
        art.getChildren().add(glow);
        art.getChildren().add(rect(x, y, w, h, 3, "#4a3b26"));
        art.getChildren().add(rect(cx - 3, y - 12, 6, 12, 1, "#d4af6a"));
        Polygon shade = new Polygon(cx - 9, y - 12, cx + 9, y - 12, cx + 6, y - 24, cx - 6, y - 24);
        shade.setFill(Color.web("#ecd29b"));
        art.getChildren().add(shade);
    }

    private static Rectangle rect(double x, double y, double w, double h, double arc, String color) {
        Rectangle r = new Rectangle(x, y, w, h);
        r.setArcWidth(arc);
        r.setArcHeight(arc);
        r.setFill(Color.web(color));
        return r;
    }
}