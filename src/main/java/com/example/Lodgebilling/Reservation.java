package com.example.Lodgebilling;

import java.time.LocalDate;

public class Reservation {
    private String resNumber;
    private String guestName;
    private String roomType;
    private LocalDate checkIn;
    private int nights;
    private LocalDate checkOut;
    private double deposit;
    private String status;   // "Booked" or "Checked-in"

    public Reservation(String resNumber, String guestName, String roomType,
                       LocalDate checkIn, int nights, double deposit) {
        this.resNumber = resNumber;
        this.guestName = guestName;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.nights = nights;
        this.checkOut = checkIn.plusDays(nights);
        this.deposit = deposit;
        this.status = "Booked";
    }

    public String getResNumber() { return resNumber; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public LocalDate getCheckIn() { return checkIn; }
    public int getNights() { return nights; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getDeposit() { return deposit; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}