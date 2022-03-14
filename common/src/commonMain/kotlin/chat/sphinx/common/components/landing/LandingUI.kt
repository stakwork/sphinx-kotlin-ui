package chat.sphinx.common.components.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import chat.sphinx.common.Res
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.platform.imageResource

@Composable
fun LandingUI() {
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
                    painter = imageResource(Res.drawable.landing_page_image),
                    contentDescription = "Sphinx landing page graphic",
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "SPHINX",
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "LIGHTNING CHAT",
                    textAlign = TextAlign.Center
                )

                // TODO: Demo image...
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "WELCOME",
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {
                        LandingScreenState.screenState(LandingScreenType.NewUser)
                    }
                ) {
                    Text(
                        text = "New user"
                    )
                }

                Button(
                    onClick = {
                        LandingScreenState.screenState(LandingScreenType.ExistingUser)
                    }
                ) {
                    Text(
                        text = "Existing user"
                    )
                }

                TextButton(
                    onClick = {
                        LandingScreenState.screenState(LandingScreenType.RestoreFromKeystore)
                    }
                ) {
                    Text(
                        text = "Restore from Keychain",
                        color = Color.Black
                    )
                }
            }
        }
    }
}
