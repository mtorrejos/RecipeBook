package com.example.recipebook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
    val database = Firebase.database(DatabaseConnect().connection)
    val recipeView = RecipeView()
    val dishRef = database.getReference("dishes")
    var childCount: Long = 0
    var nameList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtUpdate = findViewById<EditText>(R.id.edtUpdateName)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        recipeView.getRecipes()

        dishList = arrayListOf()

        findViewById<Button>(R.id.btnMove).setOnClickListener(){
            val intent = Intent(this, DishCreate::class.java)
            startActivity(intent)
        }

        //dishRef child counter
        dishRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var numberOfChildren = dataSnapshot.childrenCount
                setCount(numberOfChildren)
                var names: ArrayList<String> = arrayListOf()

                for (childSnapshot in dataSnapshot.children) {
                    val id = childSnapshot.key // Assuming the key is the ID
                    names += "$id"
                    Log.e("dishID", id!!)
                }

                setnameList(names)
                val spnDB = findViewById<Spinner>(R.id.spnDBItems)
                spnDB.adapter = ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item, nameList)

            }
            override fun onCancelled(databaseError: DatabaseError) { Log.e("Error:", databaseError.message) }
        })

        val spnDB = findViewById<Spinner>(R.id.spnDBItems)
        spnDB.adapter = ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item, nameList)

        findViewById<Button>(R.id.btnUpload).setOnClickListener(){
            uploadData()
        }

        findViewById<Button>(R.id.btnUpdate).setOnClickListener(){
            val ingredient = Ingredient("potato, 1kg")
            val tempIng: ArrayList<Ingredient> = arrayListOf(ingredient)
            val dishChildCreate = dishRef.child((spnDB.selectedItem.toString())).ref //get reference to dishID: #
            val updateDishData = Dish(edtUpdate.text.toString(), "recipe", tempIng, null)
            dishChildCreate.setValue(updateDishData)
        }
    }

fun uploadData() {
    var ingredients : ArrayList<Ingredient> = arrayListOf()
    try {

        val testDish = Dish("dishID: "+ (childCount).toString(),"test recipe", ingredients, null) //dish details
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

    private fun setnameList(s: ArrayList<String>) {
        nameList = s
    }
}



