package ep.rest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.Serializable

class LoginFormActivity : AppCompatActivity(), Callback<User> {
    private val tag = LoginFormActivity::class.java.canonicalName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        login.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val geslo = etGeslo.text.toString().trim()
            Log.w(tag, "Login pressed")
            if (email != "" && geslo != "") {
                ProductService.instance.login(email, geslo).enqueue(this)
                Log.w(tag, "Login api called with values")
            } else {
                etMessage.text = String.format("Manjkajoči podatki")
            }
        }
    }

    override fun onResponse(call: Call<User>, response: Response<User>) {
        if (response.isSuccessful) {
            val res = response.body()
            Log.w(tag, "Received response")
            Log.i(tag, res.toString())
            if (res is User) {
                Log.i(tag, "Is user")
                val resultIntent = Intent()
                resultIntent.putExtra("user", res as Serializable)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                etMessage.text = String.format("Napačni podatki")
            }
        } else {
            val errorMessage = try {
                "An error occurred: ${response.errorBody()?.string()}"
            } catch (e: IOException) {
                "An error occurred: error while decoding the error message."
            }

            Log.e(tag, errorMessage)
        }
    }

    override fun onFailure(call: Call<User>, t: Throwable) {
        Log.w(tag, "Error: ${t.message}", t)
    }
}
