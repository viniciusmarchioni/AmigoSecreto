package com.example.amigosecretoguest

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import java.sql.Connection
import java.sql.DriverManager

class MainActivity : AppCompatActivity() {


    lateinit var botaoinu:Button;

    //Acesso ao banco de dados
    val url = "jdbc:postgresql://isabelle.db.elephantsql.com:5432/zlhwkfxk"
    val user = "zlhwkfxk"
    val password = "5H5djg3N01zMeTkRC3RmnZoFVo9Yia63"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //precisa disso para acessar o banco não sei pq
        val threadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(threadPolicy)

        val cadButton = findViewById<Button>(R.id.cadastrar)
        val verButton = findViewById<Button>(R.id.verificar)
        val troca = findViewById<Button>(R.id.troca)
        val status = findViewById<TextView>(R.id.status)
        val editCpf = findViewById<EditText>(R.id.cpf_edit)
        val editName = findViewById<EditText>(R.id.nome_edit)
        val editDesejo = findViewById<EditText>(R.id.desejo_edit)
        val id = findViewById<EditText>(R.id.gameId)

        cadButton.setOnClickListener {
            status.text = cadastro(
                editCpf.text.toString(), editName.text.toString(), editDesejo.text.toString(),
                cadButton, id.text.toString())
        }

        verButton.setOnClickListener {
            status.text = verificarSorteio(editCpf.text.toString(), verButton, id.text.toString())
        }

        troca.setOnClickListener {
            val novaTela = Intent(this,CreateGameActivity::class.java)
            startActivity(novaTela)
        }

    }

    fun cadastro(cpf: String, nome: String, desejo: String, button: Button, id:String): String {
        button.isClickable = false

        //Verificações de entradas
        if (verificarString(cpf, 14, 5)) {
            button.isClickable = true
            return "CPF inválido."
        }
        if (verificarString(nome, 25, 2)) {
            button.isClickable = true
            return "Nome inválido."
        }
        if (verificarString(desejo, 255, 2)) {
            button.isClickable = true
            return "Desejo não aceito."
        }
        if (verificarString(id, 11, 9)) {
            button.isClickable = true
            return "ID inexistente."
        }

        //Tentativa de acesso
        try {
            val connection: Connection = DriverManager.getConnection(url, user, password)
            val statement = connection.createStatement()
            var resultSet =
                statement.executeQuery("select id_table from hosts where id_table = '$id'") // query
            if (!resultSet.next()) {
                statement.close()
                connection.close()
                return "Jogo não encontrado\nVerifique o ID"
            }

            resultSet =
                statement.executeQuery("select nome from \"$id\" where cpf = '$cpf'") // query
            //Se tem um próximo então exite portanto...
            if (resultSet.next()) {
                val name = resultSet.getString("nome")
                statement.close()
                connection.close()
                button.isVisible = false
                return "Você Já está cadastrado $name"
            }
            //se não tiver segue o script
        } catch (e: Exception) {
            return "Algum erro na verificação de cadastro existente"
        }


        //Abre a conexão de novo e adiciona
        try {
            val connection: Connection = DriverManager.getConnection(url, user, password)
            val statement = connection.createStatement()
            val sql = "INSERT INTO \"$id\" (nome, cpf, desejo) VALUES (?, ?, ?)"
            val preparedStatement = connection.prepareStatement(sql)

            preparedStatement.setString(1, nome)
            preparedStatement.setString(2, cpf)
            preparedStatement.setString(3, desejo)

            preparedStatement.executeUpdate()

            preparedStatement.close()
            statement.close()
            connection.close()

            //Bloqueia o botão para evitar spam
            button.isClickable = false
            button.isVisible = false
            return "Participante adicionado"
        } catch (e: Exception) {
            button.isClickable = true
            return "Algo deu errado na inserção dos dados."
        }
    }

    //retorna verdadeiro se estiver dento do aceitavel pelo banco de dados
    fun verificarString(str: String, Maxsize: Int, Minsize: Int): Boolean {
        // Verificar o comprimento da string
        return str.length > Maxsize || str.length < Minsize
    }

    //verifica quem foi sorteado
    private fun verificarSorteio(cpf: String, button: Button,id: String): String {
        button.isClickable = false

        if(verificarString(cpf,14,5)){
            button.isClickable = true
            return "CPF inválido."
        }

        try {
            val connection: Connection = DriverManager.getConnection(url, user, password)
            val statement = connection.createStatement()
            var resultSet =
                statement.executeQuery("select nome from \"$id\" where cpf = '$cpf'")

            //se não tem próximo ele não está cadastrado portanto não tem ngm para sortear
            if (!resultSet.next()) {
                statement.close()
                connection.close()
                button.isClickable = true
                return "Você não está cadastrado."
            }
            //se está cadastrado mais uma query
            resultSet =
                statement.executeQuery("select nome, desejo from \"$id\" where id_guest = (select amigosecretoid from \"$id\" where cpf = '$cpf');")

            //se resultar em algo, retornar valores
            if (resultSet.next()) {
                val name = resultSet.getString("nome")
                val desejo = resultSet.getString("desejo")
                statement.close()
                connection.close()
                button.isClickable = true
                return "$name quer \n $desejo"
            }

            //se não o sorteio ainda não foi realizado
            statement.close()
            connection.close()
            button.isClickable = true
            return "Sorteio ainda não aconteceu."
        } catch (e: Exception) {
            return "Algum erro na verificação de cadastro existente $e"
        }
    }
}
