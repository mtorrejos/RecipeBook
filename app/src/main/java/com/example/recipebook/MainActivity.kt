package com.example.recipebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

open class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var dishList : ArrayList<Dish>
    val database = Firebase.database(//database url)
    val recipeView = RecipeView()
    val dishRef = database.getReference("dishes")
    var childCount: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        recipeView.getRecipes()

        //dishRef child counter
        dishRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var numberOfChildren = dataSnapshot.childrenCount
                setCount(numberOfChildren)
            }
            override fun onCancelled(databaseError: DatabaseError) { Log.e("Error:", databaseError.message) }
        })


        dishList = arrayListOf()

        findViewById<Button>(R.id.button).setOnClickListener(){
            uploadData()
        }

        findViewById<Button>(R.id.updatebtn).setOnClickListener(){
            val dishChildCreate = dishRef.child("dishID: " + (childCount-1).toString()).ref //get reference to dishID: #, which would create the db entry
            dishChildCreate.setValue("HELLO WORLD")
        }
    }

fun uploadData() {
    var ingredients : ArrayList<Ingredient> = arrayListOf()
    try {

        val testDish = Dish("dishID: "+ (childCount+1).toString(),"test recipe", ingredients, R.drawable.ic_launcher_background) //dish details
        //val dishChildCreate = dishRef.push() //reference child creation, push() is the function that creates the child
        //dishChildCreate.child((childCount++).toString()).setValue(testDish) //creates the actual child with specified path and sets the value

        //below is arguably the better way to do it
        val dishChildCreate = dishRef.child("dishID: " + childCount++.toString()).ref //get reference to dishID: #, which would create the db entry
        dishChildCreate.setValue(testDish) //actual creation of db entry
    }

    catch (e:Exception) {
        Log.e("error", "error")
    }
}

    private fun setCount(i: Long) { //just sets the child count for the main view
        childCount = i
    }
}

