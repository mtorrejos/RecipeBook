package com.example.recipebook

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RecipeView : AppCompatActivity() {
    private lateinit var layout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_view)

        val id = intent.getStringExtra("id").toString()

        val database = Firebase.database(DatabaseConnect().connection)
        val dishRef = database.getReference("dishes")
        val childRef = dishRef.child(id)

        val recipeName = findViewById<TextView>(R.id.txtRecipeTitle)
        val recipeInstruct = findViewById<TextView>(R.id.txtRecipeInstructions)
        val image = findViewById<ImageView>(R.id.imgRecipeImage)
        layout = findViewById(R.id.mainLinearLayout)


        childRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (child in snapshot.child("ingredients").children) {
                    var cleanedText = child.value.toString().replace("{details=","")
                    cleanedText = cleanedText.replace("}","")
                    addDynamicViewText(cleanedText)
                }
                recipeName.text = snapshot.child("name").value.toString()
                recipeInstruct.text = snapshot.child("recipe").value.toString()

                //add image

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("view", "error")
            }

        })


    }
    private fun addDynamicViewText(string: String) {
        val dynamicViewTextLayout = LayoutInflater.from(this).inflate(R.layout.dynamic_view_text, null)
        val textView = dynamicViewTextLayout.findViewById<TextView>(R.id.txtRecipeIngredient)

        layout.addView(dynamicViewTextLayout)
        textView.text = string

    }

}
