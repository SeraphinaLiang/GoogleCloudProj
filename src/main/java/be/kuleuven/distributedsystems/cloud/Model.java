package be.kuleuven.distributedsystems.cloud;

import be.kuleuven.distributedsystems.cloud.email.EmailSending;
import be.kuleuven.distributedsystems.cloud.entities.*;

import be.kuleuven.distributedsystems.cloud.firestore.CloudFirestore;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class Model {
    @Autowired
    WebClient.Builder webClientBuilder;

    static final String KEY = "wCIoTqec6vGJijW2meeqSokanZuqOL";
    static final String RELIABLE_COMPANY_URL = "https://reliabletheatrecompany.com/";
    static final String UNRELIABLE_COMPANY_URL = "https://unreliabletheatrecompany.com/";
    CloudFirestore db = new CloudFirestore();

    public List<Show> getShows() {
        // TODO: return all shows
        ArrayList<Show> shows = new ArrayList<>();
        var showsReliable = webClientBuilder
                .baseUrl(RELIABLE_COMPANY_URL)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("shows")
                        .queryParam("key", KEY)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CollectionModel<Show>>() {
                })
                .retry()
                .block()
                .getContent();

        var showsUnreliable = webClientBuilder
                .baseUrl(UNRELIABLE_COMPANY_URL)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("shows")
                        .queryParam("key", KEY)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CollectionModel<Show>>() {
                })
                .retry()
                .block()
                .getContent();

        List<Show> showsLocal = db.getLocalShowsInPlatform();
        shows.addAll(showsUnreliable);
        shows.addAll(showsReliable);
        shows.addAll(showsLocal);
        return shows;
    }

    public Show getShow(String company, UUID showId) {
        // TODO: return the given show

        //"/shows/62f893ab-6721-417a-9dbf-59b511e97676"
        Show show;

        if (company.contains("unreliabletheatrecompany")) {

            show = webClientBuilder
                    .baseUrl("https://unreliabletheatrecompany.com/")
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .queryParam("key", KEY)
                            // .queryParam("showId", showId)
                            .build())
                    .retrieve()
                    .bodyToMono(Show.class)
                    .retry()
                    .block();

        } else if(company.contains("reliabletheatrecompany")){
            show = webClientBuilder
                    .baseUrl("https://reliabletheatrecompany.com/")
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .queryParam("key", KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(Show.class)
                    .retry()
                    .block();
        }else{
            show = db.getShowbyId(showId);
        }

        return show;
    }

    public List<LocalDateTime> getShowTimes(String company, UUID showId) {
        // TODO: return a list with all possible times for the given show

        //"/shows/62f893ab-6721-417a-9dbf-59b511e97676/times"
        Collection<LocalDateTime> dates;

        if (company.contains("unreliabletheatrecompany")) {
            dates = webClientBuilder
                    .baseUrl(UNRELIABLE_COMPANY_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .pathSegment("times")
                            .queryParam("key", KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<CollectionModel<LocalDateTime>>() {
                    })
                    .retry()
                    .block()
                    .getContent();

        } else if(company.contains("reliabletheatrecompany")){
            dates = webClientBuilder
                    .baseUrl(RELIABLE_COMPANY_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .pathSegment("times")
                            .queryParam("key", KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<CollectionModel<LocalDateTime>>() {
                    })
                    .block()
                    .getContent();

        } else{
            dates = db.getLocalDateTimeByShowId(showId);
        }

        return new ArrayList<>(dates);
    }

    public List<Seat> getAvailableSeats(String company, UUID showId, LocalDateTime time) {
        // TODO: return all available seats for a given show and time

        //"/shows/62f893ab-6721-417a-9dbf-59b511e97676/seats?time={time}&available=true"
        Collection<Seat> seats;

        if (company.contains("unreliabletheatrecompany")) {
            seats = webClientBuilder
                    .baseUrl(UNRELIABLE_COMPANY_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .pathSegment("seats")
                            .queryParam("key", KEY)
                            .queryParam("time", time)
                            .queryParam("available", "true")
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<CollectionModel<Seat>>() {
                    })
                    .retry()
                    .block()
                    .getContent();

        } else if(company.contains("reliabletheatrecompany")) {
            seats = webClientBuilder
                    .baseUrl(RELIABLE_COMPANY_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .pathSegment("seats")
                            .queryParam("key", KEY)
                            .queryParam("time", time)
                            .queryParam("available", "true")
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<CollectionModel<Seat>>() {
                    })
                    .block()
                    .getContent();

        } else{
            seats = db.getAvailableSeats(showId, time);
        }

        return new ArrayList<>(seats);
    }

    public Seat getSeat(String company, UUID showId, UUID seatId) {
        // TODO: return the given seat
        //shows/showID/seats/seatID

        Seat seat;

        if (company.contains("unreliabletheatrecompany")) {

            seat = webClientBuilder
                    .baseUrl(UNRELIABLE_COMPANY_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .pathSegment("seats")
                            .pathSegment(seatId.toString())
                            .queryParam("key", KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(Seat.class)
                    .retry()
                    .block();

        } else if(company.contains("reliabletheatrecompany")) {
            seat = webClientBuilder
                    .baseUrl(RELIABLE_COMPANY_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .pathSegment("seats")
                            .pathSegment(seatId.toString())
                            .queryParam("key", KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(Seat.class)
                    .block();
        } else{
            seat = db.getSeatById(showId, seatId);
        }

        return seat;
    }

    public Ticket getTicket(String company, UUID showId, UUID seatId) {
        // TODO: return the ticket for the given seat
        // /shows/showID/seats/seatID/ticket

        Ticket ticket;

        if (company.contains("unreliabletheatrecompany")) {

            ticket = webClientBuilder
                    .baseUrl(UNRELIABLE_COMPANY_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .pathSegment("seats")
                            .pathSegment(seatId.toString())
                            .pathSegment("ticket")
                            .queryParam("key", KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(Ticket.class)
                    .retry()
                    .block();

        } else if(company.contains("reliabletheatrecompany")) {
            ticket = webClientBuilder
                    .baseUrl(RELIABLE_COMPANY_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .pathSegment("seats")
                            .pathSegment(seatId.toString())
                            .pathSegment("ticket")
                            .queryParam("key", KEY)
                            .build())
                    .retrieve()
                    .bodyToMono(Ticket.class)
                    .retry()
                    .block();
        }else{
            ticket = db.getTicket(showId, seatId);
        }

        return ticket;
    }

    public List<Booking> getBookings(String customer) {
        List<Booking> bookings = db.getBookingsByCustomerFromDB(customer);
        bookings .sort(new Comparator<Booking>() {
            @Override
            public int compare(Booking o1, Booking o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
        return bookings;
    }

    public List<Booking> getAllBookings() {
        return db.getAllBookingsFromDB();
    }

    public Set<String> getBestCustomers() {
        // TODO: return the best customer (highest number of tickets, return all of them if multiple customers have an equal amount)

        Map<String, ArrayList<Booking>> bookingMap = new HashMap<>();
        List<Booking> bookingList = db.getAllBookingsFromDB();
        for (Booking b : bookingList) {
            if (!bookingMap.containsKey(b.getCustomer())) {
                bookingMap.put(b.getCustomer(), db.getBookingsByCustomerFromDB(b.getCustomer()));
            }
        }

        Set<String> bestCustomers = new HashSet<>();
        int highest = 0;
        for (Map.Entry<String, ArrayList<Booking>> entry : bookingMap.entrySet()) {
            int cnt = 0;
            for (Booking booking : entry.getValue()
            ) {
                cnt += booking.getTickets().size();
            }
            if (cnt > highest) {
                bestCustomers.clear();
                bestCustomers.add(entry.getKey());
                highest = cnt;
            } else if (cnt == highest) bestCustomers.add(entry.getKey());
        }
        return bestCustomers;
    }

    public void rollback(List<Ticket> tickets){
        for (Ticket ticket : tickets
        ) {
            try {
                if(ticket.getCompany().equals("localCompany")){
                    db.deleteTicket(ticket.getTicketId());
                }
                else{
                    String baseUrl_ = "https://reliabletheatrecompany.com/";
                    if (ticket.getCompany().equals("unreliabletheatrecompany.com"))
                        baseUrl_ = "https://unreliabletheatrecompany.com/";
                    var del = webClientBuilder
                            .baseUrl(baseUrl_)
                            .build()
                            .delete()
                            .uri(uriBuilder -> uriBuilder
                                    .pathSegment("shows")
                                    .pathSegment(ticket.getShowId().toString())
                                    .pathSegment("seats")
                                    .pathSegment(ticket.getSeatId().toString())
                                    .pathSegment("ticket")
                                    .pathSegment(ticket.getTicketId().toString())
                                    .queryParam("key", KEY)
                                    .build())
                            .retrieve()
                            .onStatus(e -> e.is4xxClientError(), clientResponse1 -> {
                                return Mono.error(new RuntimeException("404 client error"));
                            })
                            .bodyToMono(String.class)
                            .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(2))
                                    .filter(e -> e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is5xxServerError()))
                            .block();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tickets.clear();
    }

    public void confirmQuotes(List<Quote> quotes, String customer) {
        // TODO: reserve all seats for the given quotes
        if (quotes.isEmpty()) return;
        if(quotes.stream().distinct().count() != quotes.size()){
            sendEmail(customer, false, quotes);
            return;
        }
        ArrayList<Ticket> tickets = new ArrayList<>();
        for (Quote quote : quotes
        ) {
            if (quote.getCompany().equals("localCompany")){
                try {
                    tickets.add(db.saveTicket(quote.getCompany(), quote.getShowId(), quote.getSeatId(), customer));
                }catch (Exception e){
                    sendEmail(customer, false, quotes);
                    rollback(tickets);
                    e.printStackTrace();
                    return;
                }

            }
            else{
                String baseUrl = "https://reliabletheatrecompany.com/";
                if (quote.getCompany().equals("unreliabletheatrecompany.com"))
                    baseUrl = "https://unreliabletheatrecompany.com/";
                var res = webClientBuilder
                        .baseUrl(baseUrl)
                        .build()
                        .put()
                        .uri(uriBuilder -> uriBuilder
                                .pathSegment("shows")
                                .pathSegment(quote.getShowId().toString())
                                .pathSegment("seats")
                                .pathSegment(quote.getSeatId().toString())
                                .pathSegment("ticket")
                                .queryParam("key", KEY)
                                .queryParam("customer", customer)
                                .build())
                        .retrieve()
                        .onStatus(e -> e.is4xxClientError(), clientResponse -> {
                            sendEmail(customer, false, quotes);
                            rollback(tickets);
                            return Mono.error(new Exception());
                        })
                        .bodyToMono(Ticket.class)
                        .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(2))
                                .filter(e -> e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is5xxServerError()))
                        .block();
                tickets.add(res);
            }

        }
        if(tickets.isEmpty()){
            sendEmail(customer, false, quotes);
            return;
        }
        Booking newBooking = new Booking(UUID.randomUUID(), LocalDateTime.now(), tickets, customer);
        db.addBookingToDB(newBooking);
        sendEmail(customer, true, quotes);
    }

    void sendEmail(String customer, boolean status, List<Quote> quotes){
        String subject = status ? "Your booking is successful" : "Sorry, your booking fails";
        String content = "Dear " + customer + ",\n\tBelow are your bookings:\n";
        for (Quote quote:quotes){
            Show show = getShow(quote.getCompany(), quote.getShowId());
            Seat seat = getSeat(quote.getCompany(), quote.getShowId(), quote.getSeatId());
            content += "\t" + show.getName() + "\t" + seat.getType() + ":" + seat.getName() + "\n";
        }
        EmailSending.sendEmail(customer, subject, content);
    }

}
