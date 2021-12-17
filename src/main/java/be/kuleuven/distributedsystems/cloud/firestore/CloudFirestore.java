package be.kuleuven.distributedsystems.cloud.firestore;

import be.kuleuven.distributedsystems.cloud.entities.Booking;
import be.kuleuven.distributedsystems.cloud.localCompany.Ticket;
import be.kuleuven.distributedsystems.cloud.localCompany.Seat;
import be.kuleuven.distributedsystems.cloud.localCompany.Show;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class CloudFirestore {


    private Firestore firestore = null;
    private static final String projectId = "true-bit-333719";
    private static final String bucketName = "true-bit-333719.appspot.com";
    private static final String TIMEPATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public CloudFirestore() {
        if (firestore == null) {
            ByteArrayInputStream byteArrayInputStream = getBytesStream(projectId, bucketName, "true-bit-333719-2edd7ecb552b.json");
            initialDB(projectId, byteArrayInputStream);
        }
    }

    private void initialDB(String projectId, InputStream inputStream) {
        try {

            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

            FirestoreOptions firestoreOptions =
                    FirestoreOptions.getDefaultInstance().toBuilder()
                            .setProjectId(projectId)
                           // .setCredentials(GoogleCredentials.getApplicationDefault())
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
                for (be.kuleuven.distributedsystems.cloud.entities.Ticket t:booking.getTickets()){
                    Map<String, Object> map = new HashMap<>();
                    map.put("company", t.getCompany());
                    map.put("customer", t.getCustomer());
                    map.put("seatId", t.getSeatId().toString());
                    map.put("showId", t.getShowId().toString());
                    map.put("ticketId", t.getTicketId().toString());
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
            ApiFuture<QuerySnapshot> future = firestore.collection("bookings").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                Map<String, Object> map = document.getData();
                Booking b = new Booking(UUID.fromString(map.get("id").toString()), LocalDateTime.parse(map.get("time").toString(), DateTimeFormatter.ofPattern(TIMEPATTERN)),
                        new ArrayList<>(){{
                            for (Object o:(ArrayList)map.get("tickets")){
                                Map<String, String> m = (Map)o;
                                add(new be.kuleuven.distributedsystems.cloud.entities.Ticket(m.get("company"), UUID.fromString(m.get("showId")), UUID.fromString(m.get("seatId")), UUID.fromString(m.get("ticketId")), m.get("customer")));
                            }
                        }}, map.get("customer").toString());
                bookings.add(b);
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
                                add(new be.kuleuven.distributedsystems.cloud.entities.Ticket(m.get("company"), UUID.fromString(m.get("showId")), UUID.fromString(m.get("seatId")), UUID.fromString(m.get("ticketId")), m.get("customer")));
                            }
                        }}, map.get("customer").toString());
                bookings.add(b);
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
                shows.add(new be.kuleuven.distributedsystems.cloud.entities.Show(show.getCompany(), UUID.fromString(show.getShowId()), show.getName(),
                        show.getLocation(), show.getImage()));
            }
        }catch (ExecutionException|InterruptedException e){
            e.printStackTrace();
        }

        return shows;
    }

    public be.kuleuven.distributedsystems.cloud.entities.Show getShowbyId(UUID showId){
        be.kuleuven.distributedsystems.cloud.entities.Show show = new be.kuleuven.distributedsystems.cloud.entities.Show();
        try {
            DocumentReference docRef  =
                    firestore.collection("shows").document(showId.toString());
            Show showLocal = docRef.get().get().toObject(Show.class);
            show = new be.kuleuven.distributedsystems.cloud.entities.Show(showLocal.getCompany(), UUID.fromString(showLocal.getShowId()),
                    showLocal.getName(), showLocal.getLocation(), showLocal.getImage());
        }catch (ExecutionException|InterruptedException e){
            e.printStackTrace();
        }
        return show;
    }

    public List<LocalDateTime> getLocalDateTimeByShowId(UUID showId){
        List<LocalDateTime> times = new ArrayList<>();
        try {
            DocumentReference docRef =
                    firestore.collection("shows").document(showId.toString());
            Show showLocal = docRef.get().get().toObject(Show.class);
            Set<String> set = showLocal.getSeats().keySet();
            for (String s:set){
                times.add(LocalDateTime.parse(s, DateTimeFormatter.ofPattern(TIMEPATTERN)));
            }
        }catch (ExecutionException|InterruptedException e){
            e.printStackTrace();
        }
        return times;
    }

    public List<be.kuleuven.distributedsystems.cloud.entities.Seat> getAvailableSeats(UUID showId, LocalDateTime time){
        List<be.kuleuven.distributedsystems.cloud.entities.Seat> seats = new ArrayList<>();
        try {
            CollectionReference seatsRef =
                    firestore.collection("seats");
            DocumentReference docRef =
                    firestore.collection("shows").document(showId.toString());
            Show show = docRef.get().get().toObject(Show.class);
            List<String> seatsCandidate = show.getSeats().get(time.format(DateTimeFormatter.ofPattern(TIMEPATTERN)));
            for (String seatCandidate:seatsCandidate){
                Seat seat = seatsRef.document(seatCandidate).get().get().toObject(Seat.class);
                if(seat.getAvailable()){
                    seats.add(new be.kuleuven.distributedsystems.cloud.entities.Seat(show.getCompany(), showId, UUID.fromString(seat.getSeatId()),
                            LocalDateTime.parse(seat.getTime(), DateTimeFormatter.ofPattern(TIMEPATTERN)), seat.getType(), seat.getName(), seat.getPrice()));
                }
            }
        }catch (ExecutionException|InterruptedException e){
            e.printStackTrace();
        }
        return seats;
    }

    public be.kuleuven.distributedsystems.cloud.entities.Seat getSeatById(UUID showId, UUID seatId){
        be.kuleuven.distributedsystems.cloud.entities.Seat s = new be.kuleuven.distributedsystems.cloud.entities.Seat();
        try {
            DocumentReference docRefShow =
                    firestore.collection("shows").document(showId.toString());
            DocumentReference docRefSeat =
                    firestore.collection("seats").document(seatId.toString());
            Show show = docRefShow.get().get().toObject(Show.class);
            Seat seat = docRefSeat.get().get().toObject(Seat.class);
            s = new be.kuleuven.distributedsystems.cloud.entities.Seat(show.getCompany(), showId, UUID.fromString(seat.getSeatId()),
                    LocalDateTime.parse(seat.getTime(), DateTimeFormatter.ofPattern(TIMEPATTERN)), seat.getType(), seat.getName(), seat.getPrice());
        }catch (ExecutionException|InterruptedException e){
            e.printStackTrace();
        }
        return s;
    }

    public be.kuleuven.distributedsystems.cloud.entities.Ticket getTicket(UUID showId, UUID seatId){
        be.kuleuven.distributedsystems.cloud.entities.Ticket ticket = new be.kuleuven.distributedsystems.cloud.entities.Ticket();
        try{
            Ticket ticketLocal
                    = firestore.collection("tickets").whereEqualTo("seatId", seatId.toString())
                    .whereEqualTo("showId", showId.toString()).get().get().toObjects(Ticket.class).get(0);
            ticket = new be.kuleuven.distributedsystems.cloud.entities.Ticket(ticketLocal.getCompany(), UUID.fromString(ticketLocal.getShowId()),
                    UUID.fromString(ticketLocal.getSeatId()), UUID.fromString(ticketLocal.getTicketId()), ticketLocal.getCustomer());
        }catch (ExecutionException|InterruptedException e){
            e.printStackTrace();
        }
        return ticket;
    }

    public be.kuleuven.distributedsystems.cloud.entities.Ticket saveTicket(String company, UUID showId, UUID seatId, String customer) throws Exception{
        final UUID ticketId = UUID.randomUUID();
        final DocumentReference docRefTicket =
                firestore.collection("tickets").document(ticketId.toString());
        final DocumentReference docRefSeat =
                firestore.collection("seats").document(seatId.toString());
        ApiFuture<String> futureTransaction = firestore.runTransaction(transaction -> {
            Seat seat = transaction.get(docRefSeat).get().toObject(Seat.class);
            if(seat.getAvailable()){
                transaction.set(docRefTicket, new Ticket(company, showId.toString(), seatId.toString(), ticketId.toString(), customer));
                transaction.update(docRefSeat, "available", false);
                return "Success";
            }
            else{
                throw new Exception("Already booked");
            }

        });

        return new be.kuleuven.distributedsystems.cloud.entities.Ticket(company, showId, seatId, ticketId, customer);
    }

    public String deleteTicket(UUID ticketId) throws Exception{
        final DocumentReference docRefTicket =
                firestore.collection("tickets").document(ticketId.toString());
        if (!docRefTicket.get().get().exists()) return "not exist";
        final DocumentReference docRefSeat =
                firestore.collection("seats").document((String)docRefTicket.get().get().get("seatId"));
        ApiFuture<String> futureTransaction = firestore.runTransaction(transaction -> {
            transaction.update(docRefSeat, "available", true);
            transaction.delete(docRefTicket);
            return "Success";
        }) ;
        return futureTransaction.get();
    }

    @Autowired
    public void run(ApplicationArguments args) throws Exception{
        CollectionReference collectionShows = firestore.collection("shows");
        CollectionReference collectionSeats = firestore.collection("seats");

        //deleteCollection(collectionSeats, 500);
        //deleteCollection(collectionShows, 500);

        ApiFuture<QuerySnapshot> future =
                collectionShows.get();
        if (!future.get().isEmpty()) return;
        else{
            Map<String,Object> json =
                    new ObjectMapper().readValue(getBytesStream(projectId, bucketName, "data.json"), HashMap.class);
            for (Object object:(ArrayList)json.get("shows")) {
                Map<String, Object> showmap = (Map)object;
                Map<String, List<String>> timeslots = new HashMap<>();
                for (Object o:(ArrayList)showmap.get("seats")) {
                    UUID seat_uuid = UUID.randomUUID();
                    Map<String, Object> seatData = (Map)o;
                    if(!timeslots.containsKey((String)seatData.get("time"))){
                        timeslots.put((String)seatData.get("time"), new ArrayList<String>());
                    }
                    timeslots.get(seatData.get("time")).add(seat_uuid.toString());
                    collectionSeats.document(seat_uuid.toString()).set(new Seat(seat_uuid.toString(), (String)seatData.get("time"),
                            (String)seatData.get("name"), true, (String)seatData.get("type"), ((Integer)seatData.get("price")).doubleValue()));
                }
                UUID show_uuid = UUID.randomUUID();
                Show show = new Show(show_uuid.toString(), "localCompany", (String)showmap.get("name"), (String)showmap.get("location"), (String)showmap.get("image"), timeslots);
                collectionShows.document(show_uuid.toString()).set(show);
            }
        }
    }

    void deleteCollection(CollectionReference collection, int batchSize) {
        try {
            // retrieve a small batch of documents to avoid out-of-memory errors
            ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
            int deleted = 0;
            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }
            if (deleted >= batchSize) {
                // retrieve and delete another batch
                deleteCollection(collection, batchSize);
            }
        } catch (Exception e) {
            System.err.println("Error deleting collection : " + e.getMessage());
        }
    }

    public static ByteArrayInputStream getBytesStream(
            String projectId, String bucketName, String objectName) {

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(blob.getContent());
        return byteArrayInputStream;

    }
}
