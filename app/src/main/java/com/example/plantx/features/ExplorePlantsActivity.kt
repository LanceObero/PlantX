package com.example.plantx.features

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantx.R

data class ExplorePlant(
    val commonName: String,
    val scientificName: String,
    val description: String
)

class ExplorePlantsActivity : AppCompatActivity() {

    private lateinit var adapter: ExplorePlantsAdapter

    private val plants = listOf(
        ExplorePlant(
            "Aloe Vera",
            "Aloe barbadensis miller",
            "A succulent plant commonly grown for its soothing gel and medicinal uses."
        ),
        ExplorePlant(
            "Snake Plant",
            "Dracaena trifasciata",
            "A hardy indoor plant known for its tall, upright leaves and low maintenance."
        ),
        ExplorePlant(
            "Peace Lily",
            "Spathiphyllum",
            "A popular indoor plant with dark green leaves and distinctive white flowers."
        ),
        ExplorePlant(
            "Sunflower",
            "Helianthus annuus",
            "A flowering plant known for its large yellow flower head and tall stem."
        ),
        ExplorePlant(
            "Monstera",
            "Monstera deliciosa",
            "A tropical plant recognized by its large leaves with natural splits and holes."
        ),
        ExplorePlant(
            "Rose",
            "Rosa",
            "A flowering plant widely known for its beautiful and fragrant flowers."
        ),
        ExplorePlant(
            "Lavender",
            "Lavandula",
            "An aromatic flowering plant commonly grown for its fragrance and purple flowers."
        ),
        ExplorePlant(
            "Basil",
            "Ocimum basilicum",
            "An aromatic herb commonly used in cooking and grown in warm climates."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_plants)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val searchView = findViewById<SearchView>(R.id.searchPlants)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerPlants)

        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // RecyclerView
        adapter = ExplorePlantsAdapter(plants)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        // Search
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(
                    query: String?
                ): Boolean {

                    filterPlants(query)

                    return true
                }

                override fun onQueryTextChange(
                    newText: String?
                ): Boolean {

                    filterPlants(newText)

                    return true
                }
            }
        )
    }

    private fun filterPlants(searchText: String?) {

        val query = searchText
            ?.trim()
            ?.lowercase()
            .orEmpty()

        // Show all plants when search is empty
        if (query.isEmpty()) {

            adapter.updatePlants(plants)

            return
        }

        // Search by:
        // 1. Common name
        // 2. Scientific name
        // 3. Description
        val filteredPlants = plants.filter { plant ->

            plant.commonName
                .lowercase()
                .contains(query) ||

                    plant.scientificName
                        .lowercase()
                        .contains(query) ||

                    plant.description
                        .lowercase()
                        .contains(query)
        }

        adapter.updatePlants(filteredPlants)
    }
}

