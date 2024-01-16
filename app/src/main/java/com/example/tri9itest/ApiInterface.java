package com.example.tri9itest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("/createTrip")
    Call<TripsResult> getTripInformation(@Body TripsResult tripRequest);
    @POST("/createCar")
    Call<CarResult> getCarInformation(@Body CarResult CarRequest);
    @GET("/GetTrips")
    Call<List<TripsResult>> getTrips();
    @GET("/GetCars")
    Call<List<CarResult>> getCars();
    @POST("/getCarById")
    Call<CarResult> getCarInformation(@Body CarIdRequest request);
    @POST("/deleteCar")
    Call<CarResult> DropCar(@Body CarIdRequest request);
    @POST("/createReservation")
    Call<ReservationRequest> createReservation(@Body ReservationRequest request);
    @POST("/updateTrip")
    Call<TripPlaces> updatesTripPlaces(@Body TripPlaces request);

    @GET("/getReservation")
    Call<List<Reservation>> getReservation();
    @POST("/deletReservation")
    Call<ReservationId> dropReservation(@Body ReservationId request);
    @POST("/getReservationById")
    Call<List<Reservation>> getReservationByTrip(@Body ReservationIdTrip request);
    @POST("/getTrip")
    Call<TripsResult> getTrip(@Body TripIdRequest request);
    @POST("/getReservationByUser")
    Call<List<Reservation>> getReservationByUser(@Body ReservationIdUser request);
    @POST("/getTripsCreated")
    Call<List<TripsResult>> getTripByUser(@Body TripCreator request);

}