package com.fluxmartApi.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


public enum Status {
    PAID,
    FAILED,
    CANCELLED,
    PENDING
}
