package com.witmoon.xmb.model.service;

import org.json.JSONException;
import org.json.JSONObject;

public class ReturnTicket {

    public String getTicket_code() {
        return ticket_code;
    }

    public void setTicket_code(String ticket_code) {
        this.ticket_code = ticket_code;
    }

    public int getTicket_status() {
        return ticket_status;
    }

    public void setTicket_status(int ticket_status) {
        this.ticket_status = ticket_status;
    }

    private String ticket_code;
    private int ticket_status;

    public int getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(int ticket_id) {
        this.ticket_id = ticket_id;
    }

    private int ticket_id;

    public static ReturnTicket parse(JSONObject ticketObject) throws JSONException {
        ReturnTicket ticket = new ReturnTicket();
        ticket.setTicket_code(ticketObject.getString("ticket_code"));
        ticket.setTicket_status(0);
        ticket.setTicket_id(ticketObject.getInt("ticket_id"));
        return ticket;
    }
}

