package be.kuleuven.distributedsystems.cloud.test;

import be.kuleuven.distributedsystems.cloud.auth.PemUtils;
import be.kuleuven.distributedsystems.cloud.auth.SendHttps;
import be.kuleuven.distributedsystems.cloud.email.EmailSending;
import be.kuleuven.distributedsystems.cloud.entities.Booking;
import com.google.gson.Gson;
import io.opencensus.internal.DefaultVisibilityForTesting;
import org.eclipse.jetty.util.IO;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class Test {

    public static void main(String[] args){
        /*String s = "{tickets=[{showId=7e97e389-9cf6-45fe-b794-b0cf9fa9658f, company=unreliabletheatrecompany.com, seatId=dec5b3ba-0377-4752-a5d1-0fbf3b294af0, ticketId=0ddc82c5-d307-4f7b-bfb9-975b859c66a4, customer=mary@qq.com}], id=460af720-8d44-4f49-a8bf-50cd2b33f29a, time={dayOfWeek=FRIDAY, month=DECEMBER, hour=14, dayOfMonth=3, year=2021, dayOfYear=337, monthValue=12, nano=834621000, chronology={calendarType=iso8601, id=ISO}, minute=35, second=19}, customer=mary@qq.com}";
        Gson gson = new Gson();
        Booking b = gson.fromJson(s, Booking.class);
        System.out.println(b.getCustomer());*/

        //EmailSending e = new EmailSending();
        //e.sendEmail("yinqi.liang@student.kuleuven.be","title","this is content");

        try {
            String pri =
                    "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDLetkE0HWaNQ6j" +
                            "+xrku9Vdr4j2akTkU3e2Qk5XH98Mztq2qnjmSM3Aayor/E+50b7r+H5dngAf+xcm" +
                            "Ydjw6FD9JGpRUKNyBLaKsaIj/SHxn7E2LL9RNjHGnYzAVX291JtGlissQeoTEqmX" +
                            "9yb9ohfLOpaPp1Ymz2bx/PfHWKeA+vI9bxEWMgG38DZuSL6fhasUoI7YWEDGNFp5" +
                            "M7Y8hI8gCWiquNNFfcnZrcI6HOLoPqkMxc5LB8ImuC+NxniPRkJv4ouQABx4ICoo" +
                            "RGt8yvDJsSNEcM47fvkJ9VUrGTOQOC6Vbv3zQA9nVbywf5jL7xAST7NxFBfRVmqY" +
                            "NOTPILNBAgMBAAECggEAQUx9q4lYTDH9rksNkNzkdom36tgknp9YmgFCUbxVIK+b" +
                            "SaaAYOp5OOhLIs14KlowqlpW9yUWxiyWe8dHztvG7c3LXqgBo7v2dqDVtzXrODa2" +
                            "sHuAtAevxpzVx/1Hem1pnSfg1/WZMCC7kxyKAzgK6bm2Coi5YYAKedrkCzGco4uS" +
                            "P29JR9xOW0rykPSbXrCegs32SfO29UoJ8U4ToLw1Qmybz5qy02u5IJ8q7vZgPMQq" +
                            "5IQg/NpmcOIUa07TaGSSzpvG251dIImLILlKU+3UNeskrIFTe7X4l2drzfvePRmI" +
                            "w/uHz2SS9Ztc9GWlWtCLpWLp6QV+zmVj2RC0AvwHPwKBgQDpD12aKB1JiDZbG3LM" +
                            "7d+hrjIWcppym1nuyjde6CU8cOr4YUuHGyzfyzT+B8j1lwEIveCda22NHKwi+SFr" +
                            "UUCEoJMUXyi1bxNxo+vfax0rDjJLL82WHgZoAWBzqrCkMWkky0Mi7G/7WtKY6VD/" +
                            "eschirRj3a2PWpxtg5wH58lSxwKBgQDfgh+Aqn5fJstCNd6seRcrQIJiBqPM7QAH" +
                            "rCMd1h42v0kDoQGE+9eJ1kf8TYdrTd01SB8FZP40V4wdjmEPqXFGQ6U2k212+FEg" +
                            "anA1lTEN3KkUngmePVURlGp1SSBPI1BPD8ZH65Kif4YMVMlK5FNNReNd5B8p0eyq" +
                            "JtSabD5BtwKBgGBWG6jnQAPvLBjI5dFT2obojIe+45zQHoKYT/8JEqtDOb1l6mR0" +
                            "lT4Er2j6KtVpj+HfKwOnLmeQHI9wT6Ieuf6YqXNYSmmE+pKU4aE+k5YCjkOKBP8y" +
                            "dg9z0jy9p7qXOhEdfCjpdvh9eGQAvZx9UebJgPtu3JlPKS4TouGZrBxtAoGBAI38" +
                            "WsjhBlCqE1unXyLP9gD7BRkCwNHxCSDWoKRWnnEHCXotsQpq9lzQ7IACPHHVUB6G" +
                            "B3bk1nwn1ZH9HfnWGWialnzaISL/0oG2PDw936C/ugWn89I7giwdzZdechD0DRN8" +
                            "oOiVZVyniF+TCSDzdVvUdwDxZz6o5iCddFf5RX93AoGAA78D5AIjhypHx6yuTE7Q" +
                            "PLQU0KiDyNz7yL4C/1s4YBjGnR07KGReTkgT6Xbd9jdRYUtjoOsb53I3J0aLRrZG" +
                            "plryDvxNR/z6XfBZOVjZpCx3xub3kG95PLT85fKNCoe7MVK0rvV8aOK4LNwOzdwV" +
                            "VFpPThIFcwCrvULh9yXWDxc=";
            byte[] a = Base64.getDecoder().decode(pri.getBytes());
            System.out.println(Arrays.toString(a));
            PemUtils.readPrivateKeyFromFile("src/main/resources/rsa-private.pem", "RSA");

            SendHttps s = new SendHttps();
            s.getPublicKeys();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
