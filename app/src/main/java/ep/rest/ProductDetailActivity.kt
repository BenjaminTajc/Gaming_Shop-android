package ep.rest

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.content_product_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class ProductDetailActivity : AppCompatActivity() {
    private var product: Product = Product()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        setSupportActionBar(toolbar)

//        fabEdit.setOnClickListener {
//            val intent = Intent(this, ProductFormActivity::class.java)
//            intent.putExtra("ep.rest.product", product)
//            startActivity(intent)
//        }

//        fabDelete.setOnClickListener {
//            val dialog = AlertDialog.Builder(this)
//            dialog.setTitle("Confirm deletion")
//            dialog.setMessage("Are you sure?")
//            dialog.setPositiveButton("Yes") { _, _ -> deleteBook() }
//            dialog.setNegativeButton("Cancel", null)
//            dialog.create().show()
//        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra("ep.rest.id", 0)

        if (id > 0) {
            ProductService.instance.get(id).enqueue(OnLoadCallbacks(this))
        }
    }

    private fun deleteBook(){
        ProductService.instance.delete(this.product.id).enqueue(object : Callback<Void> {
            private val tag = this::class.java.canonicalName

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.w(tag, "Error: ${t.message}", t)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    val intent = Intent(getApplicationContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

    private class OnLoadCallbacks(val activity: ProductDetailActivity) : Callback<Product> {
        private val tag = this::class.java.canonicalName

        override fun onResponse(call: Call<Product>, response: Response<Product>) {
            activity.product = response.body() ?: Product()

            Log.i(tag, "Got result: ${activity.product}")

            if (response.isSuccessful) {
                activity.produkt_ime.text = String.format("Name: %s",activity.product.ime)
                activity.produkt_opis.text = String.format("Description: %s",activity.product.opis)
                activity.produkt_cena.text = String.format(Locale.ENGLISH, "Price: %.2f EUR", activity.product.cena)
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

