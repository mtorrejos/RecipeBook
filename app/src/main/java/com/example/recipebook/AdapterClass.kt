package com.example.recipebook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterClass(var dishList : ArrayList<Dish>) : RecyclerView.Adapter<AdapterClass.ViewHolderClass>(){

    class ViewHolderClass(var recipeView: View) : RecyclerView.ViewHolder(recipeView) {
        val title: TextView = recipeView.findViewById(R.id.recyclerTitle)
        val image: ImageView = recipeView.findViewById(R.id.recyclerImage)
        val delete: Button = recipeView.findViewById(R.id.btnDelete)
        val edit: Button = recipeView.findViewById(R.id.btnEdit)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterClass.ViewHolderClass {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.recipes_list, parent, false)
        return ViewHolderClass(itemView)
    }


    override fun onBindViewHolder(holder: AdapterClass.ViewHolderClass, position: Int) {
        var currentItems = dishList[position]
        holder.title.text = currentItems.name
    }

    override fun getItemCount(): Int {
        return dishList.size
    }
}