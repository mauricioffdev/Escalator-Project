# 🎸 Escalator: Gerador de Padrões e Análise Harmônica para Guitarristas

[![Java](https://img.shields.io/badge/java-25-blue.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)

O **Escalator** é um projeto de linha de comando para o treino de escalas e técnica musical. Como teste inicial, ele permite ao usuário escolher entre escalas maiores e suas relativas menores, gerando a escala menor natural correspondente.

Atualmente, o sistema oferece três tipos de padrões de estudo:

- 🎵 **Sequência de 3 notas:** Um padrão para o treino de agilidade.
- 🎶 **Tríades Diatônicas:** Geração do campo harmônico por tríades para cada grau da escala, ideal para o estudo da harmonia.
- 🎸 **Tablatura de Escalas Maiores e Menores:** Impressão visual do padrão da escala maior ou menor em formato de tablatura para facilitar o treino em instrumentos de cordas.

Um detalhe importante é que o Escalator respeita as regras da teoria musical em relação ao uso de **sustenidos (#)** e **bemóis (b)**, garantindo que a notação esteja sempre correta para cada ciclo de tonalidades.

No futuro, pretendo adicionar **tétrades**, **pentatônica menor**, **pentatônica blues** e outros padrões comuns de improvisação.

---

## 🛠 Tecnologias

- **Linguagem:** Java 25 LTS ☕
- **IDE:** IntelliJ IDEA 💻

---

## ▶️ Como Rodar o Projeto

### Pré-requisitos

Certifique-se de ter o **JDK (Java Development Kit)** instalado em sua máquina.

---

### Via Linha de Comando (Bash ou Terminal)

1. **Clone o repositório:**
    ```bash
    git clone https://github.com/mauricioffdev/Escalator-Project.git
    cd Escalator-Project
    ```

2. **Compile o código:**
    ```bash
    mvn clean compile
    ```

3. **Execute o programa:**
    ```bash
    mvn exec:java -Dexec.mainClass="br.com.escalator.App"
    ```

---

## 📂 Estrutura do Código

O projeto segue a arquitetura de pacotes padrão Java (`br.com.escalator`):

| Pacote | Classes | Descrição |
| :--- | :--- | :--- |
| `br.com.escalator` | `App.java` | Contém o método `main()`, o loop principal e a interação com o usuário (`Scanner`). |
| `br.com.escalator.model` | `Nota.java` (Enum) | Define as 12 notas cromáticas e lida com a lógica modular de semitons. |
|  | `Escala.java` | Define a estrutura de uma escala (seus intervalos) e calcula as notas reais. |
| `br.com.escalator.service` | `GeradorDePadroes.java` | Contém a lógica de treino: geração de sequências de N notas e sequência de tríades. |
|  | `GeradorDeTablatura.java` | Gera a visualização da escala maior ou menor em formato de **tablatura para guitarra**, respeitando as posições por corda e traste. Ideal para estudo visual da escala. |

---

## 🧪 Exemplo de Saída

```text
--- Escalator: Gerador de Padrões de Guitarra (Escalas Diatônicas) ---

-------------------------------------------
Escolha o Modo de Estudo:
1 - Escala Maior
2 - Escala Menor Natural (Relativa)
Sua escolha: 1

-------------------------------------------
Notas disponíveis (Ciclos):
Sustenidos: [C, G, D, A, E, B, F#, C#]
Bemóis:     [F, Bb, Eb, Ab, Db, Gb, Cb]
Digite a Tônica MAIOR (ex: C, Bb, A) ou 'Sair': G

-------------------------------------------
Opções de Padrão para G Maior:
1 - Padrão Sequência de 3 Notas
2 - Sequência de Tríades (Campo Harmônico)
3 - 3 Notas por Corda (Tablatura)
Escolha uma opção: 3

----------------- Saída para G Maior -----------------
Notas: [G, A, B, C, D, E, F#]

PADRÃO ESCOLHIDO: 3 Notas por Corda
E |-----------------------------|---------------------5--7--8-|
B |-----------------------------|-----------5--7--8-----------|
G |-----------------------------|--4--5--7--------------------|
D |--------------------4--5--7--|-----------------------------|
A |-----------3--5--7-----------|-----------------------------|
E |--3--5--7--------------------|-----------------------------|

-------------------------------------------
Pressione ENTER para escolher outra escala ou digite 'Sair'.

💡 Observação

Este projeto foi feito com a ajuda do Gemini para resolver erros e implementar a lógica de forma otimizada. 
Os comentários no código-fonte foram mantidos como material de estudo e para facilitar o entendimento de cada etapa.

👨‍💻 Desenvolvido por

Maurício Filadelfo Filho
