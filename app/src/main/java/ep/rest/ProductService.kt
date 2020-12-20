package ep.rest

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object ProductService {

    interface RestApi {

        companion object {
            // AVD emulator
            const val URL = "http://10.0.2.2:8080/dev/Gaming_Shop/index.php/api/v1/"
            // Genymotion
            //const val URL = "http://10.0.3.2:8080/netbeans/mvc-rest/api/"
        }

        @GET("products")
        fun getAll(): Call<List<Product>>

        @GET("product")
        fun get(@Query("id") id: Int): Call<Product>

        @FormUrlEncoded
        @POST("products")
        fun insert(@Field("ime") ime: String,
                   @Field("opis") opis: String,
                   @Field("cena") cena: Double,
                   @Field("creatorId") creatorId: Int): Call<Void>

        @FormUrlEncoded
        @PUT("products/{id}")
        fun update(@Path("id") id: Int,
                   @Field("ime") ime: String,
                   @Field("opis") opis: String,
                   @Field("cena") cena: Double): Call<Void>

        @DELETE("products/{id}")
        fun delete(@Path("id") id: Int): Call<Void>

        @FormUrlEncoded
        @POST("login")
        fun login(@Field("email") email: String,
                  @Field("geslo") geslo: String): Call<User>
    }

    val instance: RestApi by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl(RestApi.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        retrofit.create(RestApi::class.java)
    }
}
