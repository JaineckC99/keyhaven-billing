package com.example.Lodgebilling;

public class Bill {
    private String billNumber;
    private String guestName;
    private String roomType;
    private int nights;
    private double extras;
    private double amountPaid;
    private double totalCost;
    private double balance;

    public Bill(String billNumber, String guestName, String roomType, int nights, double extras, double amountPaid) {
        this.billNumber = billNumber;
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
        this.extras = extras;
        this.amountPaid = amountPaid;

        // Calculate totals upon creation
        calculateBill();
    }

    private void calculateBill() {
        double roomRate = 0;

        // 1. Determine Room Rate based on screenshot rules
        switch (roomType) {
            case "Standard": roomRate = 350.00; break;
            case "Deluxe":   roomRate = 550.00; break;
            case "Family":   roomRate = 750.00; break;
        }

        double roomCharge = roomRate * nights;

        // 2. Apply 5% discount if 5 nights or more
        if (nights >= 5) {
            roomCharge = roomCharge * 0.95; // 5% off
        }

        // 3. Extras get no discount. Total = Discounted Room + Extras
        this.totalCost = roomCharge + extras;

        // 4. Calculate Balance
        this.balance = totalCost - amountPaid;
    }

    // Getters for the TableView
    public String getBillNumber() { return billNumber; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public int getNights() { return nights; }
    public double getTotalCost() { return totalCost; }
    public double getAmountPaid() { return amountPaid; }
    public double getBalance() { return balance; }
}