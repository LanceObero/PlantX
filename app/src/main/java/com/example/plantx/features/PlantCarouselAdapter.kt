package com.example.plantx.features

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantx.R

data class CarouselPlant(
    val name: String,
    val scientificName: String,
    val description: String,
    val imageResId: Int
)

class PlantCarouselAdapter(
    plants: List<CarouselPlant>,
    private val onPlantClick: (CarouselPlant) -> Unit
) : RecyclerView.Adapter<PlantCarouselAdapter.PlantViewHolder>() {

    private val plantList =
        plants.toMutableList()

    class PlantViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView =
            itemView.findViewById(R.id.imgPlant)

        val name: TextView =
            itemView.findViewById(R.id.tvPlantName)

        val scientificName: TextView =
            itemView.findViewById(R.id.tvScientificName)

        val description: TextView =
            itemView.findViewById(R.id.tvPlantDescription)

        val viewPlant: TextView =
            itemView.findViewById(R.id.tvViewPlant)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlantViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_plant_carousel,
                    parent,
                    false
                )

        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlantViewHolder,
        position: Int
    ) {

        val plant =
            plantList[position]

        holder.image.setImageResource(
            plant.imageResId
        )

        holder.name.text =
            plant.name

        holder.scientificName.text =
            plant.scientificName

        holder.description.text =
            plant.description

        holder.itemView.setOnClickListener {

            onPlantClick(plant)
        }

        holder.viewPlant.setOnClickListener {

            onPlantClick(plant)
        }
    }

    override fun getItemCount(): Int {

        return plantList.size
    }

    // =========================================
    // UPDATE PLANT LIST
    // =========================================

    fun updatePlants(
        newPlants: List<CarouselPlant>
    ) {

        plantList.clear()

        plantList.addAll(newPlants)

        notifyDataSetChanged()
    }
}