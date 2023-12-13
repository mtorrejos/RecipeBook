package com.example.recipebook

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream


class RecipeView : AppCompatActivity() {
    private lateinit var layout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_view)

        val name = intent.getStringExtra("name").toString()

        val database = Firebase.database(DatabaseConnect().connection)
        val dishRef = database.getReference("dishes")

        //var childRef: DatabaseReference = getNameRef(dishRef, name)

        val recipeName = findViewById<TextView>(R.id.txtRecipeTitle)
        val image = findViewById<ImageView>(R.id.imgRecipeImage)
        val backButton = findViewById<Button>(R.id.btnBack)
        layout = findViewById(R.id.mainLinearLayout)


        getNameRef(dishRef, name) { resultRef ->
            resultRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.e("childRefAddListener", resultRef.toString())
                    for (child in snapshot.child("ingredients").children) {

                        var cleanedText = child.value.toString().replace("{details=", "")
                        cleanedText = cleanedText.replace("}", "")
                        addDynamicViewText(cleanedText, "norm")
                    }
                    addDynamicViewText("Recipe Instructions:", "bold")
                    addDynamicViewInstruc(snapshot.child("recipe").value.toString())
                    recipeName.text = snapshot.child("name").value.toString()

                    val storage = Firebase.storage.reference
                    val imageRef = storage.child(name)

                    val buffer = (1024*1024)
                    imageRef.getBytes(buffer.toLong()).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeByteArray(it,0,it.size)
                        image.setImageBitmap(bitmap)
                        Log.i("imageRefSuccess", imageRef.toString())
                    }

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

    }
    private fun addDynamicViewText(string: String,style: String) {
        val dynamicViewTextLayout = LayoutInflater.from(this).inflate(R.layout.dynamic_view_text, null)
        val textView = dynamicViewTextLayout.findViewById<TextView>(R.id.txtRecipeIngredient)

        layout.addView(dynamicViewTextLayout)
        textView.text = string

        if(style == "bold")
            textView.setTypeface(Typeface.DEFAULT_BOLD)
    }


    private fun addDynamicViewInstruc(string: String) {
        val dynamicViewTextLayout = LayoutInflater.from(this).inflate(R.layout.dynamic_instruct, null)
        val textView = dynamicViewTextLayout.findViewById<TextView>(R.id.txtRecipeInstructions)

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



internal fun getNameRef(dishRef: DatabaseReference, name: String, callback: (DatabaseReference) -> Unit) {
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
