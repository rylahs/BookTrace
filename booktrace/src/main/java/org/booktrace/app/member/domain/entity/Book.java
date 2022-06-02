package org.booktrace.app.member.domain.entity;

import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {
    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;


    @NotNull
    private String title;

    @Column(length = 300)
    private String thumbnail;

    private String author;

    private String publisher;

    @Lob
    private String description;

    @NotNull
    private String isbn;

    private Integer totPage;

    @Column(length = 300)
    @NotNull
    private String url;

    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer score;

    private Integer readPage;

    @Lob
    private String expectation;

    @Builder
    public Book(Long id, Member member, String title, String thumbnail, String author, String publisher, String description,
                String isbn, Integer totPage, String url, BookStatus bookStatus, LocalDate startDate, LocalDate endDate,
                Integer score, Integer readPage, String expectation) {
        Assert.notNull(member, "User must not be null");
        Assert.notNull(title, "Title must not be null");
        Assert.notNull(isbn, "Isbn must not be null");
        Assert.notNull(url, "Url must not be null");

        this.id = id;
        this.member = member;
        this.title = title;
        this.thumbnail = thumbnail;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.isbn = isbn;
        this.totPage = totPage;
        this.url = url;
        this.bookStatus = bookStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.score = score;
        this.readPage = readPage;
        this.expectation = expectation;

        if(!member.getBooks().contains(this))
            member.getBooks().add(this);
    }

    public void updateBookStatus(BookStatus bookStatus, LocalDate startDate, LocalDate endDate,
                                 Integer score, Integer readPage, String expectation){

        this.bookStatus = bookStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.score = score;
        this.readPage = readPage;
        this.expectation = expectation;
    }

    public void updateBookInfo(String title, String author, String publisher, Integer totPage, String thumbnail){

        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.totPage = totPage;
        this.thumbnail = thumbnail;
    }
}
