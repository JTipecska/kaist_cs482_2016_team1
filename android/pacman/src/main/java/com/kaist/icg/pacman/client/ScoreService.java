package com.kaist.icg.pacman.client;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class ScoreService {
    private static IScoreService SERVICE;

    public static IScoreService getService() {
        if(SERVICE == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://pacman.bastienbaret.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            SERVICE = retrofit.create(IScoreService.class);
        }

        return SERVICE;
    }
}
