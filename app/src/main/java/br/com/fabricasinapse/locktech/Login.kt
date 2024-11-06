package br.com.fabricasinapse.locktech

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.fabricasinapse.locktech.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var txtTelaCadastro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        iniciarComponentes()

        txtTelaCadastro.setOnClickListener {
            val intent = Intent(this@Login, Cadastro::class.java)
            startActivity(intent)
        }

        // Ajuste as margens do layout com base nos insets do sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configura o botão de login
        binding.btEntrar.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            when {
                email.isEmpty() -> {
                    binding.editEmail.error = "Preencha o E-mail!"
                }
                senha.isEmpty() -> {
                    binding.editSenha.error = "Preencha a Senha!"
                }
                !email.contains("@gmail.com") -> {
                    val snackbar = Snackbar.make(it, "E-mail inválido!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
                senha.length <= 5 -> {
                    val snackbar = Snackbar.make(it, "A senha precisa ter pelo menos 6 caracteres!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
                else -> {
                    login(it)
                }
            }
        }
    }

    // Função para realizar o login
    private fun login(view: View) {
        val progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE

        binding.btEntrar.isEnabled = false
        binding.btEntrar.setTextColor(Color.parseColor("#FFFFFF"))

        // Simula um atraso para o login
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
            navegarTelaPrincipal()
            val snackbar = Snackbar.make(view, "Login realizado com sucesso!", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }, 3000)
    }

    // Função para navegar para a tela principal após o login
    private fun navegarTelaPrincipal() {
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }

    private fun iniciarComponentes() {
        txtTelaCadastro = findViewById(R.id.txtTelaCadastro)
    }
}
