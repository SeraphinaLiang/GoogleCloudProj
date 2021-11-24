package be.kuleuven.distributedsystems.cloud.controller;

import be.kuleuven.distributedsystems.cloud.Model;
import be.kuleuven.distributedsystems.cloud.entities.Quote;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;

import com.google.api.gax.rpc.ApiException;

import com.google.cloud.pubsub.v1.Publisher;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class APIController {
    private final Model model;

    @Autowired
    public APIController(Model model) {
        this.model = model;
    }

    @PostMapping(path = "/addToCart", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<Void> addToCart(
            @ModelAttribute Quote quote,
            @RequestHeader(value = "referer") String referer,
            @CookieValue(value = "cart", required = false) String cartString) {
        List<Quote> cart = Cart.fromCookie(cartString);
        cart.add(quote);
        ResponseCookie cookie = Cart.toCookie(cart);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        headers.add(HttpHeaders.LOCATION, referer);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Void> removeFromCart(
            @ModelAttribute Quote quote,
            @RequestHeader(value = "referer") String referer,
            @CookieValue(value = "cart", required = false) String cartString) {
        List<Quote> cart = Cart.fromCookie(cartString);
        cart.remove(quote);
        ResponseCookie cookie = Cart.toCookie(cart);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        headers.add(HttpHeaders.LOCATION, referer);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping("/confirmCart")
    public ResponseEntity<Void> confirmCart(
            @CookieValue(value = "cart", required = false) String cartString) throws Exception {

            System.out.println(cartString);
        try {
            Publisher publisher = PubsubManagement.getPublisher();
            String message = cartString;
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).putAttributes("customer", AuthController.getUser().getEmail()).build();

            ApiFuture<String> future = publisher.publish(pubsubMessage);

            ApiFutures.addCallback(
                    future,
                    new ApiFutureCallback<String>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            if (throwable instanceof ApiException) {
                                ApiException apiException = ((ApiException) throwable);
                                // details on the API exception
                                System.out.println(apiException.getStatusCode().getCode());
                                System.out.println(apiException.isRetryable());
                            }
                            System.out.println("Error publishing message : " + message);
                        }
                        @Override
                        public void onSuccess(String messageId) {
                            System.out.println("Published message ID: " + messageId);
                        }},
                    MoreExecutors.directExecutor());
        } finally {
            PubsubManagement.freePublisher();
            PubsubManagement.freeChannel();
        }
        //this.model.confirmQuotes(new ArrayList<>(cart), AuthController.getUser().getEmail());
        List<Quote> cart = Cart.fromCookie(cartString);
        cart.clear();
        ResponseCookie cookie = Cart.toCookie(cart);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        headers.add(HttpHeaders.LOCATION, "/account");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
