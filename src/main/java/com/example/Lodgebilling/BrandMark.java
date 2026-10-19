package com.example.Lodgebilling;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Static, scalable version of the Lodge Billing emblem
 * (a roof over a keyhole inside a hexagon). Used in the dashboard header.
 */
public final class BrandMark {

    private static final String BRASS = "#d4af6a";
    private static final String BRASS_LIGHT = "#ecd29b";

    private BrandMark() {
    }

    /** @param size the rendered width/height in pixels */
    public static Node create(double size) {
        Polygon hex = hexagon(75, 75, 70);
        hex.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2c2e35")), new Stop(1, Color.web("#0d0d10"))));
        hex.setStroke(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#f1f2f5")), new Stop(0.5, Color.web("#7b7e89")),
                new Stop(1, Color.web("#d9dce2"))));
        hex.setStrokeWidth(3);
        hex.setStrokeLineJoin(StrokeLineJoin.ROUND);

        Polygon innerHex = hexagon(75, 75, 60);
        innerHex.setFill(Color.TRANSPARENT);
        innerHex.setStroke(Color.web(BRASS, 0.4));
        innerHex.setStrokeWidth(1.5);

        Polyline roof = new Polyline(36, 86, 75, 44, 114, 86);
        roof.setFill(Color.TRANSPARENT);
        roof.setStroke(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#b88a3e")), new Stop(0.5, Color.web(BRASS_LIGHT)),
                new Stop(1, Color.web("#b88a3e"))));
        roof.setStrokeWidth(7);
        roof.setStrokeLineCap(StrokeLineCap.ROUND);
        roof.setStrokeLineJoin(StrokeLineJoin.ROUND);

        LinearGradient silver = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ffffff")), new Stop(1, Color.web("#a9adb8")));
        Circle keyCircle = new Circle(75, 98, 9, silver);
        Polygon keyStem = new Polygon(70.5, 104, 79.5, 104, 84, 126, 66, 126);
        keyStem.setFill(silver);

        Circle spark = new Circle(75, 32, 3.5, Color.web(BRASS_LIGHT));

        Group art = new Group(hex, innerHex, roof, keyCircle, keyStem, spark);
        double scale = size / 150.0;
        art.setScaleX(scale);
        art.setScaleY(scale);

        // Wrapping group makes layout use the scaled size
        Group wrapper = new Group(art);
        wrapper.setEffect(new DropShadow(10, Color.web(BRASS, 0.25)));
        return wrapper;
    }

    private static Polygon hexagon(double cx, double cy, double r) {
        Polygon p = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(90 + 60 * i);
            p.getPoints().addAll(cx + r * Math.cos(angle), cy - r * Math.sin(angle));
        }
        return p;
    }
}