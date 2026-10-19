package com.example.Lodgebilling;

import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Keyhaven main window: sidebar navigation, top search bar and five pages
 * (Dashboard, New Bill, Reservations, Bills and Receipts, Room Prices).
 * Styling lives in dashboard.css (same package folder under resources).
 */
public class LodgeBilling {

    private static final String CSS = loadStylesheet();
    private static final List<String> ROOM_TYPES = List.of("Standard", "Deluxe", "Family");
    private static final String DEFAULT_ROOM = "Standard";

    // Display-only copies of the rates in Bill.java (update both if prices change)
    private static final String[][] RATES = {
            {"Standard", "K 350.00"},
            {"Deluxe", "K 550.00"},
            {"Family", "K 750.00"}
    };
    private static final List<String> RULES = List.of(
            "5 nights or more: 5% off the room charge.",
            "Extras get no discount.",
            "No tax is charged in this lab.",
            "Saved bills last only until you close the app.");

    private static final Map<String, String> ROOM_BLURBS = Map.of(
            "Standard", "One double bed. Best for 1 to 2 guests.",
            "Deluxe", "One king bed with extra space and a sitting area. Best for couples.",
            "Family", "Two beds and extra floor space. Best for families of up to 4.");

    // Sidebar icons (24x24 vector paths)
    private static final String ICON_DASHBOARD = "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z";
    private static final String ICON_ADD = "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z";
    private static final String ICON_CALENDAR = "M19 3h-1V1h-2v2H8V1H6v2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2zm0 16H5V9h14v10z";
    private static final String ICON_RECEIPT = "M18 17H6v-2h12v2zm0-4H6v-2h12v2zm0-4H6V7h12v2zM3 22l1.5-1.5L6 22l1.5-1.5L9 22l1.5-1.5L12 22l1.5-1.5L15 22l1.5-1.5L18 22l1.5-1.5L21 22V2l-1.5 1.5L18 2l-1.5 1.5L15 2l-1.5 1.5L12 2l-1.5 1.5L9 2 7.5 3.5 6 2 4.5 3.5 3 2v20z";
    private static final String ICON_TAG = "M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41 0-.55-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z";
    private static final String ICON_LOGOUT = "M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z";
    private static final String ICON_SEARCH = "M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z";

    private enum Tone { OK, ERROR, INFO }

    private enum Page {
        DASHBOARD("Dashboard"),
        NEW_BILL("New Bill"),
        RESERVATIONS("Reservations"),
        BILLS("Bills & Receipts"),
        PRICES("Room Prices");

        final String title;

        Page(String title) {
            this.title = title;
        }
    }

    private final Stage stage;
    private final String currentUser;

    // Data lives here (not in createScene) so it survives page changes
    private final ObservableList<Bill> billList = FXCollections.observableArrayList();
    private final ObservableList<Reservation> resList = FXCollections.observableArrayList();
    private final FilteredList<Bill> filteredBills = new FilteredList<>(billList, b -> true);
    private final FilteredList<Reservation> filteredRes = new FilteredList<>(resList, r -> true);
    private int billCounter = 1;   // B001, B002, ...
    private int resCounter = 1;    // R001, R002, ...

    // Navigation
    private final Map<Page, Button> navButtons = new EnumMap<>(Page.class);
    private final Map<Page, Node> pages = new EnumMap<>(Page.class);
    private StackPane pageHolder;
    private Page currentPage = Page.DASHBOARD;
    private TextField searchField;
    private Label statusLabel;

    // Tables
    private TableView<Bill> billTable;
    private TableView<Reservation> resTable;

    // New bill page
    private TextField billGuestField;
    private TextField billNightsField;
    private TextField billExtrasField;
    private TextField billPaidField;
    private ComboBox<String> billRoomCombo;
    private TextArea billNotesArea;
    private Label sumTotal;
    private Label sumPaid;
    private Label sumBalance;
    private Label sumNote;
    private Reservation pendingCheckIn;   // reservation being converted into a bill
    private HBox checkInBanner;
    private Label checkInBannerText;

    // Reservation page
    private TextField resGuestField;
    private TextField resNightsField;
    private TextField resDepositField;
    private ComboBox<String> resRoomCombo;
    private DatePicker resCheckInPicker;

