package com.example.capstone3.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "work_latitude", nullable = false)
    private Double workLatitude;

    @Column(name = "work_longitude", nullable = false)
    private Double workLongitude;

    @Column(name = "budget", nullable = false)
    private Double budget;

    @Column(name = "marital_status", nullable = false, length = 10)
    private String maritalStatus;

    @Column(name = "children_count", nullable = false)
    private Integer childrenCount;

    @Column(name = "preferred_bedrooms")
    private Integer preferredBedrooms;

    @Column(name = "preferred_bathrooms")
    private Integer preferredBathrooms;

    @Column(name = "preferred_district", length = 100)
    private String preferredDistrict;
}
