package chat.sphinx.common.components.landing

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import chat.sphinx.common.state.*


@Composable
fun ConnectingDialog() {

    OnboardingState.status()?.let { onboardingStatus ->
        when(onboardingStatus) {
            OnboardingStatus.Connecting -> {
                SphinxDialog(
                    title = "Connecting",
                    onCloseRequest = {

                    }
                ) {
                    Text(
                        text = "Connecting"
                    )
                    // TODO: Load connecting graphic...

                    Button(
                        onClick = {
                            OnboardingState.status(null)
                        }
                    ) {
                        Text(
                            text = "Cancel"
                        )
                    }
                }
            }
            OnboardingStatus.Failed -> {
                SphinxDialog(
                    title = "Successful",
                    onCloseRequest = {

                    }
                ) {
                    Column {
                        Text(
                            text = "Failed to connect"
                        )
                        Text(
                            text = "Please try again"
                        )

                        Button(
                            onClick = {
                                // Maybe might need to do something to figure out if we need to initialize
                                OnboardingState.status(null)
                            }
                        ) {
                            Text(
                                text = "Continue"
                            )
                        }
                    }
                }
            }
            OnboardingStatus.Successful -> {
                SphinxDialog(
                    title = "Successful",
                    onCloseRequest = {

                    }
                ) {
                    Column {
                        Text(
                            text = "Welcome"
                        )
                        Text(
                            text = "Your app is now connected"
                        )

                        Button(
                            onClick = {
                                // Maybe might need to do something to figure out if we need to initialize
                                AppState.screenState(ScreenType.DashboardScreen)
                            }
                        ) {
                            Text(
                                text = "Continue"
                            )
                        }
                    }
                }
            }
        }

    }

}