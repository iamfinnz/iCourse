package com.example.icourse.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.icourse.databinding.ItemMaterialBinding
import com.example.icourse.model.Material

class MaterialsAdapter : RecyclerView.Adapter<MaterialsAdapter.ViewHolder>(), Filterable {

    private var listener: ((Material, Int) -> Unit?)? = null
    var materials = mutableListOf<Material>()
        set(value) {
            field = value
            materialsFilter = value
            notifyDataSetChanged()
        }

    private var materialsFilter = mutableListOf<Material>()

    private val filters = object : Filter(){
        override fun performFiltering(p0: CharSequence?): FilterResults {
            var filteredList = mutableListOf<Material>()
            val filterPattern = p0.toString().trim().lowercase()

            if (filterPattern.isEmpty()){
                filteredList = materials
            } else {
                for (material in materials){
                    val title = material.titleMaterial?.trim()?.lowercase()

                    if(title?.contains(filterPattern) == true){
                        filteredList.add(material)
                    }
                }
            }

            val p1 = FilterResults()
            p1.values = filteredList
            return p1
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            materialsFilter = p1?.values as MutableList<Material>
            notifyDataSetChanged()
        }
    }

    class ViewHolder(private val materialBinding: ItemMaterialBinding) :
        RecyclerView.ViewHolder(materialBinding.root) {
        fun bindItem(material: Material, listener: ((Material, Int) -> Unit?)?) {
            Glide.with(itemView)
                .load(material.thumbnailMaterial)
                .placeholder(android.R.color.darker_gray)
                .into(materialBinding.ivMaterial)

            materialBinding.tvTitleMaterial.text = material.titleMaterial

            listener?.let {
                itemView.setOnClickListener {
                    it(material, adapterPosition)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(materialsFilter[position], listener)
    }

    override fun getItemCount(): Int = materialsFilter.size

    override fun getFilter(): Filter = filters

    fun onClick(listener: ((Material, Int) -> Unit?)){
        this.listener = listener
    }
}