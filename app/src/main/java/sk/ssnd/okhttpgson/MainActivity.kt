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
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

// GET -> https://dummyjson.com/users/2

val retrofit = Retrofit.Builder()
  .client(OkHttpClient())
  .addConverterFactory(GsonConverterFactory.create())
  .baseUrl("https://dummyjson.com")
  .build()

interface DummyJsonService {
  @GET("users")
  suspend fun getAllUsers(): UsersResponse

  @GET("users/{id}")
  suspend fun getUser(@Path("id") cislo: Int): User
}

val dummyJsonService = retrofit.create<DummyJsonService>()

data class UsersResponse(val users: List<User>)

data class UserAddress(val city: String)

data class User(
  val id: Int,
  val firstName: String,
  val lastName: String,
  val email: String,
  val image: String,
  val address: UserAddress
)

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
            val user = dummyJsonService.getUser(10)
            response.value = user
          }
        }) { Text("Download") }

        val user = response.value
        if (user != null) {
          AsyncImage(
            model = user.image,
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_foreground),
            fallback = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
          )
          Text("${user.firstName} ${user.lastName}")
          Text(user.email)
          Text(user.address.city)
        }
      }
    }
  }
}
