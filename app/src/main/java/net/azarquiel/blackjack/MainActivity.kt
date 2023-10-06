package net.azarquiel.blackjack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import net.azarquiel.blackjack.model.Carta
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var ivMazo: ImageView
    private lateinit var originalClickListener: View.OnClickListener
    private lateinit var tvPuntos: TextView
    private lateinit var random: Random
    private lateinit var llCartas: LinearLayout
    private val mazo = Array(40) { _ -> Carta() }
    private val palos = arrayOf("clubs", "diamonds", "hearts", "spades")
    private var posMazo = 0
    private var puntos = Array(2) {0}
    private var jugador = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        llCartas = findViewById(R.id.llCartas)
        ivMazo = findViewById<ImageView>(R.id.ivMazo)
        val btnStop = findViewById<Button>(R.id.btnStop)
        tvPuntos = findViewById(R.id.tvPuntos)

        originalClickListener = View.OnClickListener { ivMazoOnClick() }
        ivMazo.setOnClickListener(originalClickListener)
        btnStop.setOnClickListener { btnStopOnClick() }

        random = Random(System.currentTimeMillis())
        createMazo()
        newGame()
    }

    private fun createMazo() {
        for (j in 0 ..< 4) {
            for (i in 0..< 10) {
                mazo[10*j + i] = Carta(i+1, palos[j])
            }
        }
        mazo.shuffle(random)
    }

    private val handler = Handler(Looper.getMainLooper()) // Use the main looper

    private fun newGame() {
        llCartas.removeAllViews()

        if (jugador == 1) {
            // Remove the click listener temporarily
            ivMazo.setOnClickListener(null)
            tvPuntos.text = "Puntos: ${puntos[jugador]}"
            handler.postDelayed({

                // Code to execute after the delay (5 seconds in this case)
                sacaCarta()
                sacaCarta()
                // Add back the original click listener
                ivMazo.setOnClickListener(originalClickListener)
            }, 3500) // 5000 milliseconds (5 seconds)
        } else {
            sacaCarta()
            sacaCarta()
        }
    }

    private fun btnStopOnClick() {
        if(jugador == 0) {
            jugador = 1
            newGame()
        } else {
            finishGame()
        }
    }

    private fun finishGame() {
        var mensajeFinal = quienGana()
        mensajeFinal += "\nPuntos del jugador 1: ${puntos[0]}"
        mensajeFinal += "\nPuntos del jugador 2: ${puntos[1]}"

        AlertDialog.Builder(this)
        .setTitle("Partida completada")
        .setMessage(mensajeFinal)
        .setPositiveButton("Jugar de nuevo") { _, _ ->
            restartGame()
        }
        .setNegativeButton("Salir") { _, _ ->
            finish()
        }
        .show()
    }

    private fun quienGana(): String {
        return if (puntos[0] > 21 && puntos [1] > 21 || puntos[0] == puntos[1]) {
            "EMPATE"
        } else {
            if (puntos[0] > 21 || puntos[1] > puntos[0]) {
                "GANA EL JUGADOR 2"
            } else {
                "GANA EL JUGADOR 1"
            }
        }
    }

    private fun restartGame() {
        mazo.shuffle(random)
        jugador = 0
        puntos = Array(2) {0}
        newGame()
    }

    private fun ivMazoOnClick() {
        sacaCarta()
    }

    private fun sacaCarta() {
        if(puntos[jugador] > 21) {
            tostadaDerrota()
            btnStopOnClick()
        } else {
            val cartaJuego = mazo[posMazo]
            posMazo++
            val ivCarta = ImageView(this)

            val idImagen = resources.getIdentifier("${cartaJuego.palo}${cartaJuego.numero}", "drawable", packageName)
            ivCarta.setImageResource(idImagen)

            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, 0, 0, 0)
            ivCarta.layoutParams = lp

            llCartas.addView(ivCarta, 0)

            val valorCarta = if (cartaJuego.numero > 7) 10 else cartaJuego.numero
            puntos[jugador]+= valorCarta
            tvPuntos.text = "Puntos: ${puntos[jugador]}"
        }
    }

    private fun tostadaDerrota() {
        Toast.makeText(
            this,
            "Has perdido",
            Toast.LENGTH_LONG
        ).show()
    }
}