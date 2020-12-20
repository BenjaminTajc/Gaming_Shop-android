package ep.rest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.content_product_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class ProductDetailActivity : AppCompatActivity() {
    private val tag = ProductDetailActivity::class.java.canonicalName
    private var product: Product = Product()
    private var loggedIn: Boolean? = false
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra("ep.rest.id", 0)
        loggedIn = intent.getBooleanExtra("loggedIn", false)

        if(loggedIn == true) {
            user = intent.getSerializableExtra("user") as User
        }

        if (id > 0) {
            ProductService.instance.get(id).enqueue(OnLoadCallbacks(this))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        Log.i(tag, "OnOptionsItemSelected")
        if(id == android.R.id.home) {
            super.onBackPressed()
            val returnIntent = Intent(this, MainActivity::class.java)
            val bundle = Bundle()
            bundle.putBoolean("loggedIn", loggedIn as Boolean)
            if (loggedIn == true) {
                bundle.putSerializable("loggedIn", user)
            }
            Log.i(tag, "Return")
            Log.i(tag, loggedIn.toString())
            Log.i(tag, user.toString())
            returnIntent.putExtras(bundle)
            startActivity(returnIntent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private class OnLoadCallbacks(val activity: ProductDetailActivity) : Callback<Product> {
        private val tag = this::class.java.canonicalName

        override fun onResponse(call: Call<Product>, response: Response<Product>) {
            activity.product = response.body() ?: Product()

            Log.i(tag, "Got result: ${activity.product}")

            if (response.isSuccessful) {
                activity.produkt_opis.text = String.format("Description: %s", activity.product.opis)
                activity.produkt_cena.text = String.format(Locale.ENGLISH, "Price: %.2f EUR", activity.product.cena)

                val imageView = activity.findViewById<ImageView>(R.id.imageView3)
                val imgName = activity.product.img_name
                val imgBase = "http://10.0.2.2:8080/dev/Gaming_Shop/static/slike/"
                val imgUrl = imgBase + imgName
                Picasso.get()
                        .load(imgUrl)
                        .into(imageView)
                activity.toolbarLayout.title = activity.product.ime
            } else {
                val errorMessage = try {
                    "An error occurred: ${response.errorBody()?.string()}"
                } catch (e: IOException) {
                    "An error occurred: error while decoding the error message."
                }

                Log.e(tag, errorMessage)
                activity.produkt_opis.text = errorMessage
            }
        }



        override fun onFailure(call: Call<Product>, t: Throwable) {
            Log.w(tag, "Error: ${t.message}", t)
        }
    }
}

