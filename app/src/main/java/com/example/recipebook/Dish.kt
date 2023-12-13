package com.example.recipebook

import com.google.firebase.storage.StorageReference

data class Dish(
    var name: String,
    var recipe: String,
    var ingredients: ArrayList<Ingredient>,
    var hidden: Boolean
) {

}
