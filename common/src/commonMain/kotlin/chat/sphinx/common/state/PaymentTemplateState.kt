package chat.sphinx.common.state

import chat.sphinx.wrapper.payment.PaymentTemplate

data class PaymentTemplateState(
    val templateList: ArrayList<String>? = arrayListOf("")
)