    public LodgeBilling(Stage stage, String currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    // ------------------------------------------------------------------
    // Scene
    // ------------------------------------------------------------------
    public Scene createScene() {
        navButtons.clear();
        pages.clear();
        filteredBills.setPredicate(b -> true);
        filteredRes.setPredicate(r -> true);

        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("dashboard");
        shell.setLeft(buildSidebar());

        pageHolder = new StackPane();
        pages.put(Page.DASHBOARD, wrapPage(buildDashboardPage()));
        pages.put(Page.NEW_BILL, wrapPage(buildNewBillPage()));
        pages.put(Page.RESERVATIONS, wrapPage(buildReservationsPage()));
        pages.put(Page.BILLS, wrapPage(buildBillsPage()));
        pages.put(Page.PRICES, wrapPage(buildPricesPage()));

        BorderPane main = new BorderPane();
        main.setTop(buildTopBar());
        main.setCenter(pageHolder);
        main.setBottom(buildStatusBar());
        shell.setCenter(main);

        Scene scene = new Scene(shell, 1000, 750);
        if (CSS != null) {
            scene.getStylesheets().add(CSS);
        }
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN),
                () -> showPage(Page.NEW_BILL));
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN),
                () -> searchField.requestFocus());

        showPage(Page.DASHBOARD);
        setStatus("Ready.", Tone.INFO);
        return scene;
    }

    private static String loadStylesheet() {
        var url = LodgeBilling.class.getResource("dashboard.css");
        if (url == null) {
            System.err.println("dashboard.css not found: place it in src/main/resources/com/example/Lodgebilling/");
            return null;
        }
        return url.toExternalForm();
    }

    private Node wrapPage(VBox content) {
        content.setPadding(new Insets(8, 28, 24, 28));
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        return scroll;
    }

    // ------------------------------------------------------------------
    // Navigation
    // ------------------------------------------------------------------
    private void showPage(Page page) {
        currentPage = page;
        Node node = pages.get(page);
        node.setOpacity(0);
        pageHolder.getChildren().setAll(node);
        FadeTransition fade = new FadeTransition(Duration.millis(200), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        navButtons.forEach((p, button) -> {
            button.getStyleClass().remove("active");
            if (p == page) {
                button.getStyleClass().add("active");
            }
        });
    }

    private Node buildSidebar() {
        Label name = new Label("Keyhaven");
        name.getStyleClass().add("brand-name");
        Label slogan = new Label("Stay. Settle. Simple.");
        slogan.getStyleClass().add("brand-slogan");

        HBox brand = new HBox(12, BrandMark.create(46), new VBox(1, name, slogan));
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(24, 20, 22, 20));

        Label menuLabel = new Label("MENU");
        menuLabel.getStyleClass().add("sidebar-section");

        VBox nav = new VBox(6,
                menuLabel,
                navItem(Page.DASHBOARD, ICON_DASHBOARD),
                navItem(Page.NEW_BILL, ICON_ADD),
                navItem(Page.RESERVATIONS, ICON_CALENDAR),
                navItem(Page.BILLS, ICON_RECEIPT),
                navItem(Page.PRICES, ICON_TAG));
        nav.setPadding(new Insets(0, 14, 0, 14));

        String initial = currentUser.isEmpty() ? "?" : currentUser.substring(0, 1).toUpperCase();
        Label letter = new Label(initial);
        letter.getStyleClass().add("avatar-letter");
        StackPane avatar = new StackPane(letter);
        avatar.getStyleClass().add("avatar");
        avatar.setMinSize(38, 38);
        avatar.setPrefSize(38, 38);
        avatar.setMaxSize(38, 38);

        Label userName = new Label(currentUser);
        userName.getStyleClass().add("user-name");
        Label role = new Label("Front desk");
        role.getStyleClass().add("muted-small");
        HBox userCard = new HBox(10, avatar, new VBox(1, userName, role));
        userCard.setAlignment(Pos.CENTER_LEFT);

        Button logoutBtn = new Button("Log out", icon(ICON_LOGOUT, "nav-icon"));
        logoutBtn.setGraphicTextGap(10);
        logoutBtn.getStyleClass().add("logout-btn");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> logout());

        VBox bottom = new VBox(14, userCard, logoutBtn);
        bottom.setPadding(new Insets(16, 16, 20, 16));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox sidebar = new VBox(brand, nav, spacer, bottom);
        sidebar.setPrefWidth(236);
        sidebar.setMinWidth(236);
        sidebar.setMaxWidth(236);
        sidebar.getStyleClass().add("sidebar");
        return sidebar;
    }

    private Button navItem(Page page, String iconPath) {
        Button button = new Button(page.title, icon(iconPath, "nav-icon"));
        button.setGraphicTextGap(12);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("nav-item");
        button.setOnAction(e -> showPage(page));
        navButtons.put(page, button);
        return button;
    }

    private Node icon(String pathData, String styleClass) {
        StackPane box = new StackPane();
        box.setMinSize(20, 20);
        box.setPrefSize(20, 20);
        box.setMaxSize(20, 20);
        SVGPath path = new SVGPath();
        path.setContent(pathData);
        path.getStyleClass().add(styleClass);
        path.setScaleX(0.83);
        path.setScaleY(0.83);
        box.getChildren().add(path);
        return box;
    }

    // ------------------------------------------------------------------
    // Top bar (search) and status bar
    // ------------------------------------------------------------------
    private Node buildTopBar() {
        searchField = new TextField();
        searchField.setPromptText("Search by guest name or bill number...");
        searchField.textProperty().addListener((obs, old, text) -> applySearch(text));
        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox searchBox = new HBox(10, icon(ICON_SEARCH, "search-icon"), searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getStyleClass().add("search-box");
        searchBox.setPrefWidth(440);
        searchBox.setMaxWidth(440);

        Label date = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));
        date.getStyleClass().add("muted");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(16, searchBox, spacer, date);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(20, 28, 12, 28));
        bar.getStyleClass().add("topbar");
        return bar;
    }

    private void applySearch(String raw) {
        String q = raw == null ? "" : raw.trim().toLowerCase();
        filteredBills.setPredicate(b -> q.isEmpty()
                || b.getGuestName().toLowerCase().contains(q)
                || b.getBillNumber().toLowerCase().contains(q));
        filteredRes.setPredicate(r -> q.isEmpty()
                || r.getGuestName().toLowerCase().contains(q)
                || r.getResNumber().toLowerCase().contains(q));
        if (!q.isEmpty() && (currentPage == Page.NEW_BILL || currentPage == Page.PRICES)) {
            showPage(Page.BILLS);
        }
    }

    private Node buildStatusBar() {
        statusLabel = new Label();
        statusLabel.getStyleClass().add("status");
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setWrapText(true);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        HBox box = new HBox(statusLabel);
        box.setPadding(new Insets(0, 28, 18, 28));
        return box;
    }

    private void setStatus(String text, Tone tone) {
        statusLabel.setText(text);
        statusLabel.getStyleClass().removeAll("status-ok", "status-error", "status-info");
        statusLabel.getStyleClass().add(switch (tone) {
            case OK -> "status-ok";
            case ERROR -> "status-error";
            case INFO -> "status-info";
        });
    }

    // ------------------------------------------------------------------
    // Page 1: Dashboard
    // ------------------------------------------------------------------
    private VBox buildDashboardPage() {
        Button viewAll = new Button("View all");
        viewAll.setOnAction(e -> showPage(Page.BILLS));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox titleRow = new HBox(panelTitle("Recent bills"), spacer, viewAll);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        TableView<Bill> recent = buildBillTable(true);
        recent.setPrefHeight(230);
        VBox recentPanel = new VBox(12, titleRow, recent);
        recentPanel.setPadding(new Insets(20));
        recentPanel.getStyleClass().add("panel");

        VBox ratesPanel = new VBox(4, panelTitle("Room prices"));
        for (String[] rate : RATES) {
            ratesPanel.getChildren().add(priceRow(rate[0], rate[1] + " / night"));
        }
        ratesPanel.setPadding(new Insets(20));
        ratesPanel.getStyleClass().add("panel");

        VBox infoPanel = new VBox(10, panelTitle("Quick info"));
        for (String rule : RULES) {
            infoPanel.getChildren().add(tick(rule));
        }
        infoPanel.setPadding(new Insets(20));
        infoPanel.getStyleClass().add("panel");

        HBox infoRow = new HBox(18, ratesPanel, infoPanel);
        for (VBox panel : List.of(ratesPanel, infoPanel)) {
            HBox.setHgrow(panel, Priority.ALWAYS);
            panel.setPrefWidth(10);
            panel.setMaxWidth(Double.MAX_VALUE);
        }

        return new VBox(18,
                pageHeader("Welcome back, " + currentUser,
                        "Manage guest bills, room prices and reservations with ease."),
                buildStats(),
                recentPanel,
                infoRow);
    }

    private Node buildStats() {
        Label billsValue = new Label();
        billsValue.textProperty().bind(Bindings.size(billList).asString());

        Label resValue = new Label();
        resValue.textProperty().bind(Bindings.size(resList).asString());

        Label billedValue = new Label();
        billedValue.textProperty().bind(Bindings.createStringBinding(
                () -> money(billList.stream().mapToDouble(Bill::getTotalCost).sum()), billList));

        Label outstandingValue = new Label();
        outstandingValue.textProperty().bind(Bindings.createStringBinding(
                () -> money(billList.stream().mapToDouble(b -> Math.max(0, b.getBalance())).sum()), billList));

        return new HBox(14,
                tile("Bills saved", billsValue, false),
                tile("Reservations", resValue, false),
                tile("Total billed", billedValue, false),
                tile("Outstanding balance", outstandingValue, true));
    }

    private Node tile(String caption, Label value, boolean accent) {
        value.getStyleClass().add("tile-value");
        Label cap = new Label(caption);
        cap.getStyleClass().add("tile-caption");
        VBox tile = new VBox(2, value, cap);
        tile.getStyleClass().add("tile");
        if (accent) {
            tile.getStyleClass().add("tile-accent");
        }
        HBox.setHgrow(tile, Priority.ALWAYS);
        tile.setMaxWidth(Double.MAX_VALUE);
        tile.setPrefWidth(200);
        return tile;
    }

    // ------------------------------------------------------------------
    // Page 2: New bill
    // ------------------------------------------------------------------
    private VBox buildNewBillPage() {
        billGuestField = new TextField();
        billGuestField.setPromptText("Guest full name");
        billRoomCombo = new ComboBox<>();
        billRoomCombo.getItems().addAll(ROOM_TYPES);
        billRoomCombo.setValue(DEFAULT_ROOM);
        billNightsField = new TextField();
        billNightsField.setPromptText("e.g. 3");
        billExtrasField = new TextField("0.00");
        billPaidField = new TextField("0.00");
        billNotesArea = new TextArea();
        billNotesArea.setPromptText("Optional notes");
        billNotesArea.setPrefRowCount(3);
        fill(billGuestField, billRoomCombo, billNightsField, billExtrasField, billPaidField, billNotesArea);

        GridPane grid = formGrid(2);
        addRow(grid, 0, "Guest name", billGuestField);
        addRow(grid, 1, "Room type", billRoomCombo);
        addRow(grid, 2, "Nights", billNightsField);
        addRow(grid, 3, "Extras (K)", billExtrasField);
        addRow(grid, 4, "Amount paid (K)", billPaidField);
        addRow(grid, 5, "Notes", billNotesArea);

        Button clearBtn = new Button("Clear form");
        clearBtn.setOnAction(e -> clearBillForm());
        Button saveBtn = new Button("Save bill");
        saveBtn.getStyleClass().add("primary");
        saveBtn.setOnAction(e -> saveBill());
        HBox buttons = new HBox(10, clearBtn, saveBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox formPanel = new VBox(16, panelTitle("Guest and stay details"), grid, buttons);
        formPanel.setPadding(new Insets(20));
        formPanel.getStyleClass().add("panel");
        HBox.setHgrow(formPanel, Priority.ALWAYS);

        // Live summary
        sumTotal = new Label("\u2014");
        sumTotal.getStyleClass().add("price-value");
        sumPaid = new Label("\u2014");
        sumPaid.getStyleClass().add("price-value");
        sumBalance = new Label("\u2014");
        sumBalance.getStyleClass().add("summary-big");
        sumNote = new Label();
        sumNote.getStyleClass().add("muted");
        sumNote.setWrapText(true);

        VBox summaryPanel = new VBox(6,
                panelTitle("Bill summary"),
                roomPreview(billRoomCombo),
                summaryRow("Total", sumTotal),
                summaryRow("Amount paid", sumPaid),
                summaryRow("Balance due", sumBalance),
                sumNote);
        VBox.setMargin(sumNote, new Insets(10, 0, 0, 0));
        summaryPanel.setPadding(new Insets(20));
        summaryPanel.setPrefWidth(300);
        summaryPanel.setMinWidth(260);
        summaryPanel.setMaxWidth(300);
        summaryPanel.getStyleClass().add("panel");

        billNightsField.textProperty().addListener((o, a, b) -> updateSummary());
        billExtrasField.textProperty().addListener((o, a, b) -> updateSummary());
        billPaidField.textProperty().addListener((o, a, b) -> updateSummary());
        billRoomCombo.valueProperty().addListener((o, a, b) -> updateSummary());
        updateSummary();

        HBox row = new HBox(18, formPanel, summaryPanel);
        row.setAlignment(Pos.TOP_LEFT);
        return new VBox(18,
                pageHeader("New Bill", "Enter the stay details. The summary updates as you type."),
                buildCheckInBanner(),
                row);
    }

    private Node summaryRow(String name, Label value) {
        Label n = new Label(name);
        n.getStyleClass().add("muted");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(n, spacer, value);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("price-row");
        return row;
    }

    private void updateSummary() {
        try {
            int nights = readNights(billNightsField);
            double extras = readMoney(billExtrasField, "Extras");
            double paid = readMoney(billPaidField, "Amount paid");
            Bill preview = new Bill("Preview", "Preview", billRoomCombo.getValue(), nights, extras, paid);
            sumTotal.setText(money(preview.getTotalCost()));
            sumPaid.setText(money(paid));
            sumBalance.setText(money(preview.getBalance()));
            sumNote.setText(nights >= 5
                    ? "A 5% long-stay discount is applied to the room charge."
                    : "Stays of 5 nights or more get 5% off the room charge.");
        } catch (IllegalArgumentException ex) {
            sumTotal.setText("\u2014");
            sumPaid.setText("\u2014");
            sumBalance.setText("\u2014");
            sumNote.setText("Enter the nights, extras and amount paid to see the totals.");
        }
    }

    private void saveBill() {
        try {
            String guest = readGuest(billGuestField);
            String room = billRoomCombo.getValue();
            int nights = readNights(billNightsField);
            double extras = readMoney(billExtrasField, "Extras");
            double paid = readMoney(billPaidField, "Amount paid");

            Bill bill = new Bill(String.format("B%03d", billCounter), guest, room, nights, extras, paid);
            if (paid > bill.getTotalCost()) {
                setStatus("Amount paid (" + money(paid) + ") is more than the total ("
                        + money(bill.getTotalCost()) + ").", Tone.ERROR);
                return;
            }

            billList.add(bill);
            billCounter++;
            Reservation converted = pendingCheckIn;
            clearBillForm();
            if (converted != null) {
                converted.setStatus("Checked-in (" + bill.getBillNumber() + ")");
                resTable.refresh();
                setStatus(String.format("Checked in %s. Reservation %s is now bill %s. Balance: %s",
                        guest, converted.getResNumber(), bill.getBillNumber(), money(bill.getBalance())), Tone.OK);
            } else {
                setStatus(String.format("Saved %s for %s. Balance: %s",
                        bill.getBillNumber(), guest, money(bill.getBalance())), Tone.OK);
            }
        } catch (IllegalArgumentException ex) {
            setStatus("Error: " + ex.getMessage(), Tone.ERROR);
        }
    }

    private void clearBillForm() {
        billGuestField.clear();
        billNightsField.clear();
        billExtrasField.setText("0.00");
        billPaidField.setText("0.00");
        billNotesArea.clear();
        billRoomCombo.setValue(DEFAULT_ROOM);
        pendingCheckIn = null;
        if (checkInBanner != null) {
            updateBanner();
        }
        billGuestField.requestFocus();
    }

    private Node buildCheckInBanner() {
        checkInBannerText = new Label();
        checkInBannerText.setWrapText(true);
        checkInBannerText.getStyleClass().add("banner-text");
        HBox.setHgrow(checkInBannerText, Priority.ALWAYS);
        checkInBannerText.setMaxWidth(Double.MAX_VALUE);

        Button cancel = new Button("Cancel check-in");
        cancel.setOnAction(e -> {
            clearBillForm();
            setStatus("Check-in cancelled. The reservation is still booked.", Tone.INFO);
        });

        checkInBanner = new HBox(14, checkInBannerText, cancel);
        checkInBanner.setAlignment(Pos.CENTER_LEFT);
        checkInBanner.getStyleClass().add("banner");
        updateBanner();
        return checkInBanner;
    }

    private void updateBanner() {
        boolean active = pendingCheckIn != null;
        checkInBanner.setVisible(active);
        checkInBanner.setManaged(active);
        if (active) {
            checkInBannerText.setText("Checking in reservation " + pendingCheckIn.getResNumber()
                    + " for " + pendingCheckIn.getGuestName() + ". The deposit of "
                    + money(pendingCheckIn.getDeposit())
                    + " is entered as the amount paid. Add any extras, then save the bill.");
        }
    }

    /** Turns the selected reservation into a pre-filled bill on the New Bill page. */
    private void startCheckIn() {
        Reservation selected = resTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Select a reservation in the table first.", Tone.ERROR);
            return;
        }
        if (!"Booked".equals(selected.getStatus())) {
            setStatus("Reservation " + selected.getResNumber() + " is already checked in.", Tone.ERROR);
            return;
        }

        billGuestField.setText(selected.getGuestName());
        billRoomCombo.setValue(selected.getRoomType());
        billNightsField.setText(String.valueOf(selected.getNights()));
        billExtrasField.setText("0.00");
        billPaidField.setText(String.format(Locale.US, "%.2f", selected.getDeposit()));
        billNotesArea.setText("Checked in from reservation " + selected.getResNumber() + ".");
        pendingCheckIn = selected;
        updateBanner();
        showPage(Page.NEW_BILL);
        setStatus("Reservation " + selected.getResNumber() + " loaded. Review the bill and press Save bill.", Tone.INFO);
    }

    /** Room picture that follows the selected room type. */
    private Node roomPreview(ComboBox<String> combo) {
        StackPane holder = new StackPane();
        holder.setAlignment(Pos.CENTER);
        Runnable refresh = () -> holder.getChildren().setAll(RoomPicture.create(combo.getValue(), 260, 148));
        combo.valueProperty().addListener((o, a, b) -> refresh.run());
        refresh.run();
        return holder;
    }

    // ------------------------------------------------------------------
    // Page 3: Reservations
    // ------------------------------------------------------------------
    private VBox buildReservationsPage() {
        resGuestField = new TextField();
        resGuestField.setPromptText("Guest full name");
        resRoomCombo = new ComboBox<>();
        resRoomCombo.getItems().addAll(ROOM_TYPES);
        resRoomCombo.setValue(DEFAULT_ROOM);
        resNightsField = new TextField();
        resNightsField.setPromptText("e.g. 3");
        resCheckInPicker = new DatePicker(LocalDate.now());
        resDepositField = new TextField("0.00");
        fill(resGuestField, resRoomCombo, resNightsField, resCheckInPicker, resDepositField);

        GridPane grid = formGrid(2);
        addRow(grid, 0, "Guest name", resGuestField);
        addRow(grid, 1, "Room type", resRoomCombo);
        addRow(grid, 2, "Nights", resNightsField);
        addRow(grid, 3, "Check-in date", resCheckInPicker);
        addRow(grid, 4, "Deposit (K)", resDepositField);

        Button clearBtn = new Button("Clear form");
        clearBtn.setOnAction(e -> clearReservationForm());
        Button reserveBtn = new Button("Reserve");
        reserveBtn.getStyleClass().add("primary");
        reserveBtn.setOnAction(e -> saveReservation());
        HBox buttons = new HBox(10, clearBtn, reserveBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox formPanel = new VBox(16, panelTitle("New reservation"), grid, buttons);
        formPanel.setPadding(new Insets(20));
        formPanel.getStyleClass().add("panel");
        HBox.setHgrow(formPanel, Priority.ALWAYS);

        VBox previewPanel = new VBox(12, panelTitle("Room preview"), roomPreview(resRoomCombo));
        previewPanel.setPadding(new Insets(20));
        previewPanel.setPrefWidth(300);
        previewPanel.setMinWidth(260);
        previewPanel.setMaxWidth(300);
        previewPanel.getStyleClass().add("panel");

        HBox topRow = new HBox(18, formPanel, previewPanel);
        topRow.setAlignment(Pos.TOP_LEFT);

        resTable = buildReservationTable();
        resTable.setPrefHeight(260);

        Button checkInBtn = new Button("Check in and create bill");
        checkInBtn.getStyleClass().add("primary");
        checkInBtn.setOnAction(e -> startCheckIn());
        Button deleteBtn = new Button("Delete selected reservation");
        deleteBtn.getStyleClass().add("danger");
        deleteBtn.setOnAction(e -> deleteSelectedReservation());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox titleRow = new HBox(10, panelTitle("Reservations"), spacer, checkInBtn, deleteBtn);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox tablePanel = new VBox(12, titleRow, resTable);
        tablePanel.setPadding(new Insets(20));
        tablePanel.getStyleClass().add("panel");

        return new VBox(18,
                pageHeader("Reservations",
                        "Book a room in advance. When the guest arrives, select the reservation and check them in to turn it into a bill."),
                topRow,
                tablePanel);
    }

    /** Adds a reservation to the list and returns its number (also used by ReservationScreen). */
    public String addReservation(String guest, String room, LocalDate checkIn, int nights, double deposit) {
        String resNum = String.format("R%03d", resCounter++);
        resList.add(new Reservation(resNum, guest, room, checkIn, nights, deposit));
        return resNum;
    }

    private void saveReservation() {
        try {
            String guest = readGuest(resGuestField);
            String room = resRoomCombo.getValue();
            LocalDate checkIn = resCheckInPicker.getValue();
            if (checkIn == null) {
                throw new IllegalArgumentException("Please pick a check-in date.");
            }
            int nights = readNights(resNightsField);
            double deposit = readMoney(resDepositField, "Deposit");

            String resNum = addReservation(guest, room, checkIn, nights, deposit);
            clearReservationForm();
            setStatus(String.format("Reserved %s for %s. Check-out: %s",
                    resNum, guest, checkIn.plusDays(nights)), Tone.OK);
        } catch (IllegalArgumentException ex) {
            setStatus("Error: " + ex.getMessage(), Tone.ERROR);
        }
    }

    private void clearReservationForm() {
        resGuestField.clear();
        resNightsField.clear();
        resDepositField.setText("0.00");
        resRoomCombo.setValue(DEFAULT_ROOM);
        resCheckInPicker.setValue(LocalDate.now());
        resGuestField.requestFocus();
    }

    private void deleteSelectedReservation() {
        Reservation selected = resTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Select a reservation in the table first.", Tone.ERROR);
            return;
        }
        confirm("Delete reservation " + selected.getResNumber() + " for " + selected.getGuestName() + "?", () -> {
            resList.remove(selected);
            setStatus("Deleted reservation " + selected.getResNumber() + ".", Tone.OK);
        });
    }

    // ------------------------------------------------------------------
    // Page 4: Bills and receipts
    // ------------------------------------------------------------------
    private VBox buildBillsPage() {
        billTable = buildBillTable(false);
        billTable.setPrefHeight(420);

        Button receiptBtn = new Button("View receipt");
        receiptBtn.getStyleClass().add("primary");
        receiptBtn.setOnAction(e -> viewSelectedReceipt());
        Button deleteBtn = new Button("Delete selected bill");
        deleteBtn.getStyleClass().add("danger");
        deleteBtn.setOnAction(e -> deleteSelectedBill());
        Label hint = new Label("Tip: double-click a bill to open its receipt.");
        hint.getStyleClass().add("muted");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox actions = new HBox(10, receiptBtn, deleteBtn, spacer, hint);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox panel = new VBox(14, actions, billTable);
        panel.setPadding(new Insets(20));
        panel.getStyleClass().add("panel");

        return new VBox(18,
                pageHeader("Bills & Receipts", "Review saved bills, open a receipt or remove a bill."),
                panel);
    }

    private void viewSelectedReceipt() {
        Bill selected = billTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Select a bill to view its receipt.", Tone.ERROR);
            return;
        }
        viewReceipt(selected);
    }

    private void deleteSelectedBill() {
        Bill selected = billTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Select a bill in the table first.", Tone.ERROR);
            return;
        }
        confirm("Delete bill " + selected.getBillNumber() + " for " + selected.getGuestName() + "?", () -> {
            billList.remove(selected);
            setStatus("Deleted " + selected.getBillNumber() + ".", Tone.OK);
        });
    }

    private void viewReceipt(Bill bill) {
        String receipt = String.format(
                "=====================================\n" +
                        "          KEYHAVEN RECEIPT           \n" +
                        "=====================================\n" +
                        "Bill #      : %s\n" +
                        "Guest       : %s\n" +
                        "Room type   : %s\n" +
                        "Nights      : %d\n" +
                        "Total       : %s\n" +
                        "Paid        : %s\n" +
                        "Balance     : %s\n" +
                        "=====================================\n" +
                        "Served by   : %s\n" +
                        "=====================================",
                bill.getBillNumber(),
                bill.getGuestName(),
                bill.getRoomType(),
                bill.getNights(),
                money(bill.getTotalCost()),
                money(bill.getAmountPaid()),
                money(bill.getBalance()),
                currentUser);

        TextArea area = new TextArea(receipt);
        area.setEditable(false);
        area.setWrapText(false);
        area.setPrefWidth(430);
        area.setPrefHeight(290);
        area.setStyle("-fx-font-family: 'Consolas', monospace; -fx-text-fill: #ecd29b;");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("Receipt - " + bill.getBillNumber());
        alert.setHeaderText("Receipt for " + bill.getGuestName());
        alert.getDialogPane().setContent(area);
        styleDialog(alert);
        alert.showAndWait();
    }

    // ------------------------------------------------------------------
    // Page 5: Room prices
    // ------------------------------------------------------------------
    private VBox buildPricesPage() {
        VBox rooms = new VBox(14);
        for (String[] rate : RATES) {
            String room = rate[0];
            Label name = new Label(room);
            name.getStyleClass().add("rate-name");
            Label price = new Label(rate[1]);
            price.getStyleClass().add("rate-price");
            Label per = new Label("per night");
            per.getStyleClass().add("tile-caption");
            HBox priceLine = new HBox(8, price, per);
            priceLine.setAlignment(Pos.BASELINE_LEFT);
            Label blurb = new Label(ROOM_BLURBS.getOrDefault(room, ""));
            blurb.getStyleClass().add("muted");
            blurb.setWrapText(true);

            VBox info = new VBox(6, name, priceLine, blurb);
            info.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(info, Priority.ALWAYS);

            HBox card = new HBox(22, RoomPicture.create(room, 260, 148), info);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(14));
            card.getStyleClass().add("panel");
            rooms.getChildren().add(card);
        }

        VBox rules = new VBox(12, panelTitle("Pricing rules"));
        for (String rule : RULES) {
            rules.getChildren().add(tick(rule));
        }
        rules.setPadding(new Insets(20));
        rules.getStyleClass().add("panel");

        return new VBox(18,
                pageHeader("Room Prices", "Current nightly rates and the rules applied to every bill."),
                rooms,
                rules);
    }

    // ------------------------------------------------------------------
    // Tables
    // ------------------------------------------------------------------
    private TableView<Bill> buildBillTable(boolean compact) {
        TableView<Bill> table = new TableView<>();
        SortedList<Bill> sorted = new SortedList<>(filteredBills);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        TableColumn<Bill, String> colBill = column("Bill", "billNumber");
        TableColumn<Bill, String> colGuest = column("Guest", "guestName");
        TableColumn<Bill, String> colRoom = column("Room", "roomType");
        TableColumn<Bill, Integer> colNights = column("Nights", "nights");
        TableColumn<Bill, Double> colTotal = moneyColumn("Total", "totalCost", false);
        TableColumn<Bill, Double> colPaid = moneyColumn("Paid", "amountPaid", false);
        TableColumn<Bill, Double> colBalance = moneyColumn("Balance", "balance", true);

        if (compact) {
            table.getColumns().addAll(colBill, colGuest, colRoom, colTotal, colBalance);
        } else {
            table.getColumns().addAll(colBill, colGuest, colRoom, colNights, colTotal, colPaid, colBalance);
        }
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No bills to show."));
        table.setRowFactory(tv -> {
            TableRow<Bill> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    viewReceipt(row.getItem());
                }
            });
            return row;
        });
        return table;
    }

    private TableView<Reservation> buildReservationTable() {
        TableView<Reservation> table = new TableView<>();
        SortedList<Reservation> sorted = new SortedList<>(filteredRes);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        TableColumn<Reservation, String> colNum = column("Res #", "resNumber");
        TableColumn<Reservation, String> colGuest = column("Guest", "guestName");
        TableColumn<Reservation, String> colRoom = column("Room", "roomType");
        TableColumn<Reservation, LocalDate> colIn = column("Check-in", "checkIn");
        TableColumn<Reservation, LocalDate> colOut = column("Check-out", "checkOut");
        TableColumn<Reservation, Integer> colNights = column("Nights", "nights");
        TableColumn<Reservation, Double> colDeposit = moneyColumn("Deposit", "deposit", false);
        TableColumn<Reservation, String> colStatus = column("Status", "status");

        table.getColumns().addAll(colNum, colGuest, colRoom, colIn, colOut, colNights, colDeposit, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No reservations to show."));
        return table;
    }

    private <S, T> TableColumn<S, T> column(String title, String property) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        return col;
    }

    /** Money column shown as "K 1,662.50"; optionally colours outstanding balances. */
    private <S> TableColumn<S, Double> moneyColumn(String title, String property, boolean highlightBalance) {
        TableColumn<S, Double> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setCellFactory(c -> new TableCell<S, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(money(value));
                setAlignment(Pos.CENTER_RIGHT);
                if (highlightBalance && !isSelected()) {
                    setStyle(value > 0 ? "-fx-text-fill: #ff9a93;" : "-fx-text-fill: #7ee0a0;");
                } else {
                    setStyle("");
                }
            }
        });
        return col;
    }

    // ------------------------------------------------------------------
    // Small UI helpers
    // ------------------------------------------------------------------
    private VBox pageHeader(String title, String subtitle) {
        Label t = new Label(title);
        t.getStyleClass().add("page-title");
        Label s = new Label(subtitle);
        s.getStyleClass().add("page-subtitle");
        VBox box = new VBox(3, t, s);
        box.setPadding(new Insets(4, 0, 4, 0));
        return box;
    }

    private Label panelTitle(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("panel-title");
        return l;
    }

    private Node priceRow(String name, String price) {
        Label n = new Label(name);
        n.getStyleClass().add("price-name");
        Label p = new Label(price);
        p.getStyleClass().add("price-value");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(n, spacer, p);
        row.getStyleClass().add("price-row");
        return row;
    }

    private Node tick(String text) {
        Label mark = new Label("\u2713");
        mark.getStyleClass().add("tick");
        Label body = new Label(text);
        body.setWrapText(true);
        HBox row = new HBox(10, mark, body);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void fill(Region... regions) {
        for (Region r : regions) {
            r.setMaxWidth(Double.MAX_VALUE);
        }
    }

    private GridPane formGrid(int columns) {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        for (int i = 0; i < columns; i++) {
            ColumnConstraints c = new ColumnConstraints();
            if (i % 2 == 0) {
                c.setMinWidth(110);
                c.setPrefWidth(columns == 2 ? 130 : 110);
            } else {
                c.setHgrow(Priority.ALWAYS);
            }
            grid.getColumnConstraints().add(c);
        }
        return grid;
    }

    private void addRow(GridPane grid, int row, String label, Node input) {
        addRow(grid, row, label, input, null, null);
    }

    private void addRow(GridPane grid, int row, String label, Node input, String label2, Node input2) {
        Label l = new Label(label);
        l.getStyleClass().add("field-label");
        grid.add(l, 0, row);
        grid.add(input, 1, row);
        if (label2 != null && input2 != null) {
            Label l2 = new Label(label2);
            l2.getStyleClass().add("field-label");
            grid.add(l2, 2, row);
            grid.add(input2, 3, row);
        }
    }

    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------
    private String readGuest(TextField field) {
        String guest = field.getText().trim();
        if (guest.isEmpty()) {
            throw new IllegalArgumentException("Guest name is required.");
        }
        return guest;
    }

    private int readNights(TextField field) {
        try {
            int nights = Integer.parseInt(field.getText().trim());
            if (nights <= 0) {
                throw new NumberFormatException();
            }
            return nights;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Nights must be a whole number greater than 0.");
        }
    }

    private double readMoney(TextField field, String name) {
        try {
            double value = Double.parseDouble(field.getText().trim());
            if (!Double.isFinite(value) || value < 0) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(name + " must be a number, 0 or more.");
        }
    }

    private static String money(double value) {
        return String.format("K %,.2f", value);
    }

    // ------------------------------------------------------------------
    // Dialogs and navigation
    // ------------------------------------------------------------------
    private void confirm(String message, Runnable onYes) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.initOwner(stage);
        alert.setHeaderText(null);
        styleDialog(alert);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                onYes.run();
            }
        });
    }

    private void styleDialog(Alert alert) {
        if (CSS != null) {
            alert.getDialogPane().getStylesheets().add(CSS);
        }
    }

    private void logout() {
        confirm("Log out of " + currentUser + "?", () -> {
            LoginScreen login = new LoginScreen();
            stage.setScene(login.createScene(stage));
            stage.setTitle("Keyhaven - Login");
        });
    }
}