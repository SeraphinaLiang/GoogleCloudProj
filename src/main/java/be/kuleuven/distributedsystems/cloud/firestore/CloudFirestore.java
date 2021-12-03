package be.kuleuven.distributedsystems.cloud.firestore;

import be.kuleuven.distributedsystems.cloud.entities.Booking;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.common.collect.Lists;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class CloudFirestore {

    private Firestore firestore = null;

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
            ApiFuture<WriteResult> addResult = firestore.collection("bookings").document(booking.getId().toString()).set(booking);
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
                Booking b = document.toObject(Booking.class);
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
                Booking b = document.toObject(Booking.class);
                bookings.add(b);
            }
        } catch (ExecutionException | InterruptedException e1) {
            e1.printStackTrace();
        }
        return bookings;

    }

    /**
     * local company persist all shows, seats and tickets in Cloud Firestore.
     */
    public void initialLocalCompany() {

    }
}
