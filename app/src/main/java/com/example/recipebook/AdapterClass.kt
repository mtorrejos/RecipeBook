package com.example.recipebook

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AdapterClass(var dishList : ArrayList<Dish>) : RecyclerView.Adapter<AdapterClass.ViewHolderClass>(){

    class ViewHolderClass(var recipeView: View) : RecyclerView.ViewHolder(recipeView) {
        val title: TextView = recipeView.findViewById(R.id.recyclerTitle)
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
            intent.putExtra("name", currentItems.name)
            holder.itemView.context.startActivity(intent)
        }



        editButton.setOnClickListener() {
            val edit = Intent(holder.itemView.context, DishCreate::class.java)
            edit.putExtra("name", currentItems.name)
            edit.putExtra("editing", true)
            holder.itemView.context.startActivity(edit)
        }

        deleteButton.setOnClickListener() {
            val delete = Intent(holder.itemView.context, MainActivity::class.java)
            val name = currentItems.name
            val database = Firebase.database(DatabaseConnect().connection)
            val dishRef = database.getReference("dishes")

            getNameRef(dishRef, name) { resultRef ->
           resultRef.child("hidden").setValue(true)
            //dishRef.child(id).removeValue()
            holder.itemView.context.startActivity(delete) }
        }

    }

    override fun getItemCount(): Int {
        return dishList.size
    }

    private fun getNameRef(dishRef: DatabaseReference, name: String, callback: (DatabaseReference) -> Unit) {
        dishRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(namesnapshot: DataSnapshot) {
                for (child in namesnapshot.children) {
                    if (child.child("name").value.toString() == name) {
                        val nameRef = child.ref
                        Log.e("changedchildRef", nameRef.toString())
                        Log.e("dishRef", dishRef.toString())
                        Log.e("name", name)
                        Log.e("childRefChild", child.child("name").value.toString())
                        callback(nameRef)
                        return
                    }
                }

                callback(dishRef)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return
    }
}