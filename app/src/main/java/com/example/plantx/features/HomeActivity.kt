package com.example.plantx.features

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantx.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var plantCarousel: RecyclerView
    private lateinit var plantIndicator: LinearLayout
    private lateinit var searchEditText: EditText
    private lateinit var clearSearch: TextView
    private lateinit var plantAdapter: PlantCarouselAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val plants = listOf(
        CarouselPlant(
            name = "Peace Lily",
            scientificName = "Spathiphyllum",
            description = "A beautiful indoor plant known for its elegant white flowers.",
            imageResId = R.drawable.peace_lily
        ),
        CarouselPlant(
            name = "Aloe Vera",
            scientificName = "Aloe barbadensis miller",
            description = "A succulent plant known for its soothing gel and easy care.",
            imageResId = R.drawable.aloe_vera
        ),
        CarouselPlant(
            name = "Snake Plant",
            scientificName = "Dracaena trifasciata",
            description = "A hardy indoor plant with tall upright leaves.",
            imageResId = R.drawable.snake_plant
        ),
        CarouselPlant(
            name = "Monstera",
            scientificName = "Monstera deliciosa",
            description = "A tropical plant recognized by its large split leaves.",
            imageResId = R.drawable.monstera
        ),
        CarouselPlant(
            name = "Sunflower",
            scientificName = "Helianthus annuus",
            description = "A bright flowering plant known for its large yellow flower.",
            imageResId = R.drawable.sunflower
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val welcomeText = findViewById<TextView>(R.id.tvWelcome)

        val preferences = getSharedPreferences(
            "PlantXPrefs",
            MODE_PRIVATE
        )

        val fullName = preferences.getString(
            "full_name",
            "PlantX User"
        )

        welcomeText.text = "Welcome, $fullName!"

        searchEditText = findViewById(R.id.etSearchPlants)
        clearSearch = findViewById(R.id.tvClearSearch)

        plantCarousel = findViewById(R.id.plantCarousel)
        plantIndicator = findViewById(R.id.plantIndicator)

        layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        plantCarousel.layoutManager = layoutManager

        plantAdapter = PlantCarouselAdapter(plants) {
            startActivity(
                Intent(
                    this,
                    ExplorePlantsActivity::class.java
                )
            )
        }

        plantCarousel.adapter = plantAdapter

        val seeAllPlants = findViewById<TextView>(
            R.id.tvSeeAllPlants
        )

        seeAllPlants.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ExplorePlantsActivity::class.java
                )
            )
        }

        val identifyPlant = findViewById<View>(
            R.id.cardIdentifyPlant
        )

        identifyPlant.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    IdentifyActivity::class.java
                )
            )
        }

        plantCarousel.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(
                        recyclerView,
                        dx,
                        dy
                    )

                    if (plantAdapter.itemCount == 0) {
                        return
                    }

                    val centerX = recyclerView.width / 2

                    var closestPosition =
                        RecyclerView.NO_POSITION

                    var closestDistance = Int.MAX_VALUE

                    for (i in 0 until recyclerView.childCount) {

                        val child =
                            recyclerView.getChildAt(i)

                        val childCenterX =
                            (child.left + child.right) / 2

                        val distance =
                            kotlin.math.abs(
                                childCenterX - centerX
                            )

                        if (distance < closestDistance) {

                            closestDistance = distance

                            closestPosition =
                                recyclerView
                                    .getChildAdapterPosition(child)
                        }
                    }

                    if (
                        closestPosition !=
                        RecyclerView.NO_POSITION
                    ) {
                        updateIndicator(closestPosition)
                    }
                }
            }
        )

        updateIndicator(0)

        searchEditText.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val query = s
                        ?.toString()
                        ?.trim()
                        ?.lowercase()
                        .orEmpty()

                    clearSearch.visibility =
                        if (query.isEmpty()) {
                            View.GONE
                        } else {
                            View.VISIBLE
                        }

                    if (query.isEmpty()) {

                        plantAdapter.updatePlants(plants)

                        plantCarousel.post {
                            layoutManager.scrollToPosition(0)
                        }

                        plantIndicator.visibility =
                            View.VISIBLE

                        updateIndicator(0)

                        return
                    }

                    val filteredPlants =
                        plants.filter { plant ->

                            plant.name
                                .lowercase()
                                .contains(query) ||

                                    plant.scientificName
                                        .lowercase()
                                        .contains(query) ||

                                    plant.description
                                        .lowercase()
                                        .contains(query)
                        }

                    if (filteredPlants.isNotEmpty()) {

                        plantAdapter.updatePlants(
                            filteredPlants
                        )

                        plantCarousel.post {
                            layoutManager.scrollToPosition(0)
                        }

                        plantIndicator.visibility =
                            View.VISIBLE

                        updateIndicator(0)

                    } else {

                        plantAdapter.updatePlants(
                            emptyList()
                        )

                        plantIndicator.visibility =
                            View.GONE
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        clearSearch.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.requestFocus()
        }

        val bottomNavigation =
            findViewById<BottomNavigationView>(
                R.id.bottomNavigation
            )

        bottomNavigation.selectedItemId =
            R.id.nav_home

        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    true
                }

                R.id.nav_history -> {
                    startActivity(
                        Intent(
                            this,
                            HistoryActivity::class.java
                        )
                    )
                    true
                }

                R.id.nav_profile -> {
                    startActivity(
                        Intent(
                            this,
                            ProfileActivity::class.java
                        )
                    )
                    true
                }

                else -> false
            }
        }
    }

    private fun updateIndicator(
        selectedPosition: Int
    ) {

        if (selectedPosition < 0) {
            return
        }

        for (i in 0 until plantIndicator.childCount) {

            val dot =
                plantIndicator.getChildAt(i)

            if (i == selectedPosition) {

                dot.setBackgroundResource(
                    R.drawable.indicator_active
                )

            } else {

                dot.setBackgroundResource(
                    R.drawable.indicator_inactive
                )
            }
        }
    }
}

