package ru.skypro.homework.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "ads")
public class AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column
    private String image;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments;
}
