package com.example.mohammedehsanullahshareef_hw1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvCaption: TextView
    private lateinit var tvOutfitName: TextView
    private lateinit var tvOutfitReasoning: TextView
    private lateinit var spinner: Spinner
    private lateinit var selBtn: Button

    // Launcher for OutfitGenerator → returns outfit of the day
    private val outfitLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val name = result.data?.getStringExtra(Constants.EXTRA_OUTFIT_NAME) ?: return@registerForActivityResult
            val reasoning = result.data?.getStringExtra(Constants.EXTRA_OUTFIT_REASONING) ?: ""
            tvOutfitName.text = name
            tvOutfitReasoning.text = reasoning
            tvOutfitReasoning.visibility = android.view.View.VISIBLE
            Toast.makeText(this, "✅ Outfit of the day set!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvTitle        = findViewById(R.id.dressCode)
        tvCaption      = findViewById(R.id.dressCodeCaption)
        tvOutfitName   = findViewById(R.id.outfitName)
        tvOutfitReasoning = findViewById(R.id.outfitReasoning)
        spinner        = findViewById(R.id.mainMenu)
        selBtn         = findViewById(R.id.selBtn)

        startBlinkAnimation()
        setupSpinner()
        setupNavigation()
    }

    private fun startBlinkAnimation() {
        val blink = AnimationUtils.loadAnimation(this, R.anim.blink)
        tvTitle.startAnimation(blink)
    }

    private fun setupSpinner() {
        val entries = resources.getStringArray(R.array.entries)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, entries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupNavigation() {
        selBtn.setOnClickListener {
            when (spinner.selectedItemPosition) {
                0 -> startActivity(Intent(this, SecondActivity::class.java))
                1 -> startActivity(Intent(this, ThirdActivity::class.java))
                2 -> outfitLauncher.launch(Intent(this, FourthActivity::class.java))
                else -> Toast.makeText(this, "Select a destination", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
