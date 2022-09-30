package chat.sphinx.common.state

sealed class BubbleBackground {
    object Gone: BubbleBackground()

    sealed class First: BubbleBackground() {
        object Grouped: First()
        object Isolated: First()
    }

    object Middle: BubbleBackground()

    object Last: BubbleBackground()
}
