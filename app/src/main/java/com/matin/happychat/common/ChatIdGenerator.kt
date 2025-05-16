package com.matin.happychat.common.model

import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

object ChatIdGenerator {
    private val counter = AtomicInteger(0)
    private const val COUNTER_BITS = 22
    private const val COUNTER_MASK = (1 shl COUNTER_BITS) - 1

    fun nextId(): String {
        val millis = Instant.now().toEpochMilli()
        // increment & wrap the counter within 22 bits
        val seq = counter.updateAndGet { (it + 1) and COUNTER_MASK }
        // pack into one Long
        val id64 = (millis shl COUNTER_BITS) or seq.toLong()
        // Base62 encode
        return base62Encode(id64)
    }

    private const val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private fun base62Encode(value: Long): String {
        var v = value
        if (v == 0L) return "0"
        val sb = StringBuilder()
        while (v != 0L) {
            val idx = (v % 62).toInt()
            sb.append(ALPHABET[idx])
            v /= 62
        }
        return sb.reverse().toString()
    }
}
