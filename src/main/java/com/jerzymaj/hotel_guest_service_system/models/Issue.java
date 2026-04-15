package com.jerzymaj.hotel_guest_service_system.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jerzymaj.hotel_guest_service_system.enums.IssueStatus;
import com.jerzymaj.hotel_guest_service_system.enums.IssueType;
import com.jerzymaj.hotel_guest_service_system.enums.PreferredTimeOption;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "issues")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType type;

    @NotBlank
    @Column(nullable = false)
    @ToString.Include
    private String title;

    @NotBlank
    @Column(nullable = false, length = 3000)
    private String description;

    @NotNull
    @Column(nullable = false)
    @ToString.Include
    private int roomNumber;

    private String photoPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreferredTimeOption preferredTimeOption;

    private LocalDate preferredDate;

    private LocalTime preferredTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IssueStatus status = IssueStatus.NEW;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    @ToString.Include
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
