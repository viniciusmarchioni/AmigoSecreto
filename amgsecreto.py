import psycopg2
import random


def cortar_string(s, size):
    s = s.lower()
    if len(s) > size:
        return s[:size]
    return s


def limpar_cpf(cpf):
    cpf = cpf.replace(".", "").replace("-", "")
    return cpf


def verificar_cpf_valido(cpf):
    for caractere in cpf:
        if caractere.isalpha():
            return False

    if len(cpf) < 11 or len(cpf) > 14:
        return False

    return True


def sortear():
    conn = psycopg2.connect(
        host="host",
        database="database",
        user="user",
        password="password",
    )

    # Criar um cursor
    cursor = conn.cursor()

    consulta_sql = f"SELECT id FROM participantes;"
    cursor.execute(consulta_sql)

    # Recuperar os resultados
    resultados = cursor.fetchall()

    # Adicionar os resultados a uma lista
    lista_resultados = [resultado[0] for resultado in resultados]

    if len(lista_resultados) < 3:
        cursor.close()
        return "Nao e possivel sortear com menos de 3 participantes"

    sorteio = [0] * len(lista_resultados)
    count = 0

    while count < len(sorteio):
        value = random.randint(0, len(lista_resultados) - 1)
        if (
            lista_resultados[value] == lista_resultados[count]
            or lista_resultados[value] in sorteio
        ):
            pass
        else:
            sorteio[count] = lista_resultados[value]
            count += 1

    valor_antigo = None
    valor_novo = "valor_novo"

    for i in range(len(sorteio)):
        consulta_sql = f"UPDATE participantes SET amigosecretoid = {sorteio[i]} WHERE id = {lista_resultados[i]};"
        cursor.execute(consulta_sql)

        # Confirmar a transação
        conn.commit()

    # Fechar a conexão
    cursor.close()

    return "Sorteio realizado"


def addParticipante(nome, cpf, desejo):
    # Conectar ao banco de dados
    conn = psycopg2.connect(
        host="host",
        database="database",
        user="user",
        password="password",
    )

    nome = cortar_string(nome, 25)
    if not verificar_cpf_valido(cpf):
        return "CPF Invalido"
    cpf = limpar_cpf(cpf)
    desejo = cortar_string(desejo, 255)

    # Criando um cursor
    cur = conn.cursor()

    try:
        cur.execute(f"SELECT nome FROM participantes WHERE cpf = '{cpf}';")
        verificador = cur.fetchone()[0]
        if verificador is not None:
            return f"Você já está cadastrado {verificador}"
    except:
        pass

    # Exemplo de comando INSERT
    insert_query = f"INSERT INTO participantes (nome, cpf, desejo) VALUES ('{nome}', '{cpf}', '{desejo}');"

    # Executando o comando INSERT
    cur.execute(insert_query)

    # Commit para salvar as alterações no banco de dados
    conn.commit()

    # Fechando o cursor e a conexão
    cur.close()

    return "Participante adicionado"


def verificar_sorteio(cpf):
    cpf = limpar_cpf(cpf)
    # Conectar ao banco de dados
    conn = psycopg2.connect(
        host="isabelle.db.elephantsql.com",
        database="zlhwkfxk",
        user="zlhwkfxk",
        password="5H5djg3N01zMeTkRC3RmnZoFVo9Yia63",
    )

    cursor = conn.cursor()

    consulta_sql = f"select nome from participantes where cpf = '{cpf}'"
    cursor.execute(consulta_sql)
    resultado = cursor.fetchone()
    if resultado is None:
        return "Você não está cadastrado"

    consulta_sql = f"select nome, desejo from participantes where id = (select amigosecretoid from participantes where cpf = '{cpf}');;"
    cursor.execute(consulta_sql)

    # Recuperar os resultados
    resultado = cursor.fetchone()
    cursor.close()
    if resultado is None:
        return "O sorteio ainda não aconteceu"

    conn.close()
    return resultado[0] + " quer " + resultado[1]


sortear()
