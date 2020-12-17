package ep.rest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_product_form.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProductFormActivity : AppCompatActivity(), Callback<Void> {

    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_form)

        login.setOnClickListener {
            val ime = etTitle.text.toString().trim()
            val opis = etDescription.text.toString().trim()
            val cena = etPrice.text.toString().trim().toDouble()

            if (product == null) { // dodajanje                          \/ CreatorId temporarily statically defined
                ProductService.instance.insert(ime, opis, cena, 1).enqueue(this)
            } else { // urejanje
                ProductService.instance.update(product!!.id, ime, opis, cena).enqueue(this)
            }
        }

        val product = intent?.getSerializableExtra("ep.rest.product") as Product?
        if (product != null) {
            etTitle.setText(product.ime)
            etPrice.setText(product.cena.toString())
            etDescription.setText(product.opis)
            this.product = product
        }
    }

    override fun onResponse(call: Call<Void>, response: Response<Void>) {
        val headers = response.headers()

        if (response.isSuccessful) {
            val id = if (product == null) {
                // Preberemo Location iz zaglavja
                Log.i(TAG, "Insertion completed.")
                val parts = headers.get("Location")?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                // spremenljivka id dobi vrednost, ki jo vrne zadnji izraz v bloku
                parts?.get(parts.size - 1)?.toInt()
            } else {
                Log.i(TAG, "Editing saved.")
                // spremenljivka id dobi vrednost, ki jo vrne zadnji izraz v bloku
                product!!.id
            }

            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("ep.rest.id", id)
            startActivity(intent)
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody()?.string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Log.e(TAG, errorMessage)
        }
    }

    override fun onFailure(call: Call<Void>, t: Throwable) {
        Log.w(TAG, "Error: ${t.message}", t)
    }

    companion object {
        private val TAG = ProductFormActivity::class.java.canonicalName
    }
}
