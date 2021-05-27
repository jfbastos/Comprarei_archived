package br.com.joao.felipe.comprarei.utils

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun mascaraData(editText: EditText) {

    var oldString = ""

    editText.addTextChangedListener(object : TextWatcher {
        var changed: Boolean = false

        override fun afterTextChanged(p0: Editable?) {
            changed = false
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //changed=false
            editText.setSelection(p0.toString().length)
        }

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            var str: String = p0.toString()

            when {
                oldString == str -> {
                    //significs que o user esta apagando
                    //do Nothing

                }
                str.length == 2 -> {  //  xx
                    val element0: String = str.elementAt(0).toString()
                    val element1: String = str.elementAt(1).toString()
                    str = "$element0$element1/"
                    editText.setText(str)
                    oldString = element0 + element1
                    editText.setSelection(str.length)

                }
                str.length == 5 -> { //  xx/xx

                    val element0: String = str.elementAt(0).toString() //x
                    val element1: String = str.elementAt(1).toString() //-x
                    val element2: String = str.elementAt(2).toString() //--/
                    val element3: String = str.elementAt(3).toString() //--/x
                    val element4: String = str.elementAt(4).toString() //--/-x

                    str = "$element0$element1$element2$element3$element4/"
                    editText.setText(str)
                    oldString = element0 + element1 + element2 + element3 + element4
                    editText.setSelection(str.length)

                }
                str.length > 10 -> { // este exemplo é para data no formato xx/xx/xx. Se você quer usar xx/xx/xxxx mudar para if (str.length >10). O resto do código permanece o mesmo.

                    str = str.substring(0, str.length - 1)
                    editText.setText(str)
                    editText.setSelection(str.length)

                }
            }

        }
    })
}