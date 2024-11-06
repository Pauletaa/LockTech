package br.com.fabricasinapse.locktech

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Suporte : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ativa as bordas transparentes
        setContentView(R.layout.activity_suporte) // Define o layout da activity

        // Ajuste para adicionar padding aos system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referência ao botão do WhatsApp
        val btnWhatsApp: ImageButton = findViewById(R.id.btnWhatsApp)
        btnWhatsApp.setOnClickListener {
            val url = "https://wa.me/qr/CSYST7RHHJ3TO1"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        // Referência ao botão do Instagram
        val btnInstagram: ImageButton = findViewById(R.id.btnInstagram)
        btnInstagram.setOnClickListener {
            val url = "https://www.instagram.com/locktechrt/?utm_source=ig_web_button_share_sheet"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        // Referência ao botão do E-Mail
        val btnEmail: ImageButton = findViewById(R.id.btnEmail)
        btnEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:locktechrt@gmail.com")
            }
            startActivity(emailIntent)
        }

        // Referência ao botão de imagem (Facebook)
        val btnFacebook: ImageButton = findViewById(R.id.btnFacebook)
        btnFacebook.setOnClickListener {
            // Ação ao clicar no botão
            Toast.makeText(this, "Botão do Facebook clicado!", Toast.LENGTH_SHORT).show()
        }
    }
}
