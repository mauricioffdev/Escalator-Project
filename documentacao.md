Documentação Técnica do Projeto Escalator
Este documento detalha as principais tecnologias, conceitos de programação e estruturas de dados utilizados no desenvolvimento do Escalator, um sistema para treino de escalas e harmonia musical em Java.

1. Conceitos de Orientação a Objetos (POO)
   O projeto foi construído com base em princípios de POO para garantir modularidade e organização.

Classes: O código é organizado em classes com responsabilidades bem definidas:

App: A classe principal, responsável pela interação com o usuário, menus e orquestração das operações.

Escala: Uma classe de "modelo" que define as propriedades de uma escala (nome e intervalos) e a lógica para calcular as notas.

GeradorDePadroes: Uma classe de "serviço" que encapsula a lógica de negócio para gerar padrões de treino (sequências e tríades).

Nota (Enum): Uma classe especial que representa um conjunto fixo de constantes (as 12 notas musicais), garantindo a integridade dos dados.

Encapsulamento: As classes ocultam seus dados internos e expõem apenas a funcionalidade necessária. Por exemplo, a classe Escala tem um array de intervalos privado, e o acesso a ele é feito por meio do método público calcularNotas(). Isso impede que o estado interno do objeto seja modificado acidentalmente.

Métodos e Atributos Estáticos: Foram usados para funcionalidades que não precisam de uma instância de objeto:

Atributos Estáticos (static): Variáveis como CICLO_SUSTENIDOS_EXIBICAO e TONICAS_MAIORES_DE_BEMOL são estáticas porque são constantes e compartilhadas por todas as partes do programa, sem a necessidade de criar um objeto App.

Métodos Estáticos (static): Métodos como coletarTonica() e isTonicaDeBemol() são estáticos porque realizam uma tarefa que não depende do estado de um objeto específico, podendo ser chamados diretamente da classe App.

2. Estruturas de Dados e Coleções
   O projeto faz uso de coleções de dados para armazenar e manipular informações de forma eficiente.

Arrays ([]): Usados para armazenar sequências de valores de um tipo específico. Por exemplo, o int[] intervalos da classe Escala define os intervalos de semitons de uma escala, e os String[] são usados para armazenar os nomes dos tons nos ciclos.

Listas (List): A java.util.List foi amplamente utilizada para coleções dinâmicas de objetos, como a lista de Notas da escala calculada.

ArrayList: A implementação de lista mais comum, usada para armazenar as notas de uma escala (List<Nota>) e as sequências de padrões (List<String>), pois permite adicionar e acessar elementos facilmente.

stream: O uso de stream() para mapear e coletar dados (.map().collect(Collectors.toList())) é uma forma moderna e funcional de processar coleções em Java.

Mapas (Map): A java.util.Map foi fundamental para armazenar pares de chave-valor e mapear notações musicais.

Map.of(): Usado para criar mapas imutáveis de forma concisa, como BEMOL_TO_SHARP, garantindo que os dados não sejam alterados após a inicialização.

getOrDefault(): Um método seguro para buscar valores, retornando um valor padrão se a chave não for encontrada, evitando erros de NullPointerException.

3. Sintaxe e Recursos da Linguagem
   Enums: A classe Nota foi implementada como um enum, a estrutura ideal para um conjunto de constantes fixo. O enum facilita o uso e a validação das notas, além de ser facilmente convertível para um valor numérico modular (0-11) para cálculos.

Controle de Fluxo:

Laços de Repetição (do-while, for): O do-while principal mantém o programa rodando até que o usuário decida sair, enquanto os laços for são usados para iterar sobre as notas de uma escala e gerar os padrões.

Estruturas Condicionais (if, else if, switch): Usadas para direcionar a lógica do programa, como na escolha do modo (Maior ou Menor) e na seleção da opção de padrão de treino.

Manipulação de Strings: Métodos como toUpperCase(), replace(), contains() e String.format() foram usados para processar e formatar a entrada do usuário e a saída do console, tornando a interação mais intuitiva e as saídas mais legíveis.

Tratamento de Exceções (try-catch): O bloco try-catch é usado para lidar com o erro de IllegalArgumentException quando o usuário digita uma nota inválida, garantindo que o programa não pare de forma abrupta.