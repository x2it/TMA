package com.realtor.geeksales.telephony

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telecom.TelecomManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DialerHelper @Inject constructor(
    @ApplicationContext private val ctx: Context
) {
    companion object {
        private const val TAG = "DialerHelper"

        /** 归一化号码：仅保留数字 */
        fun normalize(phone: String): String = phone.filter { it.isDigit() }
    }

    /**
     * 使用 ACTION_DIAL 打开系统拨号盘（无需 CALL_PHONE 权限，但需用户点"拨打"）。
     * 此为首选，合规 & 稳妥。
     */
    fun openDialer(phone: String) {
        val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${normalize(phone)}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { ctx.startActivity(i) }
            .onFailure { Log.w(TAG, "openDialer failed", it) }
    }

    /** 直接发起 CALL：需要 Manifest.permission.CALL_PHONE 运行时授权。 */
    fun directCall(phone: String) {
        val i = Intent(Intent.ACTION_CALL, Uri.parse("tel:${normalize(phone)}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { ctx.startActivity(i) }
            .onFailure { Log.w(TAG, "directCall failed", it) }
    }

    /** 当前是否处于通话中（仅粗略判断，读 READ_PHONE_STATE 时才准确） */
    fun isInCall(): Boolean {
        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as? android.telephony.TelephonyManager
        return tm?.callState != android.telephony.TelephonyManager.CALL_STATE_IDLE
    }

    fun defaultDialerPackage(): String? {
        val tm = ctx.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
        return tm?.defaultDialerPackage
    }
}
