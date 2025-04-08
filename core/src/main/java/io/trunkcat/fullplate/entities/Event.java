package io.trunkcat.fullplate.entities;

public class Event {
    private String eventName;
    private int eventID;

    public Event(String eventName,
                 int eventID) {
        this.eventID = eventID;
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
}
