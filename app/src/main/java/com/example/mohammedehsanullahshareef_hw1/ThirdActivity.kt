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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThirdActivity : AppCompatActivity() {

    private lateinit var categorySpinner: Spinner
    private lateinit var opacitySeekBar: SeekBar
    private lateinit var opacityLabel: TextView
    private lateinit var itemCount: TextView
    private lateinit var emptyState: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var doneBtn: Button
    private lateinit var rootView: View

    private lateinit var adapter: ClothingAdapter
    private val db by lazy { AppDatabase.getInstance(this).clothingDao() }

    private var allItems = listOf<ClothingItemEntity>()
    private var currentCategory = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rootView        = findViewById(R.id.main)
        categorySpinner = findViewById(R.id.categorySpinner)
        opacitySeekBar  = findViewById(R.id.opacitySeekBar)
        opacityLabel    = findViewById(R.id.opacityLabel)
        itemCount       = findViewById(R.id.itemCount)
        emptyState      = findViewById(R.id.emptyState)
        recyclerView    = findViewById(R.id.wardrobeRecycler)
        doneBtn         = findViewById(R.id.doneBtn)

        setupRecycler()
        setupSpinner()
        setupSeekBar()
        loadItems()
        setupDoneButton()
    }

    private fun setupRecycler() {
        adapter = ClothingAdapter(
            items   = emptyList(),
            baseUrl = Constants.BASE_URL.trimEnd('/'),
            onDelete = { item -> confirmDelete(item) }
        )
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    private fun setupSpinner() {
        val labels = resources.getStringArray(R.array.category_labels)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val categories = resources.getStringArray(R.array.categories)
                currentCategory = categories[pos]
                filterAndDisplay()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupSeekBar() {
        opacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                opacityLabel.text = "Opacity: $progress%"
                adapter.updateOpacity(progress / 100f)
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })
    }

    private fun loadItems() {
        lifecycleScope.launch {
            allItems = withContext(Dispatchers.IO) { db.getAll() }
            filterAndDisplay()
        }
    }

    private fun filterAndDisplay() {
        val filtered = if (currentCategory == "all") allItems
                       else allItems.filter { it.category == currentCategory }

        adapter.updateItems(filtered)
        itemCount.text = "${filtered.size} item${if (filtered.size != 1) "s" else ""}"
        emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun confirmDelete(item: ClothingItemEntity) {
        AlertDialog.Builder(this)
            .setTitle("Remove Item")
            .setMessage(getString(R.string.delete_confirm))
            .setPositiveButton("Remove") { _, _ -> deleteItem(item) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteItem(item: ClothingItemEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.deleteById(item.id)
                // also delete from backend (best effort)
                try { RetrofitClient.api.deleteItem(item.id) } catch (_: Exception) {}
            }
            allItems = allItems.filter { it.id != item.id }
            filterAndDisplay()
            Snackbar.make(rootView, "Item removed", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupDoneButton() {
        doneBtn.setOnClickListener {
            lifecycleScope.launch {
                val stats = buildCategoryStats()
                val resultIntent = Intent().apply {
                    putExtra(Constants.EXTRA_CATEGORY_STATS, stats)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private suspend fun buildCategoryStats(): CategoryStats {
        return withContext(Dispatchers.IO) {
            CategoryStats(
                topAvg      = db.avgRatingForCategory("top") ?: 0f,
                bottomAvg   = db.avgRatingForCategory("bottom") ?: 0f,
                shoesAvg    = db.avgRatingForCategory("shoes") ?: 0f,
                outerwearAvg= db.avgRatingForCategory("outerwear") ?: 0f,
                dressAvg    = db.avgRatingForCategory("dress") ?: 0f,
                accessoryAvg= db.avgRatingForCategory("accessory") ?: 0f,
                topCount    = db.countForCategory("top"),
                bottomCount = db.countForCategory("bottom"),
                shoesCount  = db.countForCategory("shoes")
            )
        }
    }
}
