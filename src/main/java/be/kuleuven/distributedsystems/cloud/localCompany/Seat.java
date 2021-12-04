package be.kuleuven.distributedsystems.cloud.localCompany;/**
 * @author ：mmzs
 * @date ：Created in 2021/12/4 19:04
 * @description：Entity class Seat for local company
 * @modified By：
 * @version: $
 */

import java.io.Serializable;

/**
 * @author     ：mmzs
 * @date       ：Created in 2021/12/4 19:04
 * @description：Entity class Seat for local company
 * @modified By：
 * @version: $
 */

public class Seat implements Serializable {
    private String seatID;
    private String time;
    private String name;
    private boolean available;
    private String type;
    private double price;

    public Seat(){}

    public Seat(String seatId, String time, String name, boolean available, String type, double price){
        this.seatID = seatId;
        this.time = time;
        this.name = name;
        this.available = available;
        this.type = type;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public String getSeatID() {
        return seatID;
    }

    public void setSeatID(String seatID) {
        this.seatID = seatID;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
