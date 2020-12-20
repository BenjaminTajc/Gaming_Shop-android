package ep.rest

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class MainActivity : AppCompatActivity(), Callback<List<Product>> {
    private val tag = this::class.java.canonicalName

    private lateinit var adapter: ProductsAdapter

    private var user: User? = null

    private var loggedIn: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState != null) {
            user = savedInstanceState.getSerializable("user") as User
            loggedIn = savedInstanceState.getBoolean("loggedIn")
        }

        if(loggedIn == true){
            login.text = String.format("logout")
        }

        if(intent.extras != null) {
            val bundle = intent.extras
            loggedIn = bundle?.getBoolean("loggedIn")
            if(loggedIn == true) {
                user = bundle?.getSerializable("user") as User
            }
        }

        adapter = ProductsAdapter(this)
        items.adapter = adapter
        items.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val product = adapter.getItem(i)
            if (product != null) {
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra("ep.rest.id", product.id)
                if(loggedIn == true){
                    intent.putExtra("user", user)
                    intent.putExtra("loggedIn", loggedIn)
                }
                startActivity(intent)
            }
        }

        container.setOnRefreshListener { ProductService.instance.getAll().enqueue(this) }

        login.setOnClickListener {
            if(loggedIn == false) {
                val intent = Intent(this, LoginFormActivity::class.java)
                startActivityForResult(intent, 0)
            } else {
                user = null
                loggedIn = false
            }
        }

        ProductService.instance.getAll().enqueue(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("user", user)
        outState.putBoolean("loggedIn", loggedIn as Boolean)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(tag, "Activity result")
        if(requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                user = data?.getSerializableExtra("user") as? User
                loggedIn = true
                login.text = String.format("logout")
            } else if (resultCode == Activity.RESULT_CANCELED) {
                loggedIn = false
                login.text = String.format("login")
            }
        }
    }

    override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
        if (response.isSuccessful) {
            val hits = response.body() ?: emptyList()
            Log.i(tag, "Got ${hits.size} hits")
            adapter.clear()
            adapter.addAll(hits)
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody()?.string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            Log.e(tag, errorMessage)
        }
        container.isRefreshing = false
    }

    override fun onFailure(call: Call<List<Product>>, t: Throwable) {
        Log.w(tag, "Error: ${t.message}", t)
        container.isRefreshing = false
    }
}
