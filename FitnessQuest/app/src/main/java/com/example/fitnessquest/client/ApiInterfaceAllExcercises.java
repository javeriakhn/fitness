package com.example.fitnessquest.client;

import com.example.fitnessquest.ExercisesBodyParts;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ApiInterfaceAllExcercises {
    @Headers({
            "X-RapidAPI-Key: b71ad0ccb3mshff6bf5d5f54b0dep14a09ajsncce4d704517c",
            "X-RapidAPI-Host: exercisedb.p.rapidapi.com"
    })

    @GET("exercises/{part}/{partName}")
    Call<List<ExercisesBodyParts>> getExercisesForBodyPart(@Path("part") String part, @Path("partName") String partName);

}
