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
    private String showId;
    private String company;
    private String name;
    private String location;
    private String image;
    private Map<String, List<String>> seats;

    public Show(){}

    public Show(String showId, String company, String name, String location, String image, Map<String, List<String>> seats){
        this.showId = showId;
        this.company = company;
        this.name = name;
        this.location = location;
        this.image = image;
        this.seats = seats;
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

    public Map<String, List<String>> getSeats() {
        return seats;
    }

    public void setSeats(Map<String, List<String>> seats) {
        this.seats = seats;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}
