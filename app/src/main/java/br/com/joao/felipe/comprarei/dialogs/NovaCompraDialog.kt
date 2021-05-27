package br.com.joao.felipe.comprarei.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import br.com.joao.felipe.comprarei.R
import br.com.joao.felipe.comprarei.utils.Animacoes
import br.com.joao.felipe.comprarei.utils.formatadores.Formata
import br.com.joao.felipe.comprarei.utils.mascaraData
import com.google.android.material.textfield.TextInputEditText
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


class NovaCompraDialog : DialogFragment() {

    interface Cadastra {
        fun onCadastro(nome: String, data: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity?.layoutInflater?.inflate(R.layout.frame_nova_compra, null)

        val alert = AlertDialog.Builder(activity)

        val btnConfirma = view?.findViewById<Button>(R.id.formulario_compra_btn_confirma)
        val btnCancela = view?.findViewById<Button>(R.id.formulario_compra_btn_cancela)
        val compraNome = view?.findViewById<TextInputEditText>(R.id.formulario_compra_nome)
        val compraData = view?.findViewById<TextInputEditText>(R.id.formulario_compra_data)
        val mensagemErro = view?.findViewById<TextView>(R.id.formulario_compra_mensagem_erro)

        alert.setView(view)

        if (compraData != null) {
            mascaraData(compraData)
        }

        btnConfirma?.let { botao ->
            botao.setOnClickListener {
                if (compraNome?.text?.isEmpty() == true) {
                    Animacoes.notificaErro(botao, compraNome)
                    mensagemErro?.visibility = View.VISIBLE
                } else if (compraData?.text?.isEmpty() == true) {
                    Formata.formataDataString(Date.from(Instant.now()))?.let { data ->
                        (activity as (Cadastra)).onCadastro(compraNome?.text.toString(), data)
                        dismiss()
                    }
                } else if (!compraData?.text?.toString()?.let { it1 -> validaData(it1) }!!) {
                    compraData.let { data -> Animacoes.notificaErro(botao, data) }
                } else {
                    (activity as (Cadastra)).onCadastro(
                        compraNome?.text.toString(),
                        compraData.text.toString()
                    )
                    dismiss()
                }
            }
        }

        btnCancela?.setOnClickListener {
            dismiss()
        }

        return alert.create()
    }

    private fun validaData(data: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        sdf.isLenient = false
        try {
            val data = sdf.parse(data)
            if (data.after(Date("31/12/2030"))) {
                return false
            }
        } catch (e: ParseException) {
            return false
        }
        return true
    }


}