package com.fluxmartApi.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "payment_method")
@Entity
@Getter
@Setter

public class PaymentMethod {
    @Id
    @Column(name = "payment_method_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    @Column(name = "name")
    private String name;

}
