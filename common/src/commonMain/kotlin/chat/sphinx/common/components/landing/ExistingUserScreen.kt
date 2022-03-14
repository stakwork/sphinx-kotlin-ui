package chat.sphinx.common.components.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import chat.sphinx.common.Res
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.store.ExistingUserStore
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.onKeyUp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExistingUserScreen(
    existingUserStore: ExistingUserStore
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(SolidColor(Color.Blue), alpha = 0.50f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = imageResource(Res.drawable.existing_user_image),
                    contentDescription = "Existing user graphic",
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Connect your keys from your sphinx app and paste it here.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }

        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(SolidColor(Color.Black), alpha = 0.50f)
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                TopAppBar(
                    title = { Spacer(modifier = Modifier.height(8.dp)) },

                    elevation = 0.dp,
                    backgroundColor = Color.Transparent,
                    navigationIcon = {
                        IconButton(onClick = {
                            LandingScreenState.screenState(LandingScreenType.LandingPage)
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                        }
                    }
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "Connect",
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                    ) {
                        OutlinedTextField(
                            value = existingUserStore.state.sphinxKeys,
                            modifier = Modifier
                                .weight(weight = 1F)
                                .onKeyEvent(onKeyUp(Key.Enter, existingUserStore::onSubmitKeys)),
                            onValueChange = existingUserStore::onKeysTextChanged,
                            singleLine = true,
                            label = { Text(text = "Paste your keys...") }
                        )
                    }

                    existingUserStore.state.errorMessage?.let { errorMessage ->
                        Text(
                            text = errorMessage,
                            color = Color.Red
                        )
                    }

                    Button(
                        onClick = {
                            // TODO: Handle exiting user...
                        }
                    ) {
                        Text(
                            text = "Submit"
                        )
                    }
                }
            }

        }
    }

}