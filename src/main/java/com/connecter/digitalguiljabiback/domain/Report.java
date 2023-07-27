package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_pk", referencedColumnName = "pk")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="board_pk", referencedColumnName = "pk")
    private Board board;

    @NotNull
    @CreationTimestamp
    @Column(name = "create_datetime")
    @Builder.Default
    private LocalDate createDatetime = LocalDate.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @Builder.Default
    private ReportType type = ReportType.ETC;

    @NotNull
    private String reason; // 사유

}
