package br.com.joao.felipe.comprarei.activitys

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import br.com.joao.felipe.comprarei.R
import br.com.joao.felipe.comprarei.dao.Produto
import br.com.joao.felipe.comprarei.utils.Animacoes
import br.com.joao.felipe.comprarei.utils.MaskWatcher
import br.com.joao.felipe.comprarei.utils.constantes.CHAVE_PRODUTO
import br.com.joao.felipe.comprarei.utils.constantes.QNT_MAX
import br.com.joao.felipe.comprarei.utils.constantes.QNT_MIN
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.formulario_item.*
import kotlin.properties.Delegates


class FormularioProdutoActivity : AppCompatActivity() {

    private var qnt: Int = 1
    private var idProduto by Delegates.notNull<Long>()
    private var idCompra by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.formulario_item)

        val nomeProduto = findViewById<TextInputEditText>(R.id.formulario_produto_texto_nome)
        val valorProduto = findViewById<TextInputEditText>(R.id.formulario_produto_texto_valor)
        val quantidadeProduto = findViewById<EditText>(R.id.formulario_produto_quantidade)
        val marcaProduto = findViewById<TextInputEditText>(R.id.formulario_produto_texto_marcar)

        valorProduto.addTextChangedListener(MaskWatcher(valorProduto))

        if (intent.hasExtra(CHAVE_PRODUTO)) {
            val produto = intent.getSerializableExtra(CHAVE_PRODUTO) as Produto
            idProduto = produto.id
            idCompra = produto.idCompra
            preencheCamposEdicao(nomeProduto, valorProduto, quantidadeProduto, marcaProduto)
            efetuaEdicaoProduto(
                nomeProduto,
                valorProduto,
                quantidadeProduto,
                marcaProduto,
                idCompra,
                idProduto
            )
        } else {
            efetuaCadastroProduto(nomeProduto, valorProduto, quantidadeProduto, marcaProduto)
        }

        editaQuantidade(quantidadeProduto)
    }

    private fun preencheCamposEdicao(
        nomeProduto: TextInputEditText,
        valorProduto: TextInputEditText,
        quantidadeProduto: EditText,
        marcaProduto: TextInputEditText
    ) {
        val produto = intent.getSerializableExtra(CHAVE_PRODUTO) as Produto
        nomeProduto.setText(produto.nome)
        valorProduto.setText(produto.valor)
        quantidadeProduto.setText(produto.quantidade)
        qnt = produto.quantidade.toInt()
        if (produto.marca != null) marcaProduto.setText(produto.marca)
        formulario_produto_btn_cadastra.text = "Salvar"
    }

    private fun efetuaEdicaoProduto(
        nomeProduto: TextInputEditText,
        valorProduto: TextInputEditText,
        quantidadeProduto: EditText,
        marcaProduto: TextInputEditText,
        idCompra: Long,
        idProduto: Long
    ) {
        formulario_produto_btn_cadastra.setOnClickListener { botao ->
            when {
                nomeProduto.text.isNullOrBlank() -> {
                    Animacoes.notificaErro(botao, nomeProduto)
                }
                valorProduto.text.isNullOrBlank() -> {
                    Animacoes.notificaErro(botao, valorProduto)
                }
                else -> {
                    val resultadoInsercao = intent
                    intent.putExtra(
                        CHAVE_PRODUTO,
                        Produto(
                            idProduto,
                            nomeProduto.text.toString(),
                            valorProduto.text.toString(),
                            quantidadeProduto.text.toString(),
                            marcaProduto.text.toString(),
                            idCompra
                        )
                    )
                    setResult(Activity.RESULT_OK, resultadoInsercao)
                    finish()
                }
            }
        }
    }

    private fun efetuaCadastroProduto(
        nomeProduto: TextInputEditText,
        valorProduto: TextInputEditText,
        quantidadeProduto: EditText,
        marcaProduto: TextInputEditText
    ) {
        formulario_produto_btn_cadastra.setOnClickListener { botao ->
            when {
                nomeProduto.text.isNullOrBlank() -> {
                    Animacoes.notificaErro(botao, nomeProduto)
                }
                valorProduto.text.isNullOrBlank() -> {
                    Animacoes.notificaErro(botao, valorProduto)
                }
                else -> {
                    val resultadoInsercao = intent
                    intent.putExtra(
                        CHAVE_PRODUTO,
                        Produto(
                            nomeProduto.text.toString(),
                            valorProduto.text.toString(),
                            quantidadeProduto.text.toString(),
                            marcaProduto.text.toString()
                        )
                    )
                    setResult(Activity.RESULT_OK, resultadoInsercao)
                    finish()
                }
            }
        }
    }

    private fun editaQuantidade(quantidade: EditText) {

        val botaoMenos = findViewById<FloatingActionButton>(R.id.formulario_produto_fab_menos)
        val botaoMais = findViewById<FloatingActionButton>(R.id.formulario_produto_fab_mais)

        quantidade.setText(qnt.toString())

        botaoMenos.setOnClickListener { botao ->
            qnt = quantidade.text.toString().toInt()
            if (qnt > QNT_MIN) {
                botao.isEnabled = true
                botaoMais.isEnabled = true
                qnt--
                quantidade.setText(qnt.toString())
            } else {
                botao.isEnabled = false
            }
        }

        botaoMais.setOnClickListener { botao ->
            qnt = quantidade.text.toString().toInt()
            if (qnt < QNT_MAX) {
                botao.isEnabled = true
                botaoMenos.isEnabled = true
                qnt++
                quantidade.setText(qnt.toString())
            } else {
                botao.isEnabled = false
            }
        }
    }
}