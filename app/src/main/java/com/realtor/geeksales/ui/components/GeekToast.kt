package com.realtor.geeksales.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realtor.geeksales.ui.theme.Accent
import com.realtor.geeksales.ui.theme.BgElev
import com.realtor.geeksales.ui.theme.Danger
import com.realtor.geeksales.ui.theme.Divider
import com.realtor.geeksales.ui.theme.Success
import com.realtor.geeksales.ui.theme.TextMuted
import com.realtor.geeksales.ui.theme.TextSecondary
import com.realtor.geeksales.ui.theme.Warning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ToastType { SUCCESS, ERROR, WARNING, INFO, LOADING }

class ToastState {
    var visible by mutableStateOf(false); private set
    var type by mutableStateOf(ToastType.INFO); private set
    var message by mutableStateOf(""); private set
    var progress by mutableStateOf(false); private set
    private val scope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    fun show(msg: String, t: ToastType = ToastType.INFO, autoHideMs: Long = 2000) {
        job?.cancel(); message = msg; type = t; progress = false; visible = true
        if (t != ToastType.LOADING) {
            job = scope.launch { delay(autoHideMs); visible = false }
        }
    }
    fun showLoading(msg: String) { job?.cancel(); message = msg; type = ToastType.LOADING; progress = true; visible = true }
    fun hide() { job?.cancel(); visible = false }
    fun showSuccess(msg: String) = show(msg, ToastType.SUCCESS)
    fun showError(msg: String) = show(msg, ToastType.ERROR, 3000)
    fun showWarning(msg: String) = show(msg, ToastType.WARNING)
    fun showInfo(msg: String) = show(msg, ToastType.INFO)
}

val GlobalToast = ToastState()

@Composable
fun ToastHost(toast: ToastState = GlobalToast, modifier: Modifier = Modifier) {
    if (!toast.visible) return
    val color = when (toast.type) { ToastType.SUCCESS -> Success; ToastType.ERROR -> Danger; ToastType.WARNING -> Warning; ToastType.INFO -> Accent; ToastType.LOADING -> Accent }
    val bg = color.copy(alpha = 0.12f)
    val icon = when (toast.type) { ToastType.SUCCESS -> "✓"; ToastType.ERROR -> "✕"; ToastType.WARNING -> "⚠"; ToastType.INFO -> "ⓘ"; ToastType.LOADING -> "" }
    Box(modifier = modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        Row(modifier = Modifier.background(bg).border(1.dp, color).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (toast.progress) { CircularProgressIndicator(modifier = Modifier.width(16.dp).height(16.dp), color = color, strokeWidth = 2.dp) }
            else { Text(text = icon, color = color, style = MaterialTheme.typography.titleMedium) }
            Text(text = toast.message, color = color, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun LoadingState(message: String = "加载中…", modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CircularProgressIndicator(modifier = Modifier.width(28.dp).height(28.dp), color = Accent, strokeWidth = 2.dp)
        Text(text = message, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ErrorState(message: String, onRetry: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "⚠", color = Warning, style = MaterialTheme.typography.headlineMedium)
        Text(text = message, color = Danger, style = MaterialTheme.typography.bodyMedium)
        if (onRetry != null) {
            Spacer(Modifier.height(4.dp))
            androidx.compose.material3.TextButton(onClick = onRetry, modifier = Modifier.border(1.dp, Accent)) {
                Text("重试", color = Accent, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
