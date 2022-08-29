//package utils
//
//import androidx.compose.ui.text.toLowerCase
//import io.ktor.util.*
//import java.util.*
//import kotlin.collections.HashMap
//import kotlin.text.toCharArray
//
////
////  PaymentRequestDecoder.swift
////  com.stakwork.sphinx.desktop
////
////  Created by Tomas Timinskas on 11/05/2020.
////  Copyright © 2020 Sphinx. All rights reserved.
////
//
//class PaymentRequestDecoder {
//    companion object{
//
//        val prefixes = listOf("lnbc", "lntb", "lnbcrt")
//    }
//    internal var paymentRequestString: String? = null
//    internal var decodedPR: Map<String,Any>? = null
//    private val checksumMarker: Char = Char("1")
//    private val bech32CharValues = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"
//
//    internal fun isPaymentRequest() : Boolean =
//        decodedPR != null
//
//    internal fun getAmount() : Int? {
//        val pr = decodedPR ?: return null
//        val hrp = pr["human_readable_part"] as? HashMap<String,Any>?
//        if (hrp != null) {
//            val mSatAmountString = hrp["amount"] as? String
//            if (mSatAmountString != null) {
//                val amountInt = mSatAmountString.toInt()
//                if (amountInt != null) {
//                    return amountInt / 1000
//                }
//            }
//        }
//        return null
//    }
//
//    internal fun getDate() : Date? {
//        val pr = decodedPR ?: return null
//        val data = pr["data"] as? HashMap<String,Any>
//        if (data != null) {
//            val date = data["time_stamp"] as? Date
//            if (date != null) {
//                return date
//            }
//        }
//        return null
//    }
//
//    internal fun getTagWith(type: String) : HashMap<String,Any>? {
//        val pr = decodedPR ?: return null
//        val data = pr["data"] as? HashMap<String,Any>
//        if (data != null) {
//            val tags = data["tags"] as? List<HashMap<String,Any>>
//            if (tags != null) {
//                for (tag in tags) {
//                    val t = tag["type"] as? String
//                    if (t != null && t == type) {
//                        return tag
//                    }
//                }
//            }
//        }
//        return null
//    }
//
//    internal fun getExpirationDate() : Date? {
//        val pr = decodedPR ?: return null
//        val data = pr["data"] as? HashMap<String,Any>
//        if (data != null) {
//            val date = data["time_stamp"] as? Date
//            if (date != null) {
//                var expiry = 3600
//                val expiryTag = getTagWith(type = "x")
//                if (expiryTag != null) {
//                    val value = expiryTag["value"] as? Int
//                    if (value != null) {
//                        expiry = value
//                    }
//                }
//                return date.addingTimeInterval(TimeInterval(expiry))
//            }
//        }
//        return null
//    }
//
//    internal fun getMemo() : String? {
//        val descriptionTag = getTagWith(type = "d")
//        if (descriptionTag != null) {
//            val memo = descriptionTag["value"] as? String
//            if (memo != null) {
//                return memo
//            }
//        }
//        return null
//    }
//
//    internal fun decodePaymentRequest(paymentRequest: String) {
//        this.paymentRequestString = paymentRequest
//        val input = paymentRequest.lowercase().replace("lightning:", "")
//        val splitPosition = input.indexOfLast { it==checksumMarker }
//        if (splitPosition != null) {
//            val index = splitPosition.utf16Offset(in = input) - 1
//            if (index < 0) {
//                return
//            }
//            val humanReadablePart = input.substring(0, index)
//            val startIndex = splitPosition.utf16Offset(in = input) + 1
//            val endIndex = input.length - 6
//            if (endIndex < startIndex) {
//                return
//            }
//            val data = input.substring(startIndex,endIndex)
//            val checksum = input.substring( input.length - 6,  input.length)
//            if ((!verifyChecksum(hrp = humanReadablePart, data = bech32ToFiveBitArray(str = "${data}${checksum}")))) {
//                decodedPR = null
//                return
//            }
//            val hdr = decodeHumanReadablePart(humanReadablePart = humanReadablePart)
//            val d = decodeData(data = data, humanReadablePart = humanReadablePart)
//            if (hdr == null || d == null) {
//                return
//            }
//            decodedPR = mapOf("human_readable_part" to hdr, "data" to d, "checksum" to checksum)
//            return
//        }
//        decodedPR = null
//    }
//
//    internal fun verifyChecksum(hrp: String, data: List<Int>) : Boolean {
//        val hrp = expand(str = hrp)
//        val all = hrp
//        val bool = polymod(values = all)
//        return bool
//    }
//
//    internal fun expand(str: String) : List<Int> {
//        var array = mutableListOf<Int>()
//        for (i in 0 until str.length) {
//            val asciiCode = UnicodeScalar(String(str[String.Index(utf16Offset = i, in = str)]))?.value
//            if (asciiCode != null) {
//                val asciiCodeInt = Int(asciiCode)
//                array.add(asciiCodeInt >> 5)
//            }
//        }
//        array.add(0)
//        for (i in 0 until str.length) {
//            val asciiCode = UnicodeScalar(String(str[String.Index(utf16Offset = i, in = str)]))?.value
//            if (asciiCode != null) {
//                val asciiCodeInt = Int(asciiCode)
//                array.add(asciiCodeInt & 31)
//            }
//        }
//        return array
//    }
//
//    internal fun polymod(values: List<Int>) : Boolean {
//        val GEN = listOf(0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3)
//        var chk = 1
//        for (value in values) {
//            val b = (chk >> 25)
//            chk = (chk & 0x1ffffff) << 5 ^ value
//            for (i in 0 until 5) {
//                if ((((b >> i) & 1) == 1)) {
//                    chk ^= GEN[i]
//                } else {
//                    chk ^= 0
//                }
//            }
//        }
//        return Bool(truncating = NSNumber(value = chk))
//    }
//
//    internal fun decodeHumanReadablePart(humanReadablePart: String) : kotlin.collections.Map<String,String>? {
//        var prefix: String = ""
//        for (pref in PaymentRequestDecoder.prefixes) {
//            val p = humanReadablePart.substring(0, pref.length)
//        }
//        //            if (p == pref) {
//        //                prefix = pref
//        //            }
//        if ((prefix == "")) {
//            return null
//        }
//        val amountString = humanReadablePart.substring(prefix.length, humanReadablePart.length)
//        val amount = decodeAmount(str = amountString.toString())
//        if (amount != null) {
//            return mapOf("prefix" to prefix, "amount" to amount)
//        }
//        return null
//    }
//
//    internal fun decodeData(data: String, humanReadablePart: String) : Map<String,Any>? {
//        val date32 = data.substring( 0,  7)
//        val dateEpoch = epochToDate(int = bech32ToInt(str =date32.toString()))
//        val signature = data.substring(data.length - 104,  data.length)
//        val tagData = data.substring(7, data.length - 104)
//        val decodedTags = decodeTags(tagData = tagData.toString())
//        var value = bech32ToFiveBitArray(str = "${date32}${tagData}")
//        value = fiveBitArrayTo8BitArray(int5Array = value, includeOverflow = true)
//        val first = textToHexString(text = humanReadablePart)
//        val second = byteArrayToHexString(byteArray = value)
//        val valueString = "${first}${second}"
//        return mapOf("time_stamp" to dateEpoch, "tags" to decodedTags, "signature" to decodeSignature(signature = signature.toString()), "signing_data" to valueString)
//    }
//
//    internal fun decodeTags(tagData: String) : List<Map<String,Any>> {
//        val tags = extractTags(str = tagData)
//        var decodedTags = mutableListOf<Map<String,Any>>()
//        for (tag in tags) {
//            val type = tag["type"]
//            val length = tag["length"]
//            val lengthInt = length?.toIntOrNull()
//            val data = tag["data"]
//            if (type != null && length != null && lengthInt != null && data != null) {
//                decodedTags.add(decodeTag(type = type, length = lengthInt, data = data))
//            }
//        }
//        return decodedTags
//    }
//
//    internal fun decodeSignature(signature: String) : Map<String,Any> {
//        val data = fiveBitArrayTo8BitArray(int5Array = bech32ToFiveBitArray(str = signature), includeOverflow = false)
//        val recoveryFlag = data[data.size - 1]
//        val r = byteArrayToHexString(byteArray =data.toList())
//        val s = byteArrayToHexString(byteArray = data.toList())
//        return mapOf("r" to r, "s" to s, "recovery_flag" to recoveryFlag)
//    }
//
//    internal fun extractTags(str: String) : List<Map<String, String>> {
//        var tags = mutableListOf<Map<String,String>>()
//        var string = str
//        while ((string.isNotEmpty())) {
//            val typeString = string.toCharArray()[0]
//            val substring = string.substring(1, 3)
//            val dataLength = bech32ToInt(substring)
//            val data = string.substring( 3, dataLength + 3)
//            val map= mapOf<String,String>(
//                "type" to typeString.toString(),
//                "length" to dataLength.toString(),"data" to data
//            )
//            tags.add(map)
//            if (3 + dataLength <= string.length) {
//                string = string.substring(3 + dataLength,  string.length)
//            } else {
//                string = ""
//            }
//        }
//        return tags
//    }
//
//    internal fun decodeTag(type: String, length: Int, data: String) : Map<String,Any> {
//        when ((type)) {
//            "p" -> {
//                if ((length != 52)) {
//                    return mapOf()
//                }
//                return mapOf("type" to type, "length" to "${length}", "description" to "payment_hash", "value" to byteArrayToHexString(byteArray = fiveBitArrayTo8BitArray(int5Array = bech32ToFiveBitArray(str = data), includeOverflow = false)))
//            }
//            "d" -> return mapOf("type" to type, "length" to "${length}", "description" to "description", "value" to bech32ToUTF8String(str = data))
//            "n" -> {
//                if ((length != 53)) {
//                    return mapOf()
//                }
//                return mapOf("type" to type, "length" to "${length}", "description" to "payee_public_key", "value" to byteArrayToHexString(byteArray = fiveBitArrayTo8BitArray(int5Array = bech32ToFiveBitArray(str = data), includeOverflow = false)))
//            }
//            "h" -> {
//                if ((length != 52)) {
//                    return mapOf()
//                }
//                return mapOf("type" to type, "length" to "${length}", "description" to "description_hash", "value" to data)
//            }
//            "x" -> return mapOf("type" to type, "length" to "${length}", "description" to "expiry", "value" to bech32ToInt(str = data))
//            "c" -> return mapOf("type" to type, "length" to "${length}", "description" to "min_final_cltv_expiry", "value" to bech32ToInt(str = data))
//            "f" -> {
//                 val versionString = data.toCharArray()[0]
//                    val version = bech32ToFiveBitArray(str = versionString.toString())[0]
//                if ((version < 0 || version > 18)) {
//                    return mapOf()
//                }
//                 val fallbackAddress = data.substring(1,  data.length)
//                 val versionDictionary = mapOf("version" to version, "fallback_address" to fallbackAddress)
//                return mapOf("type" to type, "length" to "${length}", "description" to "fallback_address", "value" to versionDictionary)
//            }
//            "r" -> {
//               val rData = fiveBitArrayTo8BitArray(int5Array = bech32ToFiveBitArray(str = data), includeOverflow = false)
//               val pubkey = rData.subList(0,33)
//               val shortChannelId = rData.subList(33,41)
//               val feeBaseMsat = rData.subList(41,45)
//               val feeProportionalMillionths = rData.subList(45,49)
//               val cltvExpiryDelta = rData.subList(49,51)
//               val valueDictionary  = mapOf("public_key" to byteArrayToHexString(byteArray = pubkey), "short_channel_id" to byteArrayToHexString(byteArray = shortChannelId), "fee_base_msat" to byteArrayToInt(byteArray = feeBaseMsat), "fee_proportional_millionths" to byteArrayToInt(byteArray = feeProportionalMillionths), "cltv_expiry_delta" to byteArrayToInt(byteArray = cltvExpiryDelta))
//                return mapOf("type" to type, "length" to length, "description" to "routing_information", "value" to valueDictionary)
//            }
//            else -> return mapOf()
//        }
//    }
//
//    internal fun bech32ToInt(str: String) : Int {
//        var sum = 0
//        for (i in 0 until str.length) {
//            sum = sum * 32
//            val charAtIndex = str.toCharArray()[i]
//            val indexOf = bech32CharValues.indexOfFirst { it==charAtIndex }
//            if (indexOf != null) {
//                val i = indexOf.utf16Offset(in = str)
//                sum = sum + i
//            }
//        }
//        return sum
//    }
//
//    internal fun bech32ToFiveBitArray(str: String) : List<Int> {
//        var array = listOf<Int>()
//        for (i in 0 until str.length) {
//            val charAtIndex = str.toCharArray()[i]
//            val indexOf = bech32CharValues.indexOfFirst { it===charAtIndex }
//            if (indexOf != null) {
//                val i = indexOf.utf16Offset(in = str)
//                array.append(i)
//            }
//        }
//        return array
//    }
//
//    internal fun byteArrayToInt(byteArray: List<Int>) : Int {
//        var value = 0
//        for (i in 0 until byteArray.size) {
//            value = (value << 8) + byteArray[i]
//        }
//        return value
//    }
//
//    internal fun fiveBitArrayTo8BitArray(int5Array: List<Int>, includeOverflow: Boolean) : List<Int> {
//        var count = 0
//        var buffer = 0
//        var byteArray = mutableListOf<Int>()
//        for (value in int5Array) {
//            buffer = (buffer << 5) + value
//            count += 5
//            if ((count >= 8)) {
//                byteArray.add(buffer >> (count - 8) & 255)
//                count -= 8
//            }
//        }
//        if ((includeOverflow && count > 0)) {
//            byteArray.add(buffer << (8 - count) & 255)
//        }
//        return byteArray
//    }
//
//    internal fun bech32ToUTF8String(str: String) : String {
//        val int5Array = bech32ToFiveBitArray(str = str)
//        val byteArray = fiveBitArrayTo8BitArray(int5Array = int5Array, includeOverflow = false)
//        var utf8String = ""
//        for (i in 0 until byteArray.size) {
//            val string = "0${String.format( "%02X", byteArray[i])}"
//            val substring = string.substring(string.length - 2, string.length)
//            utf8String = "${utf8String}%${substring}"
//        }
//        val uri = utf8String.decodeUrl()
//        if (uri != null) {
//            return uri
//        }
//        return ""
//    }
//
//    internal fun byteArrayToHexString(byteArray: List<Int>) : String {
//        val string = byteArray.map { byte  ->
//            val b = (byte & 0xFF)
//            val bString = "0${String.format("%02X", b)}"
//            val bSubstring = bString.substring(bString.length - 2,bString.length)
//            "${bSubstring}"
//        }
//        return string.joinToString(separator = "").toLowerCase()
//    }
//
//    internal fun textToHexString(text: String) : String {
//        val data = Data(text.utf8)
//        val hexString = data.map { it->String.format("%02x", it) }.joined()
//        return hexString
//    }
//
//    internal fun epochToDate(int: Int) : Date {
//        val date = Date(timeIntervalSince1970 = TimeInterval(int))
//        return date
//    }
//
//    internal fun decodeAmount(str: String) : String? {
//        if (str.isEmpty()) {
//            return null
//        }
//        val multiplier = str.toCharArray()[str.length - 1]
//        val amount = str.substring(0, str.length - 1)
//        val firstAmountChar = str.toCharArray()[0]
//        if (firstAmountChar == "0".toCharArray()[0]) {
//            return "error"
//        }
//        val amountInt =amount.toIntOrNull()
//        if (amountInt != null) {
//            if ((amountInt < 0)) {
//                return "error"
//            }
//            when (multiplier.toString()) {
//                "" -> return "Any amount"
//                "p" -> return "${amountInt / 10}"
//                "n" -> return "${amountInt * 100}"
//                "u" -> return "${amountInt * 100000}"
//                "m" -> return "${amountInt * 100000000}"
//                else -> return "error"
//            }
//        }
//        return "error"
//    }
//}
