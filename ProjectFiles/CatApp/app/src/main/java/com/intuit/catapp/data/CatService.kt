package com.intuit.catapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ICatService {

    @GET("breeds?limit=30&page=0")
    suspend fun getBreeds(): List<Breed>
}

/**
 * Find out more information on the Cat API by checking out the documentation here:
 * https://documenter.getpostman.com/view/5578104/RWgqUxxh#intro
 */
object CatService {

    private lateinit var service: ICatService

    fun init() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(ICatService::class.java)
    }

    fun getService(): ICatService {
        return service
    }
}
