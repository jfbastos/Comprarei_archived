package br.com.joao.felipe.comprarei.utils.formatadores

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class Formata {

    companion object {
        fun formatoDinheiroBigDecimal(valor: BigDecimal?): String? {
            return String.format(Locale.ENGLISH, "R$ %.2f", valor)
        }

        fun formatoDinheiroString(valor: String?): String? {
            return formatoDinheiroBigDecimal(BigDecimal(valor))
        }

        fun formataDataString(data: Date?): String? {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            return sdf.format(data)
        }
    }
}