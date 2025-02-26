package com.intuit.catapp.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.intuit.catapp.R
import com.intuit.catapp.data.Breed
import com.intuit.catapp.ui.theme.CatAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: BreedsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = BreedsViewModel()

        setContent {
            // Set up to observe the ViewModel's LiveData and update the composable state as it's value changes
            var breedsData: List<Breed> by remember { mutableStateOf(emptyList()) }
            viewModel.breedsLiveData.observe(this) {
                breedsData = it.data ?: emptyList()
            }

            // Trigger GET Breeds from the API asynchronously
            viewModel.getBreeds()

            CatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Column {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(12.dp),
                            text = "Cat Breeds",
                            fontSize = 32.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        BreedsList(data = breedsData)
                    }
                }

                /**
                 * If you want to use traditional Android View heirarchy components, you can do so
                 * leveraging the interoperability of Compose with the `AndroidView` composable function.
                 *
                 * This allows you a way to define the `factory` param (a lambda that provides context and
                 * returns a View) to integrate Android Views around and inter-mingled with compose content.
                 *
                 * For more information, please check out the Android docs:
                 * https://developer.android.com/jetpack/compose/migrate/interoperability-apis/views-in-compose
                 */
                /*
                 AndroidView(
                    factory = { context ->
                        View(context)
                    },
                    modifier = Modifier,
                    update = { view ->
                        ...
                    }
                )
                 */
            }
        }
    }
}

@Composable
fun BreedsList(data: List<Breed>) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedBreed: Breed? by remember { mutableStateOf(null) }

    if (showDialog && selectedBreed != null) {
        ImageDialog(breed = selectedBreed!!) {
            showDialog = false
            selectedBreed = null
        }
    }

    LazyColumn {
        items(data) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        selectedBreed = it
                        showDialog = true
                    },
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.surface,
                elevation = 4.dp,
                border = BorderStroke(1.dp, MaterialTheme.colors.secondary)
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = it.name,
                    color = MaterialTheme.colors.primary,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ImageDialog(breed: Breed, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.height(300.dp),
            color = MaterialTheme.colors.surface,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, Color.Black)
        ) {
            AsyncImage(
                model = breed.getImageUrl(),
                contentDescription = breed.name,
                modifier = Modifier
                    .padding(12.dp)
                    .height(250.dp),
                contentScale = ContentScale.Fit,
                onError = { _ ->
                    Log.e(MainActivity::class.simpleName, "Async Image failed to load for breed: ${breed.name}")
                },
                error = painterResource(id = R.drawable.ic_image_error)
            )
        }
    }
}
