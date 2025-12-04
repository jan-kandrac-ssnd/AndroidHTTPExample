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

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      Column(modifier = Modifier.systemBarsPadding()) {
        val scope = rememberCoroutineScope()
        val response = remember { mutableStateOf("") }

        Button(onClick = {
          scope.launch(Dispatchers.IO) {
            response.value = call("https://dummyjson.com/users/1")
          }
        }) { Text("Download") }

        Text(response.value)
      }
    }
  }
}
