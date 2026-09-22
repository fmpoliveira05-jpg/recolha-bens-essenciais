# Manual de utilização

A aplicação corre na consola e funciona por menus numerados: escreve-se o número da opção e carrega-se em Enter. Os valores inválidos são recusados e a pergunta repete-se.

## Primeiro contacto (2 minutos)

1. `1 – Carregar dados` → `1 – Ficheiros locais`. A aplicação mostra quantos registos importou e quantos foram rejeitados.
2. `7 – Ver resumo da instituição` para ver as caixas e o estado de cada contentor (percentagem de ocupação).
3. `5 – Gerar rotas de recolha`. Aparece, para cada veículo, o percurso (`Base → CAIXF38 → CAIXF42 → Base`), a distância, o tempo e as trocas de contentores em cada paragem. No fim vem o resumo do plano.
4. `8 – Ver alertas` para ver os dados que foram ignorados e porquê.

Os dados só podem ser carregados uma vez por sessão, para não duplicar leituras.

## Menu principal

| Opção | Para que serve |
|---|---|
| 1 – Carregar dados | importa os ficheiros JSON da pasta `data/` (ou a que for indicada ao arrancar) ou tenta a Web API |
| 2 – Veículos | adicionar, ativar, desativar e listar veículos |
| 3 – Caixas, contentores e leituras | criar caixas, instalar contentores e registar leituras à mão |
| 4 – Distâncias | registar distâncias entre a base e uma caixa, ou entre duas caixas |
| 5 – Gerar rotas de recolha | cria um novo mapa de recolha com os dados atuais |
| 6 – Ver último mapa de recolha | volta a mostrar o último plano gerado |
| 7 – Ver resumo da instituição | caixas, contentores e ocupação |
| 8 – Ver alertas | registos inválidos, com a data e o conteúdo original |
| 0 – Sair | termina a aplicação |

## Experimentar as regras

Algumas experiências que ajudam a perceber o comportamento do planeamento:

- **Desativar veículos** (menu 2 → 2) e voltar a gerar rotas: com menos veículos, alguns contentores passam a aparecer como "por recolher" no relatório.
- **Registar uma leitura alta** (menu 3 → 3) num contentor de roupa, por exemplo 95 % da capacidade: na geração seguinte ele passa a fazer parte de uma rota.
- **Registar uma leitura acima da capacidade**: é recusada com uma mensagem a explicar porquê.

## Ficheiros de dados

A pasta `data/` tem seis ficheiros, um por conjunto de dados:

| Ficheiro | Conteúdo |
|---|---|
| `types.json` | tipos de contentor existentes |
| `containers.json` | todos os contentores (código, tipo e capacidade em kg) |
| `aidBoxes.json` | caixas e os códigos dos contentores instalados em cada uma |
| `distances.json` | distâncias (m) e durações (min) entre caixas e até à `Base` |
| `vehicles.json` | veículos e número de contentores de cada tipo que transportam |
| `readings.json` | leituras dos sensores (código do contentor, data e kg) |

Os contentores que estão em `containers.json` mas não pertencem a nenhuma caixa são considerados contentores vazios de reserva, guardados na base, e são eles que os veículos levam para as trocas.
