package be.kuleuven.distributedsystems.cloud.controller;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PubsubManagement{
    private static Publisher publisher = null;
    private static final String projectId = "true-bit-333719";
    private static final String topicId = "handleBooking";

    public static Publisher getPublisher(){
        try {
            TopicName topicName = TopicName.of(projectId, topicId);
            publisher =
                    Publisher.newBuilder(topicName)
                            .build();
        }catch (IOException e){
            e.printStackTrace();
        }
        return publisher;
    }


    public static void freePublisher(){
        try {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
