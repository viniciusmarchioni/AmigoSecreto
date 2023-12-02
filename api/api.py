from flask import Flask, request
import psycopg2


conn = psycopg2.connect(
    host="host",
    database="database",
    user="user",
    password="password",
)


# Criar um cursor
cursor = conn.cursor()


app = Flask(__name__)


# @app.route("/<int:id>")


@app.route("/hosts/<string:id>")
def returnTableID(id):
    cursor.execute("select id_table from hosts where cpf='" + id + "'")
    resultado = cursor.fetchone()[0]
    return resultado


@app.route("/games/<string:tableID>/<string:nome>_<string:cpf>_<string:desejo>")
def addParticipante(tableID, nome, cpf, desejo):
    valor = f'"{tableID}"'
    if verifyGuest(cpf, valor):
        return f"Você já está cadastrado."
    cursor.execute(
        f"INSERT INTO {valor} (nome, cpf, desejo) VALUES ('{nome}', '{cpf}', '{desejo}');"
    )
    conn.commit()
    return "Feito"


def verifyGuest(cpf, tableID):
    cursor.execute(f"select cpf from {tableID} where cpf='{cpf}'")
    resultado = cursor.fetchone()[0]
    if resultado is not None:
        return True
    return False


app.run(port=5000, host="localhost", debug=True)


cursor.close()
conn.close()
