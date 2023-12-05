from flask import Flask, jsonify, request
import psycopg2
import json
import functions as func

conn = psycopg2.connect(
    host="host",
    database="database",
    user="user",
    password="password",
)

# Criar um cursor
cursor = conn.cursor()


app = Flask(__name__)


def criarJson(tableid=str(), cpf=str(), nome=str(), desejo=str(), response=str()):
    return jsonify(
        {
            "tableID": tableid,
            "nome": nome,
            "cpf": cpf,
            "desejo": desejo,
            "response": response,
        }
    )

@app.route("/games/addguest/", methods=["POST"])
def addParticipante():
    participante = request.get_json()
    try:
        tableID = participante["tableID"]
        nome = participante["nome"]
        cpf = participante["cpf"]
        desejo = participante["desejo"]
        resposta = participante["response"]
        cpf = func.limpar_cpf(cpf)

        cursor.execute(f"select id_table from hosts where id_table='{tableID}'")
        result = cursor.fetchone()

        if func.invalidValue(tableID, 10, 10) or result is None:
            return jsonify(
                {
                    "tableID": tableID,
                    "nome": nome,
                    "cpf": cpf,
                    "desejo": desejo,
                    "response": "ID não encontrado",
                }
            )

        elif func.invalidValue(nome, 2, 25, True):
            return jsonify(
                {
                    "tableID": tableID,
                    "nome": nome,
                    "cpf": cpf,
                    "desejo": desejo,
                    "response": "Nome inválido",
                }
            )

        elif func.invalidCpf(cpf):
            return jsonify(
                {
                    "tableID": tableID,
                    "nome": nome,
                    "cpf": cpf,
                    "desejo": desejo,
                    "response": "Tudo Certo",
                }
            )  # f"Funcionou {nome}!"

        elif verifyGuest(cpf, tableID):
            return jsonify(
                {
                    "tableID": tableID,
                    "nome": nome,
                    "cpf": cpf,
                    "desejo": desejo,
                    "response": "Você já está cadastrado",
                }
            )

        elif func.invalidValue(desejo, 5, 255):
            return jsonify(
                {
                    "tableID": tableID,
                    "nome": nome,
                    "cpf": cpf,
                    "desejo": desejo,
                    "response": "Desejo inválido",
                }
            )

        cursor.execute(
            f"INSERT INTO \"{tableID}\" (nome,cpf,desejo) VALUES ('{nome}', '{cpf}', '{desejo}');"
        )
        conn.commit()
        return jsonify(
            {
                "tableID": tableID,
                "nome": nome,
                "cpf": cpf,
                "desejo": desejo,
                "response": "200",
            }
        )  # f"Funcionou {nome}!"

    except Exception as e:
        return f"Aconteceu algum erro:\n{e}"


@app.route("/games/create/", methods=["POST"])
def createTable():
    requerimento = request.get_json()

    try:
        hostcpf = requerimento["cpf"]
        tableID = requerimento["tableID"]
        nome = requerimento["nome"]
        desejo = requerimento["desejo"]
        resposta = requerimento["response"]

        if func.invalidCpf(func.limpar_cpf(hostcpf)):
            return jsonify(
                {
                    "tableID": tableID,
                    "nome": nome,
                    "cpf": hostcpf,
                    "desejo": desejo,
                    "response": "cpf inválido!",
                }
            )

        key = func.gerarID()
        while True:
            cursor.execute(f"select id_table from hosts where id_table='{key}'")
            if cursor.fetchone() is None:
                break
            key = func.gerarID()

        cursor.execute(
            f"INSERT INTO hosts values('{func.limpar_cpf(hostcpf)}', '{key}')"
        )
        conn.commit()

        cursor.execute(
            f'CREATE TABLE "{key}"(id_guest serial not null,'
            + "nome varchar(25) not null,amigosecretoid int,cpf varchar(14),"
            + "desejo varchar(255),"
            + "primary key(id_guest))"
        )
        conn.commit()
        return jsonify(
            {
                "tableID": key,
                "nome": nome,
                "cpf": hostcpf,
                "desejo": desejo,
                "response":"200",
            }
        )
    except Exception as e:
        return jsonify(
            {
                "tableID": tableID,
                "nome": nome,
                "cpf": hostcpf,
                "desejo": desejo,
                "response": f"Erro:{e}",
            }
        )


