package rccl.diploma.crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Column(nullable = false, length = 500)
    private String filePath;               // /uploads/requests/123/photo_2026-02-08_19-45.jpg

    @Column(nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    @Column
    private String originalFileName;

    @Column
    private Long fileSize;

    @Column(length = 100)
    private String contentType;

}