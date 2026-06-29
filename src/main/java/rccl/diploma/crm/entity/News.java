package rccl.diploma.crm.entity;

import jakarta.persistence.*;
import lombok.*;
import rccl.diploma.crm.entity.enums.NewsCategory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Builder.Default
    private Boolean isValid = true;

    @Column
    private NewsCategory category;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private LocalDateTime validUntil;

    @Column
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsPhoto> photos = new ArrayList<>();

    public void addPhoto(NewsPhoto photo) {
        if (photo != null){
            photos.add(photo);
            photo.setNews(this);
        }
    }

    public void toggleValid() {
        isValid = !isValid;
    }
}
