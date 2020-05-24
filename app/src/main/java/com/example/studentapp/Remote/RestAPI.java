package com.example.studentapp.Remote;

import com.example.studentapp.Model.Course;
import com.example.studentapp.Model.Member;
import com.example.studentapp.Model.Student;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RestAPI {
    @GET("api/registeredcourses/{id}")
    Call<String> getRegisteredCourses(@Path("id") int id);
    @GET("api/availablecourses/{id}")
    Call<String> getAvailableCourses(@Path("id") int id);

    @POST("api/register")
    Observable<String> registerUser(@Body Student user);
    @POST("api/login")
    Observable<String> loginUser(@Body Student user);
    @POST("api/course")
    Observable<String> addCourse(@Body Course course);
    @POST("api/member")
    Observable<String> addMember(@Body Member member);

    @PUT("api/course/{id}")
    Call<Course> updateCourse(@Path("id") String id, @Body Course course);

    @DELETE("api/course/{id}")
    Call<Void> deleteCourse(@Path("id") String id);
    @DELETE("api/member/{courseid, id}")
    Call<Void> deleteMember(@Path("courseid") String courseId, @Path("id") String id);
}
