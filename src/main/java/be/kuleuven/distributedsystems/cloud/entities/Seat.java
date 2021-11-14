package be.kuleuven.distributedsystems.cloud.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Seat {
    private String company;
    private UUID showId;
    private UUID seatId;
    private LocalDateTime time;
    private String type;
    private String name;
    private double price;

    public Seat() {
    }

    public Seat(String company, UUID showId, UUID seatId, LocalDateTime time, String type, String name, double price) {
        this.company = company;
        this.showId = showId;
        this.seatId = seatId;
        this.time = time;
        this.type = type;
        this.name = name;
        this.price = price;
    }

    public String getCompany() {
        return company;
    }

    public UUID getShowId() {
        return showId;
    }

    public UUID getSeatId() {
        return this.seatId;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Seat)) {
            return false;
        }
        var other = (Seat) o;
        return this.company.equals(other.company)
                && this.showId.equals(other.showId)
                && this.seatId.equals(other.seatId);
    }

    @Override
    public int hashCode() {
        return this.company.hashCode() * this.showId.hashCode() * this.seatId.hashCode();
    }
}
