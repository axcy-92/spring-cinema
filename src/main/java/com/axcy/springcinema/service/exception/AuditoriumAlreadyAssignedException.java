package com.axcy.springcinema.service.exception;

import com.axcy.springcinema.entity.Auditorium;
import com.axcy.springcinema.entity.Event;

public class AuditoriumAlreadyAssignedException extends Exception {
    private String message;

    public AuditoriumAlreadyAssignedException(Event event, Auditorium auditorium) {
        message = String.format("Event <%s> is not assigned to auditorium <%s> because it's already assigned to another event",
                event.getName(),
                auditorium.getName());
    }

    @Override
    public String getMessage() {
        return message;
    }
}
