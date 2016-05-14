package com.vatichub.obd2.bean;

import org.json.JSONObject;

public class OBD2Event {
    private JSONObject eventData;

    public OBD2Event (JSONObject eventData) {
        this.eventData = eventData;
    }

    public JSONObject getEventData() {
        return eventData;
    }
}
