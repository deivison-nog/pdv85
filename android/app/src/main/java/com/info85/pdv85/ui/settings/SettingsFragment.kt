package com.info85.pdv85.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.info85.pdv85.data.remote.model.SettingsRequest
import com.info85.pdv85.databinding.FragmentSettingsBinding
import com.info85.pdv85.util.Result
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _b: FragmentSettingsBinding? = null
    private val b get() = _b!!
    private val vm: SettingsViewModel by viewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentSettingsBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val themes = listOf("dark", "light")
        b.spinnerTheme.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, themes)

        b.btnSave.setOnClickListener { save() }
        b.btnReload.setOnClickListener { vm.load() }

        lifecycleScope.launch {
            vm.settings.collect { state ->
                b.progressBar.visibility = if (state is Result.Loading) View.VISIBLE else View.GONE
                when (state) {
                    is Result.Success -> {
                        val s = state.data
                        b.etCompanyName.setText(s.companyName)
                        b.etCnpj.setText(s.companyCnpj)
                        b.etCouponWidth.setText(s.couponWidthMm)
                        b.etCouponCopies.setText(s.couponCopies)
                        b.switchAutoPrint.isChecked = s.couponAutoPrint == "1"
                        b.spinnerTheme.setSelection(themes.indexOf(s.theme).coerceAtLeast(0))
                        b.tvError.visibility = View.GONE
                    }
                    is Result.Error -> {
                        b.tvError.text = state.message; b.tvError.visibility = View.VISIBLE
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            vm.saveResult.collect { result ->
                when (result) {
                    is Result.Success -> {
                        AlertDialog.Builder(requireContext())
                            .setMessage("Configurações salvas!").setPositiveButton("OK", null).show()
                        vm.clearSaveResult()
                    }
                    is Result.Error -> {
                        AlertDialog.Builder(requireContext())
                            .setMessage(result.message).setPositiveButton("OK", null).show()
                        vm.clearSaveResult()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun save() {
        val req = SettingsRequest(
            companyName = b.etCompanyName.text.toString().trim(),
            companyCnpj = b.etCnpj.text.toString().trim(),
            couponWidthMm = b.etCouponWidth.text.toString().trim().ifEmpty { "58" },
            couponCopies = b.etCouponCopies.text.toString().trim().ifEmpty { "2" },
            couponAutoPrint = if (b.switchAutoPrint.isChecked) "1" else "0",
            theme = b.spinnerTheme.selectedItem.toString()
        )
        vm.save(req)
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
