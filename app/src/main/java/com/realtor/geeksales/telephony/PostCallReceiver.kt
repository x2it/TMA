package com.realtor.geeksales.telephony

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 通话结束事件总线。
 * CallObserver 监听到 OFFHOOK 后回到 IDLE 时，
 * 可发送 ACTION_CALL_ENDED 广播，随后由 UI 层订阅 [postCallEvents] 弹出登记卡片。
 */
@Singleton
class PostCallReceiver @Inject constructor(
    @ApplicationContext private val ctx: Context
) : BroadcastReceiver() {

    companion object {
        const val ACTION_CALL_ENDED = "com.realtor.geeksales.ACTION_CALL_ENDED"
        const val EXTRA_PHONE = "phone"
        const val EXTRA_DURATION_SEC = "duration_sec"

        fun send(ctx: Context, phone: String, durationSec: Int = 0) {
            val i = Intent(ACTION_CALL_ENDED).apply {
                setPackage(ctx.packageName)
                putExtra(EXTRA_PHONE, phone)
                putExtra(EXTRA_DURATION_SEC, durationSec)
            }
            ctx.sendBroadcast(i)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != ACTION_CALL_ENDED) return
    }

    fun postCallEvents() = callbackFlow {
        val filter = IntentFilter(ACTION_CALL_ENDED)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val phone = intent?.getStringExtra(EXTRA_PHONE).orEmpty()
                val dur = intent?.getIntExtra(EXTRA_DURATION_SEC, 0) ?: 0
                trySend(phone to dur)
            }
        }
        ContextCompat.registerReceiver(
            ctx, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )
        awaitClose {
            runCatching { ctx.unregisterReceiver(receiver) }
        }
    }
}
