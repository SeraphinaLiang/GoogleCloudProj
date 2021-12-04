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

@Component
public class PubsubManagement implements ApplicationRunner{
    private static ManagedChannel channel;
    private static TransportChannelProvider channelProvider;
    private static CredentialsProvider credentialsProvider;
    private static Publisher publisher = null;
    private static final String projectId = "demo-distributed-systems-kul";
    private static final String topicId = "topicid";
    private static final String subsriptionId = "subscriptionid";
    private static final String pushEndpoint = "http://localhost:8080/subscription";

    public static Publisher getPublisher(){
        try {
            init();
            TopicName topicName = TopicName.of(projectId, topicId);
            publisher =
                    Publisher.newBuilder(topicName)
                            .setChannelProvider(channelProvider)
                            .setCredentialsProvider(credentialsProvider)
                            .build();
        }catch (IOException e){
            e.printStackTrace();
        }
        return publisher;
    }

    public static void init(){
        channel = ManagedChannelBuilder.forTarget("localhost:8083").usePlaintext().build();
        channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
        credentialsProvider = NoCredentialsProvider.create();
    }

    public static void freeChannel(){
        channel.shutdown();
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

    @Override
    public void run(ApplicationArguments args) throws Exception{

        try {
            init();
            TopicName topicName = TopicName.of(projectId, topicId);
            TopicAdminClient topicClient =
                    TopicAdminClient.create(TopicAdminSettings.newBuilder()
                            .setTransportChannelProvider(channelProvider)
                            .setCredentialsProvider(credentialsProvider).build());
            try {
                topicClient.getTopic(topicName);
            }catch (RuntimeException e){
                topicClient.createTopic(topicName);
            }


            SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create(
                    SubscriptionAdminSettings.newBuilder().setTransportChannelProvider(channelProvider)
                            .setCredentialsProvider(credentialsProvider)
                            .build()
            );
            ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subsriptionId);
            try {
                subscriptionAdminClient.getSubscription(subscriptionName);
            }
            catch (RuntimeException e){
                PushConfig pushConfig = PushConfig.newBuilder().setPushEndpoint(pushEndpoint).build();
                Subscription subscription =
                        subscriptionAdminClient.createSubscription(subscriptionName, topicName, pushConfig, 10);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            freeChannel();
        }
    }
}
