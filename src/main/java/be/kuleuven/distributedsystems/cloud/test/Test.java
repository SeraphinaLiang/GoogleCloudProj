package be.kuleuven.distributedsystems.cloud.test;

import be.kuleuven.distributedsystems.cloud.email.EmailSending;
import be.kuleuven.distributedsystems.cloud.entities.Booking;
import com.google.gson.Gson;
import io.opencensus.internal.DefaultVisibilityForTesting;

public class Test {

    public static void main(String[] args){
        /*String s = "{tickets=[{showId=7e97e389-9cf6-45fe-b794-b0cf9fa9658f, company=unreliabletheatrecompany.com, seatId=dec5b3ba-0377-4752-a5d1-0fbf3b294af0, ticketId=0ddc82c5-d307-4f7b-bfb9-975b859c66a4, customer=mary@qq.com}], id=460af720-8d44-4f49-a8bf-50cd2b33f29a, time={dayOfWeek=FRIDAY, month=DECEMBER, hour=14, dayOfMonth=3, year=2021, dayOfYear=337, monthValue=12, nano=834621000, chronology={calendarType=iso8601, id=ISO}, minute=35, second=19}, customer=mary@qq.com}";
        Gson gson = new Gson();
        Booking b = gson.fromJson(s, Booking.class);
        System.out.println(b.getCustomer());*/

        EmailSending e = new EmailSending();
        e.sendEmail();
    }
}
