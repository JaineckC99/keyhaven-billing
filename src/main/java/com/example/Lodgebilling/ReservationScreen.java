package com.example.Lodgebilling;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDate;

public class ReservationScreen {

    private Stage stage;
    private String currentUser;
    private LodgeBilling mainApp;

    public ReservationScreen(Stage stage, String currentUser, LodgeBilling mainApp) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.mainApp = mainApp;
    }

    public Scene createScene() {
        // --- Header ---
        VBox headerBox = new VBox(5);
        headerBox.setPadding(new Insets(10, 20, 10, 20));
        headerBox.setStyle("-fx-background-color: #b8bfc9; -fx-border-color: #9aa3ad; -fx-border-width: 0 0 1 0;");

        Label title = new Label("New Reservation");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #3a3a3a;");

        Label userLabel = new Label("Logged in as: " + currentUser);
        userLabel.setStyle("-fx-text-fill: #4a90e2; -fx-font-weight: bold;");

        headerBox.getChildren().addAll(title, userLabel);

        // --- Form ---
        GridPane form = new GridPane();
        form.setVgap(12);
        form.setHgap(15);

        TextField guestField = new TextField();
        guestField.setPromptText("Guest full name");

        ComboBox<String> roomCombo = new ComboBox<>();
        roomCombo.getItems().addAll("Standard", "Deluxe", "Family");
        roomCombo.setValue("Standard");
        roomCombo.setPrefWidth(200);

        DatePicker checkInPicker = new DatePicker(LocalDate.now());

        TextField nightsField = new TextField();
        nightsField.setPromptText("e.g. 3");

        TextField depositField = new TextField("0.00");

        form.add(new Label("Guest name"), 0, 0);
        form.add(guestField, 1, 0);
        form.add(new Label("Room type"), 0, 1);
        form.add(roomCombo, 1, 1);
        form.add(new Label("Check-in date"), 0, 2);
        form.add(checkInPicker, 1, 2);
        form.add(new Label("Nights"), 0, 3);
        form.add(nightsField, 1, 3);
        form.add(new Label("Deposit in K"), 0, 4);
        form.add(depositField, 1, 4);

        VBox formPanel = new VBox(15, form);
        formPanel.setPadding(new Insets(20));
        formPanel.setStyle("-fx-background-color: #f0eeea; -fx-background-radius: 6; " +
                "-fx-border-color: #c8c4bc; -fx-border-radius: 6;");
        formPanel.setMaxWidth(420);

        // --- Status ---
        Label status = new Label("Fill the form and click Book reservation.");
        status.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        status.setPadding(new Insets(10, 20, 10, 20));

        // --- Buttons ---
        Button bookBtn = new Button("Book reservation");
        bookBtn.setStyle("-fx-background-color: #9a7b4f; -fx-text-fill: white; " +
                "-fx-background-radius: 4; -fx-font-weight: bold;");

        Button backBtn = new Button("← Back to main");
        backBtn.setStyle("-fx-background-color: #7d8792; -fx-text-fill: white; " +
                "-fx-background-radius: 4;");

        Button clearBtn = new Button("Clear form");
        clearBtn.setStyle("-fx-background-color: #7d8792; -fx-text-fill: white; " +
                "-fx-background-radius: 4;");

        HBox buttonRow = new HBox(10, bookBtn, clearBtn, backBtn);
        buttonRow.setAlignment(Pos.CENTER_LEFT);
        buttonRow.setPadding(new Insets(10, 20, 20, 20));

        // --- Book action ---
        bookBtn.setOnAction(e -> {
            String guest = guestField.getText().trim();
            if (guest.isEmpty()) {
                status.setText("Error: Guest name is required.");
                status.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
                return;
            }
            int nights;
            try {
                nights = Integer.parseInt(nightsField.getText().trim());
                if (nights <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                status.setText("Error: Nights must be a positive number.");
                status.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
                return;
            }
            double deposit;
            try {
                deposit = Double.parseDouble(depositField.getText().trim());
                if (deposit < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                status.setText("Error: Deposit must be a valid number.");
                status.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
                return;
            }

            String resNum = mainApp.addReservation(
                    guest, roomCombo.getValue(),
                    checkInPicker.getValue(), nights, deposit
            );

            status.setText("Booked " + resNum + " for " + guest + ".");
            status.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");

            guestField.clear();
            nightsField.clear();
            depositField.setText("0.00");
            roomCombo.setValue("Standard");
            checkInPicker.setValue(LocalDate.now());
        });

        // --- Clear action ---
        clearBtn.setOnAction(e -> {
            guestField.clear();
            nightsField.clear();
            depositField.setText("0.00");
            roomCombo.setValue("Standard");
            checkInPicker.setValue(LocalDate.now());
            status.setText("Form cleared.");
            status.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        });

        // --- Back action ---
        backBtn.setOnAction(e -> {
            stage.setScene(mainApp.createScene());
            stage.setTitle("KeyHaven");
        });

        // --- Layout ---
        VBox center = new VBox(20, formPanel, status);
        center.setPadding(new Insets(20));
        center.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        root.setCenter(center);
        root.setBottom(buttonRow);
        root.setStyle("-fx-background-color: #e8e6e1;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setStyle("-fx-background-color: #e8e6e1;");

        return new Scene(scrollPane, 1000, 750);
    }
}