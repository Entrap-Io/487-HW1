package com.example.mohammedehsanullahshareef_hw1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FourthActivity : AppCompatActivity() {

    private lateinit var queryInput: EditText
    private lateinit var generateBtn: Button
    private lateinit var loadingLayout: LinearLayout
    private lateinit var outfitResultName: TextView
    private lateinit var outfitResultReasoning: TextView
    private lateinit var outfitRecycler: RecyclerView
    private lateinit var sendToHomeBtn: Button
    private lateinit var rootView: View

    private lateinit var outfitAdapter: OutfitItemAdapter
    private var currentOutfit: Outfit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rootView              = findViewById(R.id.main)
        queryInput            = findViewById(R.id.queryInput)
        generateBtn           = findViewById(R.id.generateBtn)
        loadingLayout         = findViewById(R.id.loadingLayout)
        outfitResultName      = findViewById(R.id.outfitResultName)
        outfitResultReasoning = findViewById(R.id.outfitResultReasoning)
        outfitRecycler        = findViewById(R.id.outfitItemsRecycler)
        sendToHomeBtn         = findViewById(R.id.sendToHomeBtn)

        setupRecycler()
        setupButtons()
    }

    private fun setupRecycler() {
        outfitAdapter = OutfitItemAdapter(emptyList(), Constants.BASE_URL.trimEnd('/'))
        outfitRecycler.layoutManager = LinearLayoutManager(this)
        outfitRecycler.adapter = outfitAdapter
    }

    private fun setupButtons() {
        generateBtn.setOnClickListener {
            val query = queryInput.text.toString().trim()
            if (query.isEmpty()) {
                Toast.makeText(this, "Enter a style or occasion first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showConfirmDialog(query)
        }

        sendToHomeBtn.setOnClickListener {
            val outfit = currentOutfit ?: return@setOnClickListener
            val resultIntent = Intent().apply {
                putExtra(Constants.EXTRA_OUTFIT_NAME, outfit.name ?: "Today's Outfit")
                putExtra(Constants.EXTRA_OUTFIT_REASONING, outfit.reasoning ?: "")
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun showConfirmDialog(query: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_generate))
            .setMessage(getString(R.string.confirm_generate_msg))
            .setPositiveButton("Generate") { _, _ -> generateOutfit(query) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun generateOutfit(query: String) {
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.searchOutfit(SearchRequest(query))
                }

                setLoading(false)

                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    val outfit = body.outfit

                    if (outfit == null || outfit.items.isEmpty()) {
                        Snackbar.make(
                            rootView,
                            body.message ?: "No outfit found. Add more items to your closet!",
                            Snackbar.LENGTH_LONG
                        ).show()
                        return@launch
                    }

                    currentOutfit = outfit
                    displayOutfit(outfit)

                } else {
                    Toast.makeText(
                        this@FourthActivity,
                        "Error: ${response.errorBody()?.string()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                setLoading(false)
                AlertDialog.Builder(this@FourthActivity)
                    .setTitle("Connection Error")
                    .setMessage("Could not reach backend.\n\n${e.message}\n\nMake sure your server is running on port 3001.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }

    private fun displayOutfit(outfit: Outfit) {
        outfitResultName.text = outfit.name ?: "Generated Outfit"
        outfitResultName.visibility = View.VISIBLE

        outfitResultReasoning.text = outfit.reasoning ?: ""
        outfitResultReasoning.visibility = View.VISIBLE

        outfitAdapter.updateItems(outfit.items)
        sendToHomeBtn.visibility = View.VISIBLE

        Snackbar.make(rootView, "✨ Outfit generated!", Snackbar.LENGTH_SHORT).show()
    }

    private fun setLoading(loading: Boolean) {
        loadingLayout.visibility = if (loading) View.VISIBLE else View.GONE
        generateBtn.isEnabled    = !loading
        queryInput.isEnabled     = !loading
    }
}
