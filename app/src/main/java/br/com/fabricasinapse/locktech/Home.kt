package br.com.fabricasinapse.locktech

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.fabricasinapse.locktech.databinding.ActivityHomeBinding

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardViewConta.setOnClickListener {
            val intent = Intent(this, Conta::class.java)
            startActivity(intent)
        }

        binding.cardViewDesbloqueio.setOnClickListener {
            val intent = Intent(this, Desbloqueio::class.java)  // Altere para a classe correta
            startActivity(intent)
        }

        binding.cardViewConectar.setOnClickListener {
            val intent = Intent(this, ConectarActivity::class.java)  // Altere para a classe correta
            startActivity(intent)
        }


        binding.cardViewSuporte.setOnClickListener {
            val intent = Intent(this, Suporte::class.java)
            startActivity(intent)
        }
    }
}


