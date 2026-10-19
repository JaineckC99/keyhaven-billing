package com.example.Lodgebilling;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.Year;
import java.util.Map;

/**
 * Login screen for the Keyhaven application.
 * Dark graphite theme with a single champagne-brass accent.
 * Layout: brand half (left) and sign-in half (right) inside one framed panel.
 */
public class LoginScreen {

    // ---- Palette ----
    private static final String ONYX = "#0b0b0d";
    private static final String GRAPHITE = "#16171b";
    private static final String STEEL = "#2d2f36";
    private static final String SILVER = "#d9dce2";
    private static final String MUTED = "#8b8e99";
    private static final String BRASS = "#d4af6a";
    private static final String BRASS_LIGHT = "#ecd29b";

    // Demo users (username -> password). Replace with hashed storage in a real system.
    private static final Map<String, String> USERS = Map.of(
            "Nyirenda", "Season",
            "Jaineck", "Sequoia"
    );

    private static final String BUTTON_STYLE =
            "-fx-background-color: linear-gradient(to right, #c39a4f, " + BRASS_LIGHT + "); " +
                    "-fx-text-fill: #17130a; -fx-font-size: 15; -fx-font-weight: bold; " +
                    "-fx-background-radius: 8; -fx-padding: 13 0 13 0; -fx-cursor: hand;";
    private static final String BUTTON_HOVER_STYLE =
            "-fx-background-color: linear-gradient(to right, " + BRASS + ", #f6e3b8); " +
                    "-fx-text-fill: #17130a; -fx-font-size: 15; -fx-font-weight: bold; " +
                    "-fx-background-radius: 8; -fx-padding: 13 0 13 0; -fx-cursor: hand;";

