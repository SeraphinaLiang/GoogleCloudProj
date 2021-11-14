package be.kuleuven.distributedsystems.cloud.entities;

import java.io.Serializable;
import java.util.UUID;

public class Quote implements Serializable {

    private String company;
    private UUID showId;
    private UUID seatId;

    public Quote() {
    }

    public Quote(String company, UUID showId, UUID seatId) {
        this.company = company;
        this.showId = showId;
        this.seatId = seatId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public UUID getShowId() {
        return showId;
    }

    public void setShowId(UUID showId) {
        this.showId = showId;
    }

    public UUID getSeatId() {
        return this.seatId;
    }

    public void setSeatId(UUID seatId) {
        this.seatId = seatId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Quote)) {
            return false;
        }
        var other = (Quote) o;
        return this.company.equals(other.company)
                && this.showId.equals(other.showId)
                && this.seatId.equals(other.seatId);
    }

    @Override
    public int hashCode() {
        return this.company.hashCode() * this.showId.hashCode() * this.seatId.hashCode();
    }
}
