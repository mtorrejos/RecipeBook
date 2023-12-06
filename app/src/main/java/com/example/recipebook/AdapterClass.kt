package com.example.recipebook

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AdapterClass(var dishList : ArrayList<Dish>) : RecyclerView.Adapter<AdapterClass.ViewHolderClass>(){

    class ViewHolderClass(var recipeView: View) : RecyclerView.ViewHolder(recipeView) {
        val title: TextView = recipeView.findViewById(R.id.recyclerTitle)
        val image: ImageView = recipeView.findViewById(R.id.recyclerImage)
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
        var editButton = holder.itemView.findViewById<Button>(R.id.btnEdit)
        var deleteButton = holder.itemView.findViewById<Button>(R.id.btnDelete)
        holder.title.text = currentItems.name

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, RecipeView::class.java)
            intent.putExtra("id", "dishID: $position")
            holder.itemView.context.startActivity(intent)
        }



        editButton.setOnClickListener() {
            val edit = Intent(holder.itemView.context, DishCreate::class.java)
            edit.putExtra("id", "dishID: $position")
            holder.itemView.context.startActivity(edit)
        }

        deleteButton.setOnClickListener() {
            val delete = Intent(holder.itemView.context, MainActivity::class.java)
            val id = "dishID: $position"
            val database = Firebase.database(DatabaseConnect().connection)
            val dishRef = database.getReference("dishes")

            dishRef.child(id).removeValue()

            holder.itemView.context.startActivity(delete)
        }

    }

    override fun getItemCount(): Int {
        return dishList.size
    }


    private fun deleteItem(string: String) {

    }
}