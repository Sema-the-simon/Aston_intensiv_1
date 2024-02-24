package com.example.aston_intensiv_1

import com.example.aston_intensiv_1.data.tracks

fun Int.nextTrack(): Int {
    var nextPosition = this + 1
    if (nextPosition >= tracks.size)
        nextPosition = 0
    return nextPosition
}

fun Int.previousTrack(): Int {
    var previousPosition = this - 1
    if (previousPosition < 0)
        previousPosition = tracks.size - 1
    return previousPosition
}