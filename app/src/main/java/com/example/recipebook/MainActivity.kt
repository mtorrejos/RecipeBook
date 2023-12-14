package com.example.recipebook

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

open class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var dishList : ArrayList<Dish>
    val database = Firebase.database(DatabaseConnect().connection)
    val recipeView = RecipeView()
    val dishRef = database.getReference("dishes")
    var childCount: Long = 0
    var nameList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        dishList = arrayListOf()

        findViewById<Button>(R.id.btnMove).setOnClickListener(){
            val intent = Intent(this, DishCreate::class.java)
            startActivity(intent)
        }

        dishRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) { //dataSnapshot is pointing to main 'dishes', children are each indiv dish
               // var numberOfChildren = dataSnapshot.childrenCount

               // setCount(numberOfChildren)

                for (childNode in dataSnapshot.children) {
                    var childIngredientList: ArrayList<Ingredient> = arrayListOf()
                    var ingredientsNode = childNode.child("ingredients")

                    for (ingredientNode in ingredientsNode.children) { //for loop for ingredients
                        var childIngredient = Ingredient(ingredientNode.value.toString())
                        childIngredientList.add(childIngredient)
                    }

                    if(childNode.child("hidden").value.toString().toBoolean())
                        continue

                    else {
                   dishList.add(Dish(
                       childNode.child("name").value.toString(),
                       childNode.child("recipe").value.toString(),
                       childIngredientList,
                       childNode.child("hidden").value.toString().toBoolean()
                   ))
                    recyclerView.adapter = AdapterClass(dishList) }
                }

            }
            override fun onCancelled(databaseError: DatabaseError) { Log.e("Error:", databaseError.message) }
        })

    }

}



