package com.example.recipebook

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RecipeView() {

    fun getRecipes(context: Context, recyclerView: RecyclerView) {
        try {
            //lateinit var ingredients: ArrayList<Ingredient>
            lateinit var dishList: ArrayList<Dish>
            val database = Firebase.database(/*link to database*/)
            var myRef = database.getReference("message")

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dishList = arrayListOf()

                    for (recipe in dataSnapshot.children) {
                        val dish: Dish? = dataSnapshot.getValue(Dish::class.java)
                        dish?.let { dishList.add(it) }
                    }
                    recyclerView.adapter = AdapterClass(dishList)

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("error", "Failed to read value.", error.toException())
                }
            })




        } catch (e: Exception) {
            Log.e("error", "Something went wrong...")
        }
    }
}