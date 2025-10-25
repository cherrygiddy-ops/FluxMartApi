package com.fluxmartApi.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shippers")
@Getter
@Setter
public class Shipper {
    @Id
    @Column(name = "shipper_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte id;

    @Column(name = "name")
    private String name;

}
