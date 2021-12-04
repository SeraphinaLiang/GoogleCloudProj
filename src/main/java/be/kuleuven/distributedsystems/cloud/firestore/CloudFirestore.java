package be.kuleuven.distributedsystems.cloud.firestore;

import be.kuleuven.distributedsystems.cloud.entities.Booking;
import be.kuleuven.distributedsystems.cloud.entities.Ticket;
import be.kuleuven.distributedsystems.cloud.localCompany.Seat;
import be.kuleuven.distributedsystems.cloud.localCompany.Show;
import ch.qos.logback.core.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.common.collect.Lists;
import com.google.errorprone.annotations.Var;
import net.minidev.json.parser.JSONParser;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class CloudFirestore implements ApplicationRunner {

    private Firestore firestore = null;

    private static final String TIMEPATTERN = "yyyy-MM-dd HH:mm:ss";

    public CloudFirestore() {

        if (firestore == null) {
            initialDB("true-bit-333719", "true-bit-333719-2edd7ecb552b.json");
        }
    }

    private void initialDB(String projectId, String jsonPath) {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

            FirestoreOptions firestoreOptions =
                    FirestoreOptions.getDefaultInstance().toBuilder()
                            .setProjectId(projectId)
                            .setCredentials(credentials)
                            .build();
            Firestore db = firestoreOptions.getService();
            this.firestore = db;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * to store all bookings in Cloud Firestore
     */
    public void addBookingToDB(Booking booking) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("customer", booking.getCustomer());
            data.put("id", booking.getId().toString());
            data.put("tickets", new ArrayList<Map<String, Object>>(){{
                for (Ticket t:booking.getTickets()){
                    Map<String, Object> map = new HashMap<>();
                    map.put("company", t.getCompany());
                    map.put("customer", t.getCustomer());
                    map.put("seatID", t.getSeatId().toString());
                    map.put("showID", t.getShowId().toString());
                    map.put("ticketID", t.getTicketId().toString());
                    add(map);
                }
            }});
            data.put("time", booking.getTime().format(DateTimeFormatter.ofPattern(TIMEPATTERN)));
            ApiFuture<WriteResult> addResult = firestore.collection("bookings").document(booking.getId().toString()).set(data);

            if (!addResult.isDone()) {

            }
            // block on response if required
            Timestamp addBookingTime = addResult.get().getUpdateTime();
        } catch (ExecutionException | InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public void deleteBookingFromDB(String bookingID) {
        try {
            // asynchronously delete a document
            ApiFuture<WriteResult> deleteResult = firestore.collection("bookings").document(bookingID).delete();
            if (!deleteResult.isDone()) {

            }
            Timestamp deleteBookingTime = deleteResult.get().getUpdateTime();
        } catch (ExecutionException | InterruptedException e1) {
            e1.printStackTrace();
        }

    }

    public List<Booking> getAllBookingsFromDB() {
        List<Booking> bookings = new ArrayList<>();
        try {
            //asynchronously retrieve all documents
            ApiFuture<QuerySnapshot> future = firestore.collection("bookings").get();
            // future.get() blocks on response
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {

                Map<String, Object> map = document.getData();

                for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {

                    System.out.println(document.getData()+"\n----------------");  //JSON object string
                    System.out.println(stringObjectEntry+"\n-----------------");  // object string




                    //Booking b = document.toObject(Booking.class);
                   // Booking b = (Booking) stringObjectEntry.getValue();
                  //  bookings.add(b);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public ArrayList<Booking> getBookingsByCustomerFromDB(String customer) {
        ArrayList<Booking> bookings = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future =
                    firestore.collection("bookings").whereEqualTo("customer", customer).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                Map<String, Object> map = document.getData();
                Booking b = new Booking(UUID.fromString(map.get("id").toString()), LocalDateTime.parse(map.get("time").toString(), DateTimeFormatter.ofPattern(TIMEPATTERN)),
                        new ArrayList<>(){{
                            for (Object o:(ArrayList)map.get("tickets")){
                                Map<String, String> m = (Map)o;
                                add(new Ticket(m.get("company"), UUID.fromString(m.get("showID")), UUID.fromString(m.get("seatID")), UUID.fromString(m.get("ticketID")), m.get("customer")));
                            }
                        }}, map.get("customer").toString());
                bookings.add(b);
//                Map<String, Object> map = document.getData();
//                assert map != null;
//                for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
//
//                    //System.out.println(stringObjectEntry.getKey());
//                    //System.out.println(stringObjectEntry.getValue());
//                    //System.out.println(document.toString());
//                    //Booking b = document.toObject(Booking.class);
//                   // Booking b = (Booking) stringObjectEntry.getValue();
//                   // bookings.add(b);
//                }

            }
        } catch (ExecutionException | InterruptedException e1) {
            e1.printStackTrace();
        }
        return bookings;

    }

    public List<be.kuleuven.distributedsystems.cloud.entities.Show> getLocalShowsInPlatform(){
        List<be.kuleuven.distributedsystems.cloud.entities.Show> shows = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future =
                    firestore.collection("shows").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document:documents){
                Show show = document.toObject(Show.class);
                shows.add(new be.kuleuven.distributedsystems.cloud.entities.Show(show.getCompany(), UUID.fromString(show.getShowID()), show.getName(),
                        show.getLocation(), show.getImage()));
            }
        }catch (ExecutionException|InterruptedException e){
            e.printStackTrace();
        }

        return shows;
    }

    /**
     * local company persist all shows, seats and tickets in Cloud Firestore.
     */

    @Override
    public void run(ApplicationArguments args) throws Exception{
        CollectionReference collectionShows = firestore.collection("shows");
        ApiFuture<QuerySnapshot> future =
                collectionShows.get();
        if (!future.get().isEmpty()) return;
        else{
            Map<String,Object> json =
                    new ObjectMapper().readValue(new File("src/main/resources/data.json"), HashMap.class);
            for (Object object:(ArrayList)json.get("shows")) {
                Map<String, Object> showmap = (Map)object;
                Map<String, Seat> seats = new HashMap<>();
                for (Object o:(ArrayList)showmap.get("seats")) {
                    Map<String, Object> seatData = (Map)o;
                    UUID seat_uuid = UUID.randomUUID();
                    seats.put(seat_uuid.toString(), new Seat(seat_uuid.toString(), (String)seatData.get("time"),
                            (String)seatData.get("name"), true, (String)seatData.get("type"), ((Integer)seatData.get("price")).doubleValue()));
                }
                UUID show_uuid = UUID.randomUUID();
                Show show = new Show(show_uuid.toString(), "localCompany", (String)showmap.get("name"), (String)showmap.get("location"), (String)showmap.get("image"), seats);
                collectionShows.document(show_uuid.toString()).set(show);
            }
        }
    }
}
