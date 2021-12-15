package be.kuleuven.distributedsystems.cloud.controller;/**
 * @author ：mmzs
 * @date ：Created in 2021/11/22 23:44
 * @description：Pubsub Initiation and Logic
 * @modified By：
 * @version: $
 */

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author     ：mmzs
 * @date       ：Created in 2021/11/22 23:44
 * @description：Pubsub Initiation and Logic
 * @modified By：
 * @version: $
 */

public class PubsubManagement{
    private static Publisher publisher = null;
    private static final String projectId = "true-bit-333719";
    private static final String topicId = "projects/true-bit-333719/topics/handleBooking";

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
