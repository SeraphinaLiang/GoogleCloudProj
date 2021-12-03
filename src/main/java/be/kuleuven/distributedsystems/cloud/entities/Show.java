package be.kuleuven.distributedsystems.cloud.entities;

import java.io.Serializable;

public class Show implements Serializable {
    private String company;
    private String showId;
    private String name;
    private String location;
    private String image;

    public Show() {}

    public Show(String company, String showId, String name, String location, String image) {
        this.company = company;
        this.showId = showId;
        this.name = name;
        this.location = location;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Show)) {
            return false;
        }
        var other = (Show) o;
        return this.company.equals(other.company)
                && this.showId.equals(other.showId);
    }

    @Override
    public int hashCode() {
        return this.company.hashCode() * this.showId.hashCode();
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
