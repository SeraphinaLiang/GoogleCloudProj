package be.kuleuven.distributedsystems.cloud.localCompany;/**
 * @author ：mmzs
 * @date ：Created in 2021/12/4 19:02
 * @description：Entity class Show for local company
 * @modified By：
 * @version: $
 */

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author     ：mmzs
 * @date       ：Created in 2021/12/4 19:02
 * @description：Entity class Show for local company
 * @modified By：
 * @version: $
 */

public class Show implements Serializable {
    private String showID;
    private String company;
    private String name;
    private String location;
    private String image;
    private Map<String, Seat> seats;

    public Show(){}

    public Show(String showID, String company, String name, String location, String image, Map<String, Seat> seats){
        this.showID = showID;
        this.company = company;
        this.name = name;
        this.location = location;
        this.image = image;
        this.seats = seats;
    }

    public String getShowID() {
        return showID;
    }

    public void setShowID(String showID) {
        this.showID = showID;
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

    public Map<String, Seat> getSeats() {
        return seats;
    }

    public void setSeats(Map<String, Seat> seats) {
        this.seats = seats;
    }
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}
