package be.kuleuven.distributedsystems.cloud.localCompany;/**
 * @author ：mmzs
 * @date ：Created in 2021/12/4 21:13
 * @description：
 * @modified By：
 * @version: $
 */

import java.io.Serializable;

/**
 * @author     ：mmzs
 * @date       ：Created in 2021/12/4 21:13
 * @description：
 * @modified By：
 * @version: $
 */

public class Ticket implements Serializable {
    private String company;
    private String showId;
    private String seatId;
    private String ticketId;
    private String customer;

    public Ticket(){}

    public Ticket(String company, String showId, String seatId, String ticketId, String customer) {
        this.company = company;
        this.showId = showId;
        this.seatId = seatId;
        this.ticketId = ticketId;
        this.customer = customer;
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

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
