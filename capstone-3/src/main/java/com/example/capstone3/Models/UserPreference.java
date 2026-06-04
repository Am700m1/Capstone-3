package com.example.capstone3.Models;

import com.example.capstone3.Enums.PreferenceLevel;
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

    @Column(name = "max_budget", nullable = false)
    private Double maxBudget;

    @Column(name = "max_commute_minutes")
    private Integer maxCommuteMinutes;

    @Column(name = "requires_parking")
    private Boolean requiresParking;

    @Column(name = "requires_elevator")
    private Boolean requiresElevator;

    @Column(name = "requires_furnished")
    private Boolean requiresFurnished;

    // instead of boolean and to be very accurate
    // enumtype.string so its entered as string
    @Enumerated(EnumType.STRING)
    @Column(name = "gym_preference", length = 20)
    private PreferenceLevel gymPreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "cafes_preference", length = 20)
    private PreferenceLevel cafesPreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "hospital_preference", length = 20)
    private PreferenceLevel hospitalPreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "school_preference", length = 20)
    private PreferenceLevel schoolPreference;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_transport_preference", length = 20)
    private PreferenceLevel publicTransportPreference;

    @Column(name = "preferred_bedrooms")
    private Integer preferredBedrooms;

    @Column(name = "preferred_bathrooms")
    private Integer preferredBathrooms;

    @Column(name = "preferred_district", length = 100)
    private String preferredDistrict;
}
