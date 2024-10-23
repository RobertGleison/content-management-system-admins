package com.example.cms;


public class Movie {
    private String movieTitle;
    private String movieDescription;
    private String movieGenre;
    private Integer movieYear;
    private String moviePublisher;
    private Integer movieDuration;

    public Movie(String movieTitle, String movieDescription, String movieGenre, Integer movieYear, String moviePublisher, Integer movieDuration) {
        this.movieTitle = movieTitle;
        this.movieDescription = movieDescription;
        this.movieGenre = movieGenre;
        this.movieYear = movieYear;
        this.moviePublisher = moviePublisher;
        this.movieDuration = movieDuration;
    }

    public Movie() {
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public String getMovieGenre() {
        return movieGenre;
    }

    public void setMovieGenre(String movieGenre) {
        this.movieGenre = movieGenre;
    }

    public Integer getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(Integer movieYear) {
        this.movieYear = movieYear;
    }

    public String getMoviePublisher() {
        return moviePublisher;
    }

    public void setMoviePublisher(String moviePublisher) {
        this.moviePublisher = moviePublisher;
    }

    public Integer getMovieDuration() {
        return movieDuration;
    }

    public void setMovieDuration(Integer movieDuration) {
        this.movieDuration = movieDuration;
    }
}
