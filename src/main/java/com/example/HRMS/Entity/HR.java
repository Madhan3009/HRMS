package com.example.HRMS.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="hr")
public class HR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
