package chat.sphinx.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import chat.sphinx.common.SplashScreen
import chat.sphinx.common.state.SphinxState

@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
actual fun Dashboard(
    sphinxState: SphinxState
) {
    val splitterState = rememberSplitPaneState()
    val hSplitterState = rememberSplitPaneState()
    HorizontalSplitPane(
        splitPaneState = splitterState
    ) {
        first(400.dp) {
            DashboardSidebar()
        }
        second(300.dp) {
            VerticalSplitPane(splitPaneState = hSplitterState) {
                first(50.dp) {
                    Box(Modifier.background(Color.Blue).fillMaxSize())
                }
                second(20.dp) {
                    Box(Modifier.background(Color.Green).fillMaxSize())
                }
            }
        }
        splitter {
            visiblePart {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colors.background)
                )
            }
            handle {
                Box(
                    Modifier
                        .markAsHandle()
                        .cursorForHorizontalResize()
                        .background(SolidColor(Color.Gray), alpha = 0.50f)
                        .width(9.dp)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen()
    }
}
