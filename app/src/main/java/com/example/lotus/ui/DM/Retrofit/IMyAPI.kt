package com.example.lotus.ui.DM.Retrofit

import com.example.lotus.ui.DM.Model.Item
import io.reactivex.Observable
import retrofit2.http.GET

//const val BASE_URL = "https://jsonplaceholder.typicode.com/"

interface IMyAPI {
    @get:GET("photos")
    val photos:Observable<List<Item>>
}
