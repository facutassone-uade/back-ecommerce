package com.uade.e_commerce.customer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uade.e_commerce.order.Order;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastName;
    private Long nationalId;
    private String email;
    private Long phone;

    @Embedded
    private Address address;

    private String username;
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

}
