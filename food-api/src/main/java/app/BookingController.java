package app;

import io.javalin.http.Context;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

class BookingController {

    // "Database" of bookings
    // Since the server is multi-threaded, we need to use a thread-safe data structure
    // such as ConcurrentHashMap or HashMap
    private ConcurrentHashMap<Integer, Booking> bookings = new ConcurrentHashMap<Integer, Booking>();
    private int lastId = 0;

    public BookingController() {
        bookings.put(++lastId, new Booking("Larry", "David", 2024, 10, 21, 18, 30, 2));
        bookings.put(++lastId, new Booking("Jerry", "Seinfeld",2025, 3, 27, 19, 0, 4));
        bookings.put(++lastId, new Booking("George", "Costanza",2025, 6, 21, 20, 30,  1));
    }

    public void getOne(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(bookings.get(id));
    }

    public void getAll(Context ctx) {
        ctx.json(bookings);
        ctx.status(200);
    }

    public void create(Context ctx) {
        Booking booking = ctx.bodyAsClass(Booking.class);
        bookings.put(++lastId, booking);
        ctx.status(201);
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        bookings.remove(id);
        ctx.status(204);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Booking booking = ctx.bodyAsClass(Booking.class);
        bookings.put(id, booking);
        ctx.status(200);
    }

}