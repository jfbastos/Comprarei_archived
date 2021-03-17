package br.com.joao.felipe.comprarei.utils

import android.view.View
import android.view.animation.AnimationUtils
import br.com.joao.felipe.comprarei.R
import com.google.android.material.textfield.TextInputEditText

class Animacoes {

    companion object {
        fun notificaErro(view: View, text: TextInputEditText) {
            val erro = AnimationUtils.loadAnimation(view.context, R.anim.shake)
            text.startAnimation(erro)
        }
    }
}