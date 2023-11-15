package com.example.recipebook

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RecipeView {
     fun getRecipes(){
        try {
            val database = Firebase.database("//database link")
            val recipeRef = database.reference.child("dishes")

            recipeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Iterate through the child nodes and retrieve their names
                    for (childSnapshot in dataSnapshot.children) {
                        val recipeName = childSnapshot.key
                        Log.d("HELP", recipeName!!)

                        val image = childSnapshot.child("image").value

                        Log.d("HELP IMAGE", image.toString())
                    }
                    Log.d("HELP COUNT", dataSnapshot.childrenCount.toString())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Error", "onCancelled", databaseError.toException())
                }
            })
        } catch (e: Exception) {
            Log.e("error", "Something went wrong...", e)
        }
    }
}
