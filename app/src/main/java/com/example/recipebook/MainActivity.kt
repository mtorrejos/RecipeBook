package com.example.recipebook

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
                var numberOfChildren = dataSnapshot.childrenCount

                setCount(numberOfChildren)

                for (childNode in dataSnapshot.children) {
                    var childIngredientList: ArrayList<Ingredient>
                    childIngredientList = arrayListOf()
                    var ingredientsNode = childNode.child("ingredients")

                    for (ingredientNode in ingredientsNode.children) { //for loop for ingredients
                        var childIngredient = Ingredient(ingredientNode.value.toString())
                        childIngredientList.add(childIngredient)
                    }

                    var bitmappedImage = convertToBitmap(childNode.child("image").value.toString(),60,60)

                   dishList.add(Dish(childNode.child("name").value.toString(), childNode.child("recipe").value.toString(), childIngredientList, bitmappedImage))
                    recyclerView.adapter = AdapterClass(dishList)
                }

            }
            override fun onCancelled(databaseError: DatabaseError) { Log.e("Error:", databaseError.message) }
        })



        /*findViewById<Button>(R.id.btnUpdate).setOnClickListener(){
            val ingredient = Ingredient("potato, 1kg")
            val tempIng: ArrayList<Ingredient> = arrayListOf(ingredient)
            val dishChildCreate = dishRef.child((spnDB.selectedItem.toString())).ref //get reference to dishID: #
            val updateDishData = Dish(edtUpdate.text.toString(), "recipe", tempIng, null)
            dishChildCreate.setValue(updateDishData)
        }*/
    }


    private fun setCount(i: Long) { //just sets the child count for the main view
        childCount = i
    }

    private fun setnameList(s: ArrayList<String>) {
        nameList = s
    }

    private fun convertToBitmap(digits: String, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Set up Paint for drawing
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 40f

        // Draw the digits on the canvas
        canvas.drawText(digits, 0f, height / 2f, paint)

        return bitmap
    }

    private fun addtoDishList(dish:Dish) {
        dishList.add(dish)

    }
}