@app.route("/games/sortition/verify", methods=["POST"])
def verifySortition():
    requerimento = request.get_json()

    hostcpf = requerimento["cpf"]
    tableID = requerimento["tableID"]
    nome = requerimento["nome"]
    desejo = requerimento["desejo"]
    resposta = requerimento["response"]

    if (not func.invalidCpf(func.limpar_cpf(hostcpf)) and verificarTableID(tableID)):
        if sorteioAconteceu(tableID):
            cursor.execute(
                f"select amigosecretoid from \"{tableID}\" where cpf ='{hostcpf}'"
            )
            resultado = cursor.fetchone()[0]

            cursor.execute(
                f"select nome from \"{tableID}\" where id_guest = {resultado}"
            )
            nomeSorteado = cursor.fetchone()[0]

            cursor.execute(
                f"select desejo from \"{tableID}\" where id_guest = {resultado}"
            )
            desejoSorteado = cursor.fetchone()[0]

            cursor.execute(
                f"select desejo from \"{tableID}\" where cpf ='{hostcpf}'"
            )
            desejo = cursor.fetchone()[0]

            return criarJson(tableID,hostcpf,nomeSorteado,desejoSorteado,"200")
        else:
            return criarJson(tableID,hostcpf,nome,desejo,"O sorteio não aconteceu!")
    return criarJson(tableID,hostcpf,nome,desejo,"TableID ou CPF errado!")


@app.route("/games/sortition/start", methods=["POST"])
def requerirSorteio():
    requerimento = request.get_json()

    hostcpf = requerimento["cpf"]
    tableID = requerimento["tableID"]
    nome = requerimento["nome"]
    desejo = requerimento["desejo"]
    resposta = requerimento["response"]

    if(verificarTableID(tableID)):
        cursor.execute(f"select cpf from hosts where cpf = '{hostcpf}'")
        try:
            resultado = cursor.fetchone()[0]
            if resultado is not None:
                realizarSorteio(tableID)
                return criarJson(tableID,hostcpf,nome,desejo,"Sorteio Realizado!")
            else:
                return criarJson(tableID,hostcpf,nome,desejo,"Apenas o Host pode realizar o sorteio.")
        except Exception:
            return criarJson(tableID,hostcpf,nome,desejo,"Apenas o Host pode realizar o sorteio.")

    else:
        return criarJson(tableID,hostcpf,nome,desejo,"ID do jogo não encontrada.")


@app.route("/games/requere", methods=["POST"])
def requerirsessoes():
    requerimento = request.get_json()

    hostcpf = requerimento["cpf"]
    lista = requerimento["sessoes"]
    resposta = requerimento["response"]

    if(not func.invalidCpf(func.limpar_cpf(hostcpf))):
        if(verificarHost(hostcpf)):
            cursor.execute(f"select id_table from hosts where cpf = '{hostcpf}'")
            resposta = cursor.fetchall()
            resposta = func.matrizParray(resposta)
            return jsonify({
            "cpf": hostcpf,
            "sessoes": resposta,
            "response": "200"
            })
        return jsonify({
            "cpf": hostcpf,
            "sessoes": lista,
            "response": "002"
            })        
    else:
        return jsonify({
            "cpf": hostcpf,
            "sessoes": lista,
            "response": "001"
        })
#001-cpf/002-nHost

def sorteioAconteceu(tableid=str()):
    if not verificarTableID(tableid):
        return False
    try:
        cursor.execute(
            f'select amigosecretoid from "{tableid}" where id_guest = (select min(id_guest) from "{tableid}")'
        )
        amigosecretoid = cursor.fetchone()[0]
        if amigosecretoid is None:
            return False
        return True
    except:
        return False


def verifyGuest(cpf=str(), tableID=str()):
    tableID = f'"{tableID}"'
    cursor.execute(f"select cpf from {tableID} where cpf='{cpf}'")
    resultado = cursor.fetchone()
    if resultado is not None:
        return True
    return False


def verificarTableID(tableID=str()):
    cursor.execute(f"select id_table from hosts where id_table = '{tableID}'")
    try:
        resultado = cursor.fetchone()[0]
        return True
    except Exception:
        return False


def realizarSorteio(tableID=str()):
    # Executando a consulta SQL
    cursor.execute(f'select id_guest from "{tableID}"')
    resultados = cursor.fetchall()

    lista_participantes = []

    # add na lista participante
    for i in resultados:
        lista_participantes.append(int(i[0]))


    lista_sorteio = func.sorteio(lista_participantes)

    for i in range(len(lista_sorteio)):
        cursor.execute(f'UPDATE "{tableID}" SET amigosecretoid = {lista_sorteio[i]} WHERE id_guest = {lista_participantes[i]};')
        conn.commit()
    
    return lista_sorteio


def verificarHost(cpf=str()):
    try:
        cursor.execute(f"select cpf from hosts where cpf = '{cpf}'")
        resposta = cursor.fetchone()[0]
        return True
    except Exception:
        return False
        
app.run(port=5000, host="localhost", debug=True)

cursor.close()
conn.close()
