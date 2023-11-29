package com.example.recipebook
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class DishCreate() : AppCompatActivity() {
    private lateinit var layout: LinearLayout
    lateinit var ingrTextBoxes: ArrayList<EditText>
    private lateinit var imageView: ImageView

    val database = Firebase.database(DatabaseConnect().connection) //connection is on local file
    val dishRef = database.getReference("dishes")
    var childCount: Long = 0
    lateinit var imageActual: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_add)

        val addIngredientButton = findViewById<Button>(R.id.btnAddIngredient)
        val saveButton = findViewById<Button>(R.id.btnSaveRecipe)
        val imageButton = findViewById<Button>(R.id.btnPickImage)
        imageActual = findViewById<ImageView>(R.id.editRecipeImage)
        layout = findViewById(R.id.mainLinearLayout)
        ingrTextBoxes = arrayListOf()

        addIngredientButton.setOnClickListener(){
            addDynamicEditText()
        }

        saveButton.setOnClickListener() {
            addRecipe(ingrTextBoxes)
        }

        imageButton.setOnClickListener() {
            openGallery()
        }

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

            }
            override fun onCancelled(databaseError: DatabaseError) { Log.e("Error:", databaseError.message) }
        })

    }

    private fun addDynamicEditText() {
        val dynamicEditTextLayout = LayoutInflater.from(this).inflate(R.layout.dynamic_text, null)
        val editText = dynamicEditTextLayout.findViewById<EditText>(R.id.editRecipeIngredients)

        layout.addView(dynamicEditTextLayout)
        ingrTextBoxes.add(editText)

        for(i in ingrTextBoxes)
            Log.e("Ingre",i.toString())
    }

    private fun addRecipe(ingrTextBoxes: ArrayList<EditText>) {
        val firstIngredient = findViewById<EditText>(R.id.editRecipeIngredients)
        var ingredients : ArrayList<Ingredient> = arrayListOf()
        val recipeTitle = findViewById<EditText>(R.id.editRecipeTitle)
        val recipeInstruct = findViewById<EditText>(R.id.editRecipeInstructions)
        ingredients.add(Ingredient(firstIngredient.text.toString()))
        for (i in ingrTextBoxes) {
            val ingredientText = Ingredient(i.text.toString())
            if (!i.text.toString().isNullOrBlank())
                ingredients.add(ingredientText)
        }

        try {
            val dishToAdd = Dish(recipeTitle.text.toString(),recipeInstruct.text.toString(), ingredients, null) //dish details

            if(recipeTitle.text.toString().isNullOrBlank())
                Toast.makeText(this,"Title can't be blank!",Toast.LENGTH_SHORT).show()
            else if(recipeInstruct.text.toString().isNullOrBlank())
                Toast.makeText(this,"Please add some instructions!",Toast.LENGTH_SHORT).show()
            else if(firstIngredient.text.toString().isNullOrBlank())
                Toast.makeText(this,"Please add an ingredient!",Toast.LENGTH_SHORT).show()

            else {
                val dishChildCreate = dishRef.child("dishID: " + childCount++.toString()).ref //get reference to dishID: #, which would create the db entry
                dishChildCreate.setValue(dishToAdd) //actual creation of db entry
                startActivity(intent)
            }
        }

        catch (e:Exception) {
            Log.e("error", "error")
        }
    }

    private fun setCount(i: Long) { //just sets the child count for the main view
        childCount = i
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            imageActual.setImageBitmap(bitmap)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }
}


