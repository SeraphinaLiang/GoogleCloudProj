package be.kuleuven.distributedsystems.cloud;

import be.kuleuven.distributedsystems.cloud.entities.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
    static final Map<String, ArrayList<Booking>> bookinng = new HashMap<>() ;

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
        shows.addAll(showsUnreliable);
        shows.addAll(showsReliable);
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

        } else {
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
        }

        return show;
    }

    public List<LocalDateTime> getShowTimes(String company, UUID showId) {
        // TODO: return a list with all possible times for the given show

        //"/shows/62f893ab-6721-417a-9dbf-59b511e97676/times"
        Collection<LocalDateTime> dates;

        if (company.contains("unreliabletheatrecompany")) {
            dates= webClientBuilder
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

        } else {
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

        }

        return new ArrayList<>(dates);
    }

    public List<Seat> getAvailableSeats(String company, UUID showId, LocalDateTime time) {
        // TODO: return all available seats for a given show and time

        //"/shows/62f893ab-6721-417a-9dbf-59b511e97676/seats?time={time}&available=true"
        Collection<Seat> seats;

        if (company.contains("unreliabletheatrecompany")) {
            seats= webClientBuilder
                    .baseUrl(UNRELIABLE_COMPANY_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .pathSegment("seats")
                            .queryParam("key", KEY)
                            .queryParam("time",time)
                            .queryParam("available","true")
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<CollectionModel<Seat>>() {
                    })
                    .retry()
                    .block()
                    .getContent();

        } else {
            seats = webClientBuilder
                    .baseUrl(RELIABLE_COMPANY_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("shows")
                            .pathSegment(showId.toString())
                            .pathSegment("seats")
                            .queryParam("key", KEY)
                            .queryParam("time",time)
                            .queryParam("available","true")
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<CollectionModel<Seat>>() {
                    })
                    .block()
                    .getContent();

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

        } else {
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

        } else {
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
        }

        return ticket;
    }

    public List<Booking> getBookings(String customer) {
        // TODO: return all bookings from the customer
        return bookinng.getOrDefault(customer, new ArrayList<>());
    }

    public List<Booking> getAllBookings() {
        // TODO: return all bookings

        return new ArrayList<>(){
            {
                Iterator<ArrayList<Booking>> iterator = bookinng.values().iterator();
                while(iterator.hasNext()) addAll(iterator.next());
            }
        };
    }

    public Set<String> getBestCustomers() {
        // TODO: return the best customer (highest number of tickets, return all of them if multiple customers have an equal amount)
        Set<String> bestCustomers = new HashSet<>();
        int highest = 0;
        for(Map.Entry<String,ArrayList<Booking>> entry:bookinng.entrySet()){
            int cnt = 0;
            for (Booking booking:entry.getValue()
                 ) {
                cnt += booking.getTickets().size();
            }
            if(cnt > highest){
                bestCustomers.clear();
                bestCustomers.add(entry.getKey());
                highest = cnt;
            }
            else if(cnt == highest) bestCustomers.add(entry.getKey());
        }
        return bestCustomers;
    }



    public void confirmQuotes(List<Quote> quotes, String customer) {
        // TODO: reserve all seats for the given quotes
        if (quotes.isEmpty()) return;
        List<Ticket> tickets = new ArrayList<>();
        for (Quote quote:quotes
             ) {
            String baseUrl = "https://reliabletheatrecompany.com/";
            if (quote.getCompany().equals("unreliabletheatrecompany.com")) baseUrl = "https://unreliabletheatrecompany.com/";
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
                            .queryParam("key",KEY)
                            .queryParam("customer", customer)
                            .build())
                    .retrieve()
                    .onStatus(e -> e.is4xxClientError(), clientResponse -> {
                        for (Ticket ticket:tickets
                             ) {
                            try{
                                String baseUrl_ = "https://reliabletheatrecompany.com/";
                                if (ticket.getCompany().equals("unreliabletheatrecompany.com")) baseUrl_ = "https://unreliabletheatrecompany.com/";
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
                                        .onStatus(e->e.is4xxClientError(), clientResponse1 -> {return Mono.error(new RuntimeException("404 client error"));})
                                        .bodyToMono(String.class)
                                        .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(2))
                                                .filter(e -> e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is5xxServerError()))
                                        .block();
                            }catch (RuntimeException e){
                                continue;
                            }

                        }
                        return Mono.error(new Exception());
                    })
                    .bodyToMono(Ticket.class)
                    .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(2))
                        .filter(e -> e instanceof WebClientResponseException && ((WebClientResponseException) e).getStatusCode().is5xxServerError()))
                    .block();
            tickets.add(res);
        }


        Booking newBooking = new Booking(UUID.randomUUID(), LocalDateTime.now(), tickets, customer);
        if(bookinng.containsKey(customer))
            bookinng.get(customer).add(newBooking);
        else
            bookinng.put(customer, new ArrayList<Booking>(Arrays.asList(newBooking)));
    }
}
