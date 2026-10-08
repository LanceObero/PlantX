package com.example.plantx.features

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantx.R

class ExplorePlantsAdapter(
    private var plants: List<ExplorePlant>
) : RecyclerView.Adapter<ExplorePlantsAdapter.PlantViewHolder>() {

    class PlantViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val commonName: TextView =
            itemView.findViewById(R.id.tvCommonName)

        val scientificName: TextView =
            itemView.findViewById(R.id.tvScientificName)

        val description: TextView =
            itemView.findViewById(R.id.tvPlantDescription)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlantViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_explore_plant,
                parent,
                false
            )

        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlantViewHolder,
        position: Int
    ) {

        val plant = plants[position]

        holder.commonName.text = plant.commonName
        holder.scientificName.text = plant.scientificName
        holder.description.text = plant.description
    }

    override fun getItemCount(): Int {
        return plants.size
    }

    fun updatePlants(newPlants: List<ExplorePlant>) {
        plants = newPlants
        notifyDataSetChanged()
    }
}