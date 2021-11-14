package be.kuleuven.distributedsystems.cloud;

import be.kuleuven.distributedsystems.cloud.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class Model {
    @Autowired
    WebClient.Builder webClientBuilder;

    static final String KEY = "wCIoTqec6vGJijW2meeqSokanZuqOL";

    static final Map<String, ArrayList<Booking>> bookinng = new HashMap<>() ;

    public List<Show> getShows() {
        // TODO: return all shows

        var showsReliable = webClientBuilder
                .baseUrl("https://reliabletheatrecompany.com/")
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("shows")
                        .queryParam("key", KEY)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CollectionModel<Show>>() {
                })
                .block()
                .getContent();


        var showsUnreliable = webClientBuilder
                .baseUrl("https://unreliabletheatrecompany.com/")
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("shows")
                        .queryParam("key", KEY)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CollectionModel<Show>>() {
                })
                .block()
                .getContent();

        ArrayList<Show> shows = new ArrayList<>();
        shows.addAll(showsReliable);
        shows.addAll(showsUnreliable);

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
                            //  .queryParam("showId", showId)
                            .build())
                    .retrieve()
                    .bodyToMono(Show.class)
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
                    .baseUrl("https://unreliabletheatrecompany.com/")
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

        } else {
            dates = webClientBuilder
                    .baseUrl("https://reliabletheatrecompany.com/")
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
                    .baseUrl("https://unreliabletheatrecompany.com/")
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

        } else {
            seats = webClientBuilder
                    .baseUrl("https://reliabletheatrecompany.com/")
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
                    .baseUrl("https://unreliabletheatrecompany.com/")
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

        } else {
            seat = webClientBuilder
                    .baseUrl("https://reliabletheatrecompany.com/")
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
                    .baseUrl("https://unreliabletheatrecompany.com/")
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
                    .block();

        } else {
            ticket = webClientBuilder
                    .baseUrl("https://reliabletheatrecompany.com/")
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
            tickets.add(new Ticket(quote.getCompany(), quote.getShowId(), quote.getSeatId(), UUID.randomUUID(), customer));
        }
        Booking newBooking = new Booking(UUID.randomUUID(), LocalDateTime.now(), tickets, customer);
        if(bookinng.containsKey(customer))
            bookinng.get(customer).add(newBooking);
        else
            bookinng.put(customer, new ArrayList<Booking>(Arrays.asList(newBooking)));
    }
}
