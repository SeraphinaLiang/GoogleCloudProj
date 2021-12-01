package be.kuleuven.distributedsystems.cloud.firestore;

import be.kuleuven.distributedsystems.cloud.entities.Booking;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class CloudFirestore {

    private Firestore db;

    public CloudFirestore() {
    }

    public void initialDB(String projectId) {
        try {
            // [START firestore_setup_client_create]
            // Option 1: Initialize a Firestore client with a specific `projectId` and
            //           authorization credential.
            // [START fs_initialize_project_id]
            // [START firestore_setup_client_create_with_project_id]
            FirestoreOptions firestoreOptions =
                    FirestoreOptions.getDefaultInstance().toBuilder()
                            .setProjectId(projectId)
                            .setCredentials(GoogleCredentials.getApplicationDefault())
                            .build();
            Firestore db = firestoreOptions.getService();
            // [END fs_initialize_project_id]
            // [END firestore_setup_client_create_with_project_id]
            // [END firestore_setup_client_create]
            this.db = db;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addBookingToDB(Booking booking) {
        try {
            ApiFuture<WriteResult> addResult = db.collection("bookings").document(booking.getId().toString()).set(booking);
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
            ApiFuture<WriteResult> deleteResult = db.collection("bookings").document(bookingID).delete();
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
            ApiFuture<QuerySnapshot> future = db.collection("bookings").get();
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
                    db.collection("bookings").whereEqualTo("customer", customer).get();
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


}
