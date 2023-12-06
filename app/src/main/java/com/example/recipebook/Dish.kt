package com.example.recipebook

import android.graphics.Bitmap

data class Dish(var name: String, var recipe: String, var ingredients: ArrayList<Ingredient>, var hidden: Boolean) {

}
