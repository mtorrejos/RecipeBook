package com.example.recipebook
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
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
    var id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_add)

        val addIngredientButton = findViewById<Button>(R.id.btnAddIngredient)
        val saveButton = findViewById<Button>(R.id.btnSaveRecipe)
        val imageButton = findViewById<Button>(R.id.btnPickImage)
        val cancelButton = findViewById<Button>(R.id.btnCancel)

        var editExtras = intent.extras
        var editPosition = editExtras?.getString("id")
        castTo(editPosition.toString())
        Log.e("editpos1",editPosition.toString())


        imageActual = findViewById<ImageView>(R.id.editRecipeImage)
        layout = findViewById(R.id.mainLinearLayout)
        ingrTextBoxes = arrayListOf()

        addIngredientButton.setOnClickListener(){
            addDynamicEditText()
        }

        saveButton.setOnClickListener() {
            addRecipe(ingrTextBoxes)
            val home = Intent(this, MainActivity::class.java)
            startActivity(home)
        }

        imageButton.setOnClickListener() {
            openGallery()
        }

        cancelButton.setOnClickListener() {
            val home = Intent(this, MainActivity::class.java)
            startActivity(home)
        }


        dishRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var numberOfChildren = dataSnapshot.childrenCount
                setCount(numberOfChildren)

                Log.e("editpos", editPosition.toString())

                if (!editPosition.isNullOrBlank()) {
                    val firstIngredient = findViewById<EditText>(R.id.editRecipeIngredients)
                    var ingredients: ArrayList<Ingredient> = arrayListOf()
                    val recipeTitle = findViewById<EditText>(R.id.editRecipeTitle)
                    val recipeInstruct = findViewById<EditText>(R.id.editRecipeInstructions)
                    id = editPosition
                    //val dataSnapshot = database.getReference("dishes")

                    Log.e("editpos", editPosition)

                    for (key in dataSnapshot.children) {
                        if (key.key == editPosition) {
                            layout.removeView(firstIngredient)
                            for (child in key.child("ingredients").children) {
                                var cleanedText = child.value.toString().replace("{details=", "")
                                cleanedText = cleanedText.replace("}", "")
                                addDynamicEditTextEdit(cleanedText)
                            }

                            recipeTitle.setText(key.child("name").value.toString())
                            recipeInstruct.setText(key.child("recipe").value.toString())
                        }
                    }
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

    private fun addDynamicEditTextEdit(string: String) {
        val dynamicEditTextLayout = LayoutInflater.from(this).inflate(R.layout.dynamic_text, null)
        val editText = dynamicEditTextLayout.findViewById<EditText>(R.id.editRecipeIngredients)

        layout.addView(dynamicEditTextLayout)
        ingrTextBoxes.add(editText)
        editText.setText(string)

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
            imageView = findViewById(R.id.editRecipeImage)
            var imageBitmap = imageViewToBitmap(imageView)
           // var imageDigits = convertToDigits(imageBitmap)

            val dishToAdd = Dish(recipeTitle.text.toString(),recipeInstruct.text.toString(), ingredients, null) //dish details
            var cont: Boolean

            if(recipeTitle.text.toString().isNullOrBlank() || recipeInstruct.text.toString().isNullOrBlank() || firstIngredient.text.toString().isNullOrBlank()) {
                Toast.makeText(this,"Please fill in all the required fields.",Toast.LENGTH_SHORT).show()
                cont = false
            }
            else
                cont = true

            if((id == "") && cont) {
                val dishChildCreate = dishRef.child(id) //get reference to dishID: #, which would create the db entry
                dishChildCreate.setValue(dishToAdd) //actual creation of db entry

                for(item in ingredients) {
                    ingredients.remove(item)
                }

                startActivity(intent)

            }


            else if(!(id == "") && cont) {
                val dishChildCreate = dishRef.child("dishID: " + childCount++.toString()) //get reference to dishID: #, which would create the db entry
                dishChildCreate.setValue(dishToAdd) //actual creation of db entry
                startActivity(intent)
            }

        }

        catch (e:Exception) {
           Log.e("error", e.toString())
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

    private fun imageViewToBitmap(imageView: ImageView): Bitmap { //dead inside
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            // If the ImageView has a BitmapDrawable, get its Bitmap
            return drawable.bitmap
        }

        // If the ImageView doesn't have a BitmapDrawable, create a new Bitmap
        val bitmap = Bitmap.createBitmap(
            imageView.width,
            imageView.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return bitmap
    }


    private fun castTo(setter: String) {
        id = setter
    }
}


