import customtkinter
import amgsecreto as amg


def configurar_erro(botao, entrada):
    entrada.configure(
        fg_color="#FF0000", text_color="white", placeholder_text_color="white"
    )
    botao.configure(state="enable")


def cadastrar_participante():
    cadastrar_participante_btn.configure(state="disabled")
    if nome_entry.get() == "":
        configurar_erro(cadastrar_participante_btn, nome_entry)
        return
    elif cpf_entry.get() == "":
        configurar_erro(cadastrar_participante_btn, cpf_entry)
        return
    elif desejo_entry.get() == "":
        configurar_erro(cadastrar_participante_btn, desejo_entry)
        return
    else:
        if not amg.verificar_cpf_valido(cpf_entry.get()):
            cpf_entry.configure(fg_color="red")
            cadastrar_participante_btn.configure(state="enable")
            return
        else:
            texto.configure(
                text=amg.addParticipante(
                    nome_entry.get(), cpf_entry.get(), desejo_entry.get()
                )
            )


def verificar_sorteio():
    verificar_sorteio_btn.configure(state="disabled")
    if cpf_entry.get() == "":
        cpf_entry.configure(fg_color="red")
        verificar_sorteio_btn.configure(state="enable")
        return
    texto.configure(text=amg.verificar_sorteio(cpf_entry.get()))
    verificar_sorteio_btn.configure(state="enable")


janela = customtkinter.CTk()
janela.geometry("400x400")
janela.title("Amigo secreto do Vinicius")
janela.resizable(False, False)

texto = customtkinter.CTkLabel(janela, text="Amigo secreto do Vinicius")
texto.pack(padx=10, pady=10)

nome_entry = customtkinter.CTkEntry(janela, placeholder_text="Nome")
nome_entry.pack(pady=10)

cpf_entry = customtkinter.CTkEntry(janela, placeholder_text="CPF")
cpf_entry.pack(pady=10)

desejo_entry = customtkinter.CTkEntry(janela, placeholder_text="Desejo(s)")
desejo_entry.pack(pady=10)

cadastrar_participante_btn = customtkinter.CTkButton(
    janela, text="Cadastro", command=cadastrar_participante
)
cadastrar_participante_btn.pack(pady=10)

verificar_sorteio_btn = customtkinter.CTkButton(
    janela, text="Verificar sorteio", command=verificar_sorteio
)
verificar_sorteio_btn.pack(padx=10, pady=10)

janela.mainloop()
