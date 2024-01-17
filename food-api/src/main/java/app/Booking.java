package app;

public class Booking {
    public String firstName = "";
    public String lastName = "";
    public int year = 0;
    public int month = 0;
    public int day = 0;
    public int hour = 0;
    public int minute = 0;
    public int nb = 0;

    public Booking() { }

    public Booking(String firstName, String lastName, int year, int month, int day, int hour, int minute, int nb) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nb = nb;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.nb = nb;
    }
}