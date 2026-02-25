package com.lms.system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "investors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Investor {

    @Id
    @Column(name = "nic", nullable = false, unique = true)
    private String nic;

    @Column(name = "route_name", nullable = false)
    private String investorName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email")
    private String email;
}
