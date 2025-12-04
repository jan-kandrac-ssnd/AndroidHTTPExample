package sk.ssnd.okhttpgson

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

// GET -> https://dummyjson.com/users/1

// Builder pattern -> OOP design patterns
fun call(url: String): String {
  val client = OkHttpClient()
  val request = Request.Builder()
    .url(url)
    .get()
    .build()
  val call = client.newCall(request)
  val response = call.execute()
  return response.body.string()
}

data class UserAddress(val city: String)

data class User(
  val id: Int,
  val firstName: String,
  val lastName: String,
  val email: String,
  val image: String,
  val address: UserAddress
)

fun transformJsonToUser(json: String) : User {
  return Gson().fromJson(json, User::class.java)
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      Column(modifier = Modifier.systemBarsPadding()) {
        val scope = rememberCoroutineScope()
        val response = remember { mutableStateOf<User?>(null) }

        Button(onClick = {
          scope.launch(Dispatchers.IO) {
            val str = call("https://dummyjson.com/users/1")
            val user = transformJsonToUser(str)
            response.value = user
          }
        }) { Text("Download") }

        val user = response.value
        if (user != null) {
          Text("${user.firstName} ${user.lastName}")
          Text(user.email)
          Text(user.address.city)
        }
      }
    }
  }
}
