package com.example.recipebook

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
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
        val backButton = findViewById<Button>(R.id.btnBack)
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

                var digits = snapshot.child("image").value.toString()
                var bitmap = convertToBitmap(digits, 180, 180)
                //image.setImageBitmap(bitmap)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("view", "error")
            }

        })

        backButton.setOnClickListener() {
        val back = Intent(this, MainActivity::class.java)
            startActivity(back)
        }


    }
    private fun addDynamicViewText(string: String) {
        val dynamicViewTextLayout = LayoutInflater.from(this).inflate(R.layout.dynamic_view_text, null)
        val textView = dynamicViewTextLayout.findViewById<TextView>(R.id.txtRecipeIngredient)

        layout.addView(dynamicViewTextLayout)
        textView.text = string

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
    } //dead

    private fun setBitmapToImageView(bitmap: Bitmap, imageView: ImageView) {
        imageView.setImageBitmap(bitmap)
    } //dead
}
