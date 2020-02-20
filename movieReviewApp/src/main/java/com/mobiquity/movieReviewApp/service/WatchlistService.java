package com.mobiquity.movieReviewApp.service;

public interface WatchlistService {

  String addMovieToUserWatchlist(String emailId, String movieName);

  String removeMovieFromAUserWatchlist(String emailId, String movieName);

  String addSeriesToUserWatchlist(String emailId, String series);

  String removeSeriesFromUserWatchlist(String emailId, String series);

}
