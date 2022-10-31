package com.kalex.bookyouu_app.data.remote.dto

import com.kalex.bookyouu_app.domain.model.ItemOfice


data class ItemOficeDto(
    val Ciudad: String ,
    val IdOficina: Int,
    val Latitud: String,
    val Longitud: String,
    val Nombre: String
)

fun ItemOficeDto.toItemOfice(): ItemOfice {
    return ItemOfice(
       Ciudad = Ciudad,
       Latitud = Latitud,
       Longitud = Longitud,
       Nombre = Nombre
    )
}