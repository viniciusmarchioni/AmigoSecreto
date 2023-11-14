package com.example.amigosecretoguest

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //precisa disso para acessar o banco não sei pq
        val threadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(threadPolicy)

        val cadButton = findViewById<Button>(R.id.cadastrar)
        val verButton = findViewById<Button>(R.id.verificar)
        val status = findViewById<TextView>(R.id.status)
        val editCpf = findViewById<EditText>(R.id.cpf_edit)
        val editName = findViewById<EditText>(R.id.nome_edit)
        val editDesejo = findViewById<EditText>(R.id.desejo_edit)

        cadButton.setOnClickListener {
            status.text = cadastro(
                editCpf, editName, editDesejo, cadButton
            )
        }
        verButton.setOnClickListener {
            status.text = verificarSorteio(editCpf.text.toString(), verButton)
        }
    }

    private fun cadastro(cpf: EditText, nome: EditText, desejo: EditText, button: Button): String {
        button.isClickable = false
        //Acesso ao banco de dados
        val url = "url"
        val user = "user"
        val password = "password"

        //Verificações de entradas
        if (verificarString(cpf.text.toString(), 14, 5)) {
            button.isClickable = true
            return "Cpf invalido"
        }
        if (verificarString(nome.text.toString(), 25, 2)) {
            button.isClickable = true
            return "Nome não tamanho aceito"
        }
        if (verificarString(desejo.text.toString(), 255, 2)) {
            button.isClickable = true
            return "Desejo não aceito"
        }

        //Tentativa de acesso
        try {
            val connection: Connection = DriverManager.getConnection(url, user, password)
            val statement = connection.createStatement()
            val resultSet =
                statement.executeQuery("select nome from participantes where cpf = '${cpf.text.toString()}'") // query
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
            val sql = "INSERT INTO participantes (nome, cpf, desejo) VALUES (?, ?, ?)"
            val preparedStatement = connection.prepareStatement(sql)

            preparedStatement.setString(1, nome.text.toString())
            preparedStatement.setString(2, cpf.text.toString())
            preparedStatement.setString(3, desejo.text.toString())

            val rowsAffected = preparedStatement.executeUpdate()

            preparedStatement.close()
            statement.close()
            connection.close()

            //Bloqueia o botão para evitar spam
            button.isClickable = false
            button.isVisible = false
            return "Participante adicionado"
        } catch (e: Exception) {
            button.isClickable = true
            return "Algo deu errado :(:\n $e"
        }

    }

    //retorna verdadeiro se estiver dento do aceitavel pelo banco de dados
    private fun verificarString(str: String, Maxsize: Int, Minsize: Int): Boolean {
        // Verificar o comprimento da string
        return str.length > Maxsize || str.length < Minsize
    }

    //verifica quem foi sorteado
    fun verificarSorteio(cpf: String, button: Button): String {
        button.isClickable = false
        val url = "url"
        val user = "user"
        val password = "password"

        try {
            val connection: Connection = DriverManager.getConnection(url, user, password)
            val statement = connection.createStatement()
            var resultSet =
                statement.executeQuery("select nome from participantes where cpf = '$cpf'")

            //se não tem próximo ele não está cadastrado portanto não tem ngm para sortear
            if (!resultSet.next()) {
                statement.close()
                connection.close()
                button.isClickable = true
                return "Você não está cadastrado"
            }
            //se está cadastrado mais uma query
            resultSet =
                statement.executeQuery("select nome, desejo from participantes where id = (select amigosecretoid from participantes where cpf = '$cpf');")

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
            return "Sorteio ainda não aconteceu"
        } catch (e: Exception) {
            return "Algum erro na verificação de cadastro existente"
        }
    }
}
