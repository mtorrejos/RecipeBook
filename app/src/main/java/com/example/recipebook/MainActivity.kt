package com.example.recipebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

open class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    //lateinit var ingredients: ArrayList<Ingredient>
    lateinit var dishList : ArrayList<Dish>
    val database = Firebase.database("https://recipe-book-4adb9-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val recipeView = RecipeView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        recipeView.getRecipes(this,recyclerView)
        uploadData()
    }

    fun uploadData() {
        var ingredients : ArrayList<Ingredient> = arrayListOf()
        ingredients.add(Ingredient("potato", 5, "grams"))
        ingredients.add(Ingredient("tomato", 20, "grams"))
        val testDish = Dish("test","test recipe", ingredients, R.drawable.ic_launcher_background)
        val myRef = database.getReference("dishes")

        myRef.setValue("Recipe Book")
        myRef.child(testDish.name).setValue(testDish)
    }
}

