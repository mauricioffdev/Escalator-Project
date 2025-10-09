Escalator: Gerador de Padrões e Análise Harmônica para Guitarristas
O Escalator é um projeto de linha de comando para o treino de escalas e técnica musical. Como teste inicial, ele permite ao usuário escolher entre escalas maiores e suas relativas menores, gerando a escala menor natural correspondente.

Atualmente, o sistema oferece dois tipos de padrões de estudo:

Sequência de 3 notas: Um padrão para o treino de agilidade.

Tríades Diatônicas: Gerando o campo harmônico por tríades para cada grau da escala, ideal para o estudo da harmonia.

Um detalhe importante é que o Escalator respeita as regras da teoria musical em relação ao uso de sustenidos (#) e bemóis (b), garantindo que a notação esteja sempre correta para cada ciclo de tonalidades.

No futuro, pretendo adicionar tétrades e outras escalas básicas, como a pentatônica menor e a pentatônica blues.

Tecnologias
Linguagem: Java 25

IDE: IntelliJ

### Como Rodar o Projeto

#### Pré-requisitos

Certifique-se de ter o **JDK (Java Development Kit)** instalado em sua máquina.

#### Via Linha de Comando (Bash ou Terminal)

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/mauricioffdev/Escalator-Project.git](https://github.com/mauricioffdev/Escalator-Project.git)
    cd Escalator-Project
    ```
2.  **Compile o código:**
    ```bash
    mvn clean compile
    ```
3.  **Execute o programa:**
    ```bash
    mvn exec:java -Dexec.mainClass="br.com.escalator.App"
    ```

### Estrutura do Código

O projeto segue a arquitetura de pacotes padrão Java (`br.com.escalator`):

| Pacote | Classes | Descrição |
| :--- | :--- | :--- |
| `br.com.escalator` | `App.java` | Contém o método `main()`, o loop principal e a interação com o usuário (`Scanner`). |
| `br.com.escalator.model` | `Nota.java` (Enum) | Define as 12 notas cromáticas e lida com a lógica modular de semitons. |
| | `Escala.java` | Define a estrutura de uma escala (seus intervalos) e calcula as notas reais. |
| `br.com.escalator.service` | `GeradorDePadroes.java` | Contém a lógica de treino: geração de sequências de N notas e sequências de tríades. |

Observação
Este projeto foi feito com a ajuda do Gemini para resolver erros e implementar a lógica de forma otimizada. Os comentários no código-fonte foram mantidos como material de estudo e para facilitar o entendimento de cada etapa.

Desenvolvido por Maurício Filadelfo Filho.
