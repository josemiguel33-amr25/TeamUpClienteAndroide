package com.example.teamup


data class PartidoSimplificado( // clases de java fx pero aqui en android
    val idPartido: Int,
    val tituloPartido: String,
    val ubicacion: String,
    val ciudad: String,
    val precio: Double,
    val dia: Int, val mes: Int, val anio: Int,
    val hora: Int, val minutos: Int,
    val estado: String,
    val soloVerificados: Boolean,
    val nombreUsuario: String,
    val idUsuario: Int,
    val fotoUsuario: String
)


data class CosmeticoSimplificado(
    val tituloCosmetico: String,
    val tipo: String,
    val rareza: String,
    val cantidad: Int,
    val vendible: Boolean,
    val idCosmetico: Int
)


data class UsuarioSimplificado(
    val nombre: String,
    val rango: String,
    val puntos: Int,
    val reputacion: Int,
    val goles: Int,
    val asistencias: Int,
    val mvps: Int,
    val verificado: Boolean
)


data class MercadoSimplificado(
    val nombreArticulo: String,
    val nombreVendedor: String,
    val idVendedor: Int,
    val precio: Int,
    val idArticulo: Int,
    val tipoCosmetico: String
)