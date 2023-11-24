package com.example.amigosecretoguest

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import java.sql.Connection
import java.sql.DriverManager
import java.util.Random


class CreateGameActivity : AppCompatActivity() {

    private lateinit var id: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)

        //precisa disso para acessar o banco não sei pq
        val threadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(threadPolicy)

        val cpfedit = findViewById<EditText>(R.id.cpf_edit)
        val name = findViewById<EditText>(R.id.nome_edit)
        val desejo = findViewById<EditText>(R.id.desejo_edit)
        val button = findViewById<Button>(R.id.criar)
        val text = findViewById<TextView>(R.id.status)
        val copy = findViewById<ImageButton>(R.id.copy)
        val join = findViewById<Button>(R.id.entrar)

        button.setOnClickListener {
            text.text = createTable(
                cpfedit.text.toString(), name.text.toString(), desejo.text.toString(), button, copy
            )
        }

        copy.setOnClickListener {
            val clipboardManager: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Label", id)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(this, "Copiado!", Toast.LENGTH_SHORT).show();
        }

        join.setOnClickListener {
            val novaTela = Intent(this,MainActivity::class.java)
            startActivity(novaTela)
        }

    }


    fun createTable(
        cpf: String,
        nome: String,
        desejo: String,
        button: Button,
        copyButton: ImageButton
    ): String {

        button.isClickable = false

        if (MainActivity().verificarString(cpf, 14, 8)) {
            button.isClickable = true
            return "CPF inválido."
        }

        try {
            val connection: Connection = DriverManager.getConnection(
                MainActivity().url,
                MainActivity().user,
                MainActivity().password
            )
            val statement = connection.createStatement()
            var resultSet =
                statement.executeQuery("select cpf from hosts where cpf = '$cpf'")

            //se tem próximo ele já tem sorteio, portanto tchau
            if (resultSet.next()) {
                statement.close()
                connection.close()
                button.isClickable = true
                return "É possivel apenas 1 jogo por CPF."
            }

            id = gerarChave()
            while (true) {
                resultSet =
                    statement.executeQuery("select id_table from hosts where id_table = '$id'")
                if (!resultSet.next()) {
                    break
                }
                id = gerarChave()
            }

            val sql = "INSERT INTO hosts (cpf, id_table) VALUES (?, ?)"
            val preparedStatement = connection.prepareStatement(sql)

            preparedStatement.setString(1, cpf)
            preparedStatement.setString(2, id)

            preparedStatement.executeUpdate()

            preparedStatement.close()


            statement.executeUpdate(
                "CREATE TABLE \"$id\"(id_guest serial not null," +
                        "nome varchar(25) not null,amigosecretoid int,cpf varchar(14)," +
                        "desejo varchar(255)," +
                        "primary key(id_guest))"
            )


            statement.close()
            connection.close()

            MainActivity().cadastro(cpf, nome, desejo, button, id);


            copyButton.isClickable = true
            copyButton.isVisible = true


            return "Jogo Criado!\nSeu id é:\n$id"

        } catch (e: Exception) {
            if (e.toString().contains("23505")) {
                button.isClickable = true
                return "Apenas uma criação por CPF!"
            }
            button.isClickable = true
            return "Algum erro na criação: $e"
        }

    }

    private fun gerarChave(): String {

        var key = ""

        for (i in 1..10) {
            val numeroAleatorio = Random().nextInt(90 - 60 + 1) + 60
            key += numeroAleatorio.toChar()
        }

        return key
    }


}