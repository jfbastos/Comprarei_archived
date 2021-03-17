package br.com.joao.felipe.comprarei.dao

import java.io.Serializable

class Produto : Serializable {
    var nome: String
    var valor: String
    var quantidade: String
    var marca: String? = null
    var id: Long = -1L
    var idCompra: Long = -1L

    constructor(
        id: Long,
        nome: String,
        valor: String,
        quantidade: String,
        marca: String?,
        idCompra: Long
    ) {
        this.id = id
        this.nome = nome
        this.valor = valor
        this.quantidade = quantidade
        this.marca = marca
        this.idCompra = idCompra
    }

    constructor(nome: String, valor: String, quantidade: String, marca: String?) {
        this.nome = nome
        this.valor = valor
        this.quantidade = quantidade
        this.marca = marca
    }


}