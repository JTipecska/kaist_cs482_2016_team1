package com.kaist.icg.pacman.client;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IScoreService {
    @GET("score")
    Call<List<Score>> getAllScores();

    @GET("score/{name}/{score}")
    Call<AddScoreResponse> addNewScore(@Path("name") String name, @Path("score") int score);
}