    public Scene createScene(Stage stage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: radial-gradient(center 50% 40%, radius 85%, #25262c, " + ONYX + ");");

        // Fine diagonal texture, clipped to the window
        Pane texture = buildTexture();
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());
        root.setClip(clip);

        // Framed panel: brand half + form half
        VBox form = buildForm(stage);
        StackPane brand = buildBrandHalf();
        HBox panel = new HBox(brand, form);
        panel.setMaxWidth(840);
        panel.setMaxHeight(Region.USE_PREF_SIZE);
        panel.setStyle("-fx-background-color: " + GRAPHITE + "; -fx-background-radius: 18; " +
                "-fx-border-color: " + STEEL + "; -fx-border-radius: 18;");
        panel.setEffect(new DropShadow(50, 0, 20, Color.web("#000000", 0.7)));

        root.getChildren().addAll(texture, panel);

        // One entrance moment: the panel fades in while the emblem draws itself
        FadeTransition fade = new FadeTransition(Duration.millis(800), panel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        return new Scene(root, 1000, 750);
    }

    // ------------------------------------------------------------------
    // Background texture
    // ------------------------------------------------------------------
    private Pane buildTexture() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);
        pane.setManaged(false);
        for (int x = -1200; x < 2000; x += 36) {
            Line line = new Line(x, 0, x + 1200, 1200);
            line.setStroke(Color.web("#ffffff", 0.022));
            pane.getChildren().add(line);
        }
        return pane;
    }

    // ------------------------------------------------------------------
    // Brand half
    // ------------------------------------------------------------------
    private StackPane buildBrandHalf() {
        StackPane brand = new StackPane();
        brand.setPrefWidth(380);
        brand.setMinWidth(380);
        brand.setPrefHeight(520);
        brand.setStyle("-fx-background-color: linear-gradient(to bottom, #111215, #1d1e23); " +
                "-fx-background-radius: 18 0 0 18; " +
                "-fx-border-color: transparent " + STEEL + " transparent transparent;");

        Label companyName = new Label(spaced("KEYHAVEN"));
        companyName.setFont(Font.font("Georgia", FontWeight.BOLD, 32));
        companyName.setTextFill(Color.web("#f2f3f5"));

        Label slogan = new Label("Stay. Settle. Simple.");
        slogan.setFont(Font.font("Georgia", FontWeight.NORMAL, 16));
        slogan.setTextFill(Color.web(BRASS));

        Rectangle rule = new Rectangle(44, 2);
        rule.setFill(Color.web(BRASS, 0.7));

        Label tagline = new Label("Bills, reservations and receipts\nfor the front desk.");
        tagline.setFont(Font.font("Segoe UI", 13));
        tagline.setTextFill(Color.web(MUTED));
        tagline.setTextAlignment(TextAlignment.CENTER);

        VBox content = new VBox(10, buildLogo(), companyName, slogan, spacer(6), rule, spacer(2), tagline);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));

        Label footer = new Label("\u00A9 " + Year.now().getValue() + " Keyhaven");
        footer.setFont(Font.font("Segoe UI", 11));
        footer.setTextFill(Color.web("#5d606b"));
        StackPane.setAlignment(footer, Pos.BOTTOM_CENTER);
        StackPane.setMargin(footer, new Insets(0, 0, 18, 0));

        brand.getChildren().addAll(content, footer);
        return brand;
    }

    /** Thin spaces between letters give the wordmark a wide, engraved feel. */
    private String spaced(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(c).append('\u2009').append('\u200A');
        }
        return sb.toString().trim();
    }

    // ------------------------------------------------------------------
    // Logo: a roof over a keyhole inside a hexagon (a lodge, and the key to it)
    // ------------------------------------------------------------------
    /**
     * Uses logo.png if it exists in src/main/resources/com/example/Lodgebilling/,
     * otherwise draws the emblem with JavaFX shapes and animates it.
     */
    private Node buildLogo() {
        var stream = LoginScreen.class.getResourceAsStream("logo.png");
        if (stream != null) {
            ImageView view = new ImageView(new Image(stream));
            view.setFitWidth(140);
            view.setFitHeight(140);
            view.setPreserveRatio(true);
            view.setSmooth(true);
            return view;
        }

        // Soft brass glow behind the emblem
        Circle glow = new Circle(75, 75, 105);
        glow.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(BRASS, 0.22)), new Stop(1, Color.TRANSPARENT)));

        // Outer hexagon: dark metal body with a silver edge
        Polygon hex = hexagon(75, 75, 70);
        hex.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2c2e35")), new Stop(1, Color.web("#0d0d10"))));
        hex.setStroke(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#f1f2f5")), new Stop(0.5, Color.web("#7b7e89")),
                new Stop(1, Color.web("#d9dce2"))));
        hex.setStrokeWidth(2.5);
        hex.setStrokeLineJoin(StrokeLineJoin.ROUND);

        // Inner hairline hexagon
        Polygon innerHex = hexagon(75, 75, 60);
        innerHex.setFill(Color.TRANSPARENT);
        innerHex.setStroke(Color.web(BRASS, 0.4));
        innerHex.setStrokeWidth(1);

        // Roof (chevron) in brass
        Polyline roof = new Polyline(36, 86, 75, 44, 114, 86);
        roof.setFill(Color.TRANSPARENT);
        roof.setStroke(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#b88a3e")), new Stop(0.5, Color.web(BRASS_LIGHT)),
                new Stop(1, Color.web("#b88a3e"))));
        roof.setStrokeWidth(6);
        roof.setStrokeLineCap(StrokeLineCap.ROUND);
        roof.setStrokeLineJoin(StrokeLineJoin.ROUND);

        // Keyhole in silver
        Circle keyCircle = new Circle(75, 98, 9);
        Polygon keyStem = new Polygon(70.5, 104, 79.5, 104, 84, 126, 66, 126);
        Group keyhole = new Group(keyCircle, keyStem);
        LinearGradient silver = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ffffff")), new Stop(1, Color.web("#a9adb8")));
        keyCircle.setFill(silver);
        keyStem.setFill(silver);

        // Spark above the roof
        Circle spark = new Circle(75, 32, 3.2, Color.web(BRASS_LIGHT));

        Group logo = new Group(glow, hex, innerHex, roof, keyhole, spark);
        logo.setEffect(new DropShadow(24, Color.web(BRASS, 0.25)));

        // Draw-on animation: hexagon, then roof, then keyhole and spark fade in
        double hexLen = 430;
        double roofLen = 130;
        hex.getStrokeDashArray().addAll(hexLen, hexLen);
        roof.getStrokeDashArray().addAll(roofLen, roofLen);
        hex.setStrokeDashOffset(hexLen);
        roof.setStrokeDashOffset(roofLen);
        innerHex.setOpacity(0);
        keyhole.setOpacity(0);
        spark.setOpacity(0);

        Timeline draw = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(hex.strokeDashOffsetProperty(), hexLen),
                        new KeyValue(roof.strokeDashOffsetProperty(), roofLen),
                        new KeyValue(innerHex.opacityProperty(), 0),
                        new KeyValue(keyhole.opacityProperty(), 0),
                        new KeyValue(spark.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(1000),
                        new KeyValue(hex.strokeDashOffsetProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(roof.strokeDashOffsetProperty(), roofLen),
                        new KeyValue(innerHex.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(1600),
                        new KeyValue(roof.strokeDashOffsetProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(keyhole.opacityProperty(), 0),
                        new KeyValue(spark.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(2100),
                        new KeyValue(keyhole.opacityProperty(), 1),
                        new KeyValue(spark.opacityProperty(), 1))
        );
        draw.play();

        return logo;
    }

    private Polygon hexagon(double cx, double cy, double r) {
        Polygon p = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(90 + 60 * i);
            p.getPoints().addAll(cx + r * Math.cos(angle), cy - r * Math.sin(angle));
        }
        return p;
    }

    // ------------------------------------------------------------------
    // Form half
    // ------------------------------------------------------------------
    private VBox buildForm(Stage stage) {
        Label heading = new Label("Sign in");
        heading.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
        heading.setTextFill(Color.web("#f2f3f5"));

        Label subheading = new Label("Use your staff account to open the billing desk.");
        subheading.setFont(Font.font("Segoe UI", 13));
        subheading.setTextFill(Color.web(MUTED));
        subheading.setWrapText(true);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleField(usernameField, 14);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField, 64);

        // Plain-text twin of the password field, used by the Show / Hide toggle
        TextField passwordVisible = new TextField();
        passwordVisible.setPromptText("Password");
        styleField(passwordVisible, 64);
        passwordVisible.textProperty().bindBidirectional(passwordField.textProperty());
        passwordVisible.setVisible(false);
        passwordVisible.setManaged(false);

        Button toggle = new Button("Show");
        toggle.setFocusTraversable(false);
        String toggleStyle = "-fx-background-color: transparent; -fx-text-fill: " + MUTED + "; " +
                "-fx-font-size: 12; -fx-cursor: hand; -fx-padding: 4 8 4 8;";
        String toggleHover = "-fx-background-color: transparent; -fx-text-fill: " + BRASS + "; " +
                "-fx-font-size: 12; -fx-cursor: hand; -fx-padding: 4 8 4 8;";
        toggle.setStyle(toggleStyle);
        toggle.setOnMouseEntered(e -> toggle.setStyle(toggleHover));
        toggle.setOnMouseExited(e -> toggle.setStyle(toggleStyle));
        toggle.setOnAction(e -> {
            boolean show = !passwordVisible.isVisible();
            passwordVisible.setVisible(show);
            passwordVisible.setManaged(show);
            passwordField.setVisible(!show);
            passwordField.setManaged(!show);
            toggle.setText(show ? "Hide" : "Show");
            (show ? passwordVisible : passwordField).requestFocus();
        });

        StackPane passwordBox = new StackPane(passwordField, passwordVisible, toggle);
        StackPane.setAlignment(toggle, Pos.CENTER_RIGHT);
        StackPane.setMargin(toggle, new Insets(0, 8, 0, 0));

        Label message = new Label();
        message.setWrapText(true);
        message.setMinHeight(20);
        message.setStyle("-fx-text-fill: #ff7b72; -fx-font-weight: bold; -fx-font-size: 12;");

        Button loginBtn = new Button("Sign in");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle(BUTTON_STYLE);
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(BUTTON_HOVER_STYLE));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(BUTTON_STYLE));

        Label notice = new Label("Authorised staff only.");
        notice.setFont(Font.font("Segoe UI", 11));
        notice.setTextFill(Color.web("#5d606b"));

        VBox form = new VBox(8,
                heading, subheading,
                spacer(18),
                fieldLabel("Username"), usernameField,
                spacer(6),
                fieldLabel("Password"), passwordBox,
                spacer(10),
                loginBtn,
                message,
                spacer(6),
                notice
        );
        form.setPrefWidth(460);
        form.setMinWidth(400);
        form.setPadding(new Insets(48, 48, 40, 48));
        form.setAlignment(Pos.CENTER_LEFT);

        Runnable doLogin = () -> {
            String user = usernameField.getText().trim();
            String pass = passwordField.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                showError(message, form, "Enter both your username and password.");
                return;
            }
            if (authenticate(user, pass)) {
                LodgeBilling app = new LodgeBilling(stage, user);
                stage.setScene(app.createScene());
                stage.setTitle("Keyhaven - " + user);
            } else {
                showError(message, form, "Incorrect username or password. Try again.");
                passwordField.clear();
                (passwordVisible.isVisible() ? passwordVisible : passwordField).requestFocus();
            }
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passwordField.setOnAction(e -> doLogin.run());
        passwordVisible.setOnAction(e -> doLogin.run());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        return form;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        l.setTextFill(Color.web(SILVER));
        return l;
    }

    private Region spacer(double height) {
        Region r = new Region();
        r.setMinHeight(height);
        r.setPrefHeight(height);
        return r;
    }

    private String fieldStyle(boolean focused, double rightPadding) {
        return "-fx-background-color: " + (focused ? "#101114" : "#0e0f11") + "; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: " + (focused ? BRASS : STEEL) + "; " +
                "-fx-border-width: " + (focused ? "1.5" : "1") + "; -fx-border-radius: 8; " +
                "-fx-text-fill: #eceef2; -fx-prompt-text-fill: #5d606b; " +
                "-fx-font-size: 14; -fx-padding: 12 " + rightPadding + " 12 14;";
    }

    private void styleField(Control field, double rightPadding) {
        field.setStyle(fieldStyle(false, rightPadding));
        field.focusedProperty().addListener((obs, was, focused) ->
                field.setStyle(fieldStyle(focused, rightPadding)));
    }

    /** Shows an error and shakes the form so the failure is noticeable. */
    private void showError(Label message, Node target, String text) {
        message.setText(text);
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), target);
        shake.setFromX(0);
        shake.setToX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> target.setTranslateX(0));
        shake.play();
    }

    private boolean authenticate(String user, String pass) {
        return pass.equals(USERS.get(user));
    }
}