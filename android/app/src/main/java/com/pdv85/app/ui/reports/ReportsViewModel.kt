package com.pdv85.app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdv85.app.data.remote.model.ReportData
import com.pdv85.app.data.repository.ReportRepository
import com.pdv85.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsViewModel : ViewModel() {
    private val repo = ReportRepository()

    private val _report = MutableStateFlow<Result<ReportData>?>(null)
    val report: StateFlow<Result<ReportData>?> = _report

    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val today: String get() = fmt.format(Date())

    fun load(from: String, to: String) {
        viewModelScope.launch {
            _report.value = Result.Loading
            _report.value = repo.getReport(from, to)
        }
    }
}
