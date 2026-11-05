
package com.pawan.moviebooking;

import jakarta.persistence.*;

@Entity
@Table(name = "screens")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String screenName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    protected Screen() {
    }

    public Screen(String screenName, Theater theater) {
        this.screenName = screenName;
        this.theater = theater;
    }

    public Integer getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }
}
