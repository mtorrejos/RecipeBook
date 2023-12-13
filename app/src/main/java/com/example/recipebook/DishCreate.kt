package com.example.recipebook
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class DishCreate() : AppCompatActivity() {
    private lateinit var layout: LinearLayout
    lateinit var ingrTextBoxes: ArrayList<EditText>
    private lateinit var imageView: ImageView

    val database = Firebase.database(DatabaseConnect().connection) //connection is on local file
    val dishRef = database.getReference("dishes")
    var childCount: Long = 0
    lateinit var imageActual: ImageView
    var id: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_add)

        val addIngredientButton = findViewById<Button>(R.id.btnAddIngredient)
        val saveButton = findViewById<Button>(R.id.btnSaveRecipe)
        val imageButton = findViewById<Button>(R.id.btnPickImage)
        val cancelButton = findViewById<Button>(R.id.btnCancel)

        var editExtras = intent.extras
        var editPosition = editExtras?.getString("name")
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
                    var ingredients: ArrayList<String> = arrayListOf()
                    val recipeTitle = findViewById<EditText>(R.id.editRecipeTitle)
                    val recipeInstruct = findViewById<EditText>(R.id.editRecipeInstructions)
                    //val dataSnapshot = database.getReference("dishes")

                    Log.e("editpos", editPosition)
                    val editing = editExtras?.getBoolean("editing")
                    castTo(editing!!, dishRef)

                    for (child in dataSnapshot.children) {
                        if (child.child("name").value.toString() == editPosition) {
                            layout.removeView(firstIngredient)
                            for (ingredient in child.child("ingredients").children) {
                                Log.i("ingredient Text", ingredient.value.toString())
                                var cleanedText = ingredient.value.toString().replace("{details=", "")
                                cleanedText = cleanedText.replace("}", "")
                                cleanedText = cleanedText.replace("[", "")
                                cleanedText = cleanedText.replace("]", "")

                                ingredients.add(cleanedText)
                                Log.i("ingredient array Text", ingredients.toString())

                            }

                            addDynamicEditTextEdit(ingredients)
                            recipeTitle.setText(child.child("name").value.toString())
                            recipeInstruct.setText(child.child("recipe").value.toString())
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

    private fun addDynamicEditTextEdit(array: ArrayList<String>) {

        for(item in array) {
            val dynamicEditTextLayout = LayoutInflater.from(this).inflate(R.layout.dynamic_text, null)
            val editText = dynamicEditTextLayout.findViewById<EditText>(R.id.editRecipeIngredients)

            layout.addView(dynamicEditTextLayout)
            ingrTextBoxes.add(editText)
            editText.setText(item)
        }
    }

    private fun addRecipe(ingrTextBoxes: ArrayList<EditText>) {

        val firstIngredient = findViewById<EditText>(R.id.editRecipeIngredients)
        var ingredients : ArrayList<Ingredient> = arrayListOf()
        val recipeTitle = findViewById<EditText>(R.id.editRecipeTitle)
        val recipeInstruct = findViewById<EditText>(R.id.editRecipeInstructions)

        val storage = Firebase.storage.reference
        val imageRef = storage.child(recipeTitle.text.toString())

        ingredients.add(Ingredient(firstIngredient.text.toString()))
        for (i in ingrTextBoxes) {
            val ingredientText = Ingredient(i.text.toString())
            if (!i.text.toString().isNullOrBlank())
                ingredients.add(ingredientText)
        }

        try {
            imageView = findViewById(R.id.editRecipeImage)

            imageView.isDrawingCacheEnabled = true
            imageView.buildDrawingCache()
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = imageRef.putBytes(data)
            uploadTask.addOnFailureListener {
                Log.i("uploaderror", it.toString())
            }.addOnSuccessListener { taskSnapshot ->
                sendToDatabase(recipeTitle,firstIngredient,recipeInstruct,ingredients,imageRef)

            }

           // var imageDigits = convertToDigits(imageBitmap)

            Log.i("after image upload","hello")


        }

        catch (e:Exception) {
           Log.e("error", e.printStackTrace().toString())
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


    private fun castTo(setter: Boolean, dishRef: DatabaseReference) {
        id = setter

    }


    private fun sendToDatabase(recipeTitle: EditText, firstIngredient: EditText, recipeInstruct: EditText, ingredients: ArrayList<Ingredient>, imageRef: StorageReference) {
        val dishToAdd = Dish(recipeTitle.text.toString(),recipeInstruct.text.toString(), ingredients, false) //dish details

        if(recipeTitle.text.toString().isNullOrBlank() || recipeInstruct.text.toString().isNullOrBlank() || firstIngredient.text.toString().isNullOrBlank()) {
            Toast.makeText(this,"Please fill in all the required fields.",Toast.LENGTH_SHORT).show()
        }
        else {
            Log.i("dishtoadd", "dishdetails")
                if (id) {

                    var editExtras = intent.extras
                    val name = editExtras!!.getString("name")


                    getNameRef(dishRef, name!!) { resultRef ->

                        resultRef.child("ingredients").removeValue()
                        ingredients.clear()
                        for (i in ingrTextBoxes) {
                            val ingredientText = Ingredient(i.text.toString())
                            if (!i.text.toString().isNullOrBlank())
                                ingredients.add(ingredientText)
                        }

                        val dishToAdd = Dish(
                            recipeTitle.text.toString(),
                            recipeInstruct.text.toString(),
                            ingredients,
                            false
                        ) //dish details

                        resultRef.setValue(dishToAdd) //actual creation of db entry
                        startActivity(intent)

                    }


                } else {
                    val dishChildCreate =
                        dishRef.child("dishID: " + childCount++.toString()) //get reference to dishID: #, which would create the db entry
                    dishChildCreate.setValue(dishToAdd) //actual creation of db entry
                    startActivity(intent)
                }

        }
    }

}





