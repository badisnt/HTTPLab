package app;

import io.javalin.*;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        BookingController bookingController = new BookingController();
        app.get("/api/bookings", bookingController::getAll);
        app.get("/api/bookings/{id}", bookingController::getOne);
        app.post("/api/bookings/", bookingController::create);
        app.put("/api/bookings/{id}", bookingController::update);
        app.delete("/api/bookings/{id}", bookingController::delete);
    }
}