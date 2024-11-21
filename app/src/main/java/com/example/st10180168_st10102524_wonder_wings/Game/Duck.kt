package com.example.st10180168_st10102524_wonder_wings.Game

//dataclass used to store the data relating to each duck, also allows for the movement of each duck
data class Duck(var x: Float, var y: Float, var speed: Int, var direction: String = "R", var isAlive: Boolean = true) {

    // Update the duck's position based on its speed and direction
    fun updatePosition() {
        if (direction == "R" && isAlive == true) {
            x += speed
        } else if (direction == "L" && isAlive == true) {
            x -= speed
        }
    }
}
