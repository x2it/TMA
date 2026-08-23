package com.realtor.geeksales.telephony

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

data class CallStateEvent(
    val state: Int,          // TelephonyManager.CALL_STATE_*
    val phone: String,
    val atMs: Long = System.currentTimeMillis()
)

/**
 * 简单的通话状态监听：只监听 IDLE -> OFFHOOK -> IDLE 的变化，
 * 用于触发"通话结束后登记卡片"。
 *
 * 注意：不做自动拨/挂断（Android 10+ 禁止第三方 APP）。
 */
@Singleton
class CallObserver @Inject constructor(
    @ApplicationContext private val ctx: Context
) {
    @Suppress("DEPRECATION")
    fun events(): Flow<CallStateEvent> = callbackFlow {
        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val listener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                trySend(CallStateEvent(state = state, phone = incomingNumber))
            }
        }
        // 读取 READ_PHONE_STATE 需要运行时授权，若未授权则 fail-fast 静默
        runCatching {
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        }
        awaitClose {
            runCatching { tm.listen(listener, PhoneStateListener.LISTEN_NONE) }
        }
    }
}
