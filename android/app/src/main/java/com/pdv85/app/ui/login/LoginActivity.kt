package com.pdv85.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pdv85.app.databinding.ActivityLoginBinding
import com.pdv85.app.ui.main.MainActivity
import com.pdv85.app.util.Result
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding
    private val vm: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener {
            vm.login(
                b.etUsername.text.toString().trim(),
                b.etPassword.text.toString()
            )
        }

        lifecycleScope.launch {
            vm.state.collect { state ->
                when (state) {
                    is Result.Loading -> setLoading(true)
                    is Result.Success -> {
                        setLoading(false)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                    is Result.Error -> {
                        setLoading(false)
                        b.tvError.text = state.message
                        b.tvError.visibility = View.VISIBLE
                    }
                    null -> Unit
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        b.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        b.btnLogin.isEnabled = !loading
        b.tvError.visibility = View.GONE
    }
}
