package com.realtor.geeksales.viewmodel

import android.content.ContentProviderOperation
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtor.geeksales.data.db.Customer
import com.realtor.geeksales.data.db.IntentLevel
import com.realtor.geeksales.data.importexport.CsvManager
import com.realtor.geeksales.data.importexport.ExcelManager
import com.realtor.geeksales.data.importexport.ImportReport
import com.realtor.geeksales.data.repo.CustomerRepository
import com.realtor.geeksales.util.Formatter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class IOStatus(
    val running: Boolean = false,
    val message: String = "",
    val report: ImportReport? = null,
    val exportCount: Int? = null
)

@HiltViewModel
class ImportExportViewModel @Inject constructor(
    private val csv: CsvManager,
    private val excel: ExcelManager,
    private val repo: CustomerRepository,
    @ApplicationContext private val ctx: Context
) : ViewModel() {

    private val _status = MutableStateFlow(IOStatus(message = "idle"))
    val status: StateFlow<IOStatus> = _status

    fun importCsv(uri: Uri) = doRun("CSV 导入中…") {
        val r = csv.importFrom(uri)
        _status.value = IOStatus(message = "CSV 导入完成", report = r)
    }
    fun importXlsx(uri: Uri) = doRun("Excel 导入中…") {
        val r = excel.importFrom(uri)
        _status.value = IOStatus(message = "Excel 导入完成", report = r)
    }
    fun exportCsv(uri: Uri) = doRun("CSV 导出中…") {
        val n = csv.exportTo(uri)
        _status.value = IOStatus(message = "CSV 导出完成", exportCount = n)
    }
    fun exportXlsx(uri: Uri) = doRun("Excel 导出中…") {
        val n = excel.exportTo(uri)
        _status.value = IOStatus(message = "Excel 导出完成", exportCount = n)
    }
    fun templateXlsx(uri: Uri) = doRun("生成模板中…") {
        excel.templateTo(uri)
        _status.value = IOStatus(message = "模板已生成")
    }

    fun importContacts() = doRun("通讯录导入中…") {
        withContext(Dispatchers.IO) {
            val contacts = mutableListOf<Customer>()
            val resolver = ctx.contentResolver
            val cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.TYPE
                ),
                null, null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )
            cursor?.use { c ->
                val seen = mutableSetOf<String>()
                while (c.moveToNext()) {
                    val name = c.getString(0) ?: continue
                    val rawPhone = c.getString(1) ?: continue
                    val normalized = Formatter.normalizePhone(rawPhone) ?: continue
                    if (!Formatter.isValidCnPhone(normalized)) continue
                    if (seen.contains(normalized)) continue
                    seen.add(normalized)
                    contacts.add(
                        Customer(
                            name = name.trim(),
                            phone = rawPhone.trim(),
                            phoneNormalized = normalized,
                            source = "通讯录导入",
                            intentLevel = IntentLevel.U
                        )
                    )
                }
            }
            if (contacts.isEmpty()) {
                _status.value = IOStatus(message = "通讯录无有效联系人")
                return@withContext
            }
            var dup = 0
            val newOnes = contacts.filter { c ->
                val existing = repo.getByPhoneNormalized(c.phoneNormalized)
                if (existing != null) { dup++; false } else true
            }
            if (newOnes.isNotEmpty()) repo.upsertAll(newOnes)
            _status.value = IOStatus(
                message = "通讯录导入完成",
                report = ImportReport(
                    total = contacts.size,
                    success = newOnes.size,
                    duplicated = dup,
                    invalid = contacts.size - newOnes.size - dup
                )
            )
        }
    }

    fun exportContacts() = doRun("导出到通讯录中…") {
        withContext(Dispatchers.IO) {
            val all = repo.getAll()
            if (all.isEmpty()) {
                _status.value = IOStatus(message = "无客户可导出")
                return@withContext
            }
            val ops = ArrayList<ContentProviderOperation>()
            all.forEach { c ->
                val rowId = ops.size
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .withValue(ContactsContract.RawContacts.STARRED, 0)
                        .build()
                )
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rowId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, c.name)
                        .build()
                )
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rowId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, c.phone)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build()
                )
            }
            ctx.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            _status.value = IOStatus(
                message = "导出到通讯录完成",
                exportCount = all.size
            )
        }
    }

    fun clearAll() = doRun("清空数据中…") {
        withContext(Dispatchers.IO) {
            val n = repo.countAll()
            repo.deleteAll()
            _status.value = IOStatus(message = "已清空 $n 条客户数据")
        }
    }

    private fun doRun(progressMsg: String, block: suspend () -> Unit) {
        _status.value = IOStatus(running = true, message = progressMsg)
        viewModelScope.launch {
            runCatching { block() }
                .onFailure { t ->
                    _status.value = IOStatus(message = "失败：${t.message ?: t.javaClass.simpleName}")
                }
        }
    }
}