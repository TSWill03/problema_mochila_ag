# Problema da Mochila 0/1 com Algoritmo Genético (Java)

Implementação do **Problema da Mochila 0/1 (Knapsack 0/1)** utilizando **Algoritmos Genéticos (AG)** em **Java**, com leitura de múltiplas instâncias a partir de uma pasta (`.txt`) e geração automática de resultados em **CSV** (para plot no Excel) e em **TXT** (melhor solução encontrada).

## Visão Geral

O problema da mochila 0/1 é um problema clássico de **otimização combinatória**:

- Existem `N` itens, cada um com **peso** e **valor**
- Existe uma mochila com **capacidade máxima**
- Cada item pode ser escolhido **0 ou 1 vez**
- Objetivo: **maximizar o valor total** sem exceder o peso total

A solução é representada como um **cromossomo binário**:
- `1` = item selecionado
- `0` = item não selecionado

Exemplo: `1010110` significa selecionar os itens 1, 3, 5 e 6.

## Estrutura do Algoritmo Genético

O AG segue o fluxo padrão:

1. **População inicial** aleatória (tipicamente 50–100 indivíduos)
2. **Avaliação / Fitness** de cada indivíduo
3. **Seleção por roleta viciada** (fitness proporcional)
4. **Crossover de 1 ponto** (gerando 2 filhos)
5. **Mutação (flip de bit)** com baixa probabilidade
6. **Elitismo**: copia o melhor indivíduo para a próxima geração
7. Repete por `G` gerações

## Função Fitness

A avaliação calcula:

- `pesoTotal` e `valorTotal` do cromossomo
- Se `pesoTotal <= capacidade`: **fitness = valorTotal**
- Se `pesoTotal > capacidade`: aplica penalização

O projeto suporta duas abordagens (dependendo do que você configurou no `evaluateRaw`):

### (A) Penalização por excesso (recomendado para convergência)
`fitness = valor - penaltyFactor * excesso`

### (B) Penalização rígida (conforme diagrama)
Se exceder, define:
`fitness = 1`

> Observação: a penalização rígida pode achatar a busca (todas inválidas ficam iguais),
> então pode exigir mais gerações/ajustes.

## Entrada (arquivos .txt)

O programa lê **todos os arquivos `.txt`** de uma pasta (recursivo).  
Formato por arquivo:

1. Primeira linha:  
`N C`  
- `N` = quantidade de itens  
- `C` = capacidade da mochila  

2. Próximas `N` linhas:  
`peso valor nome(opcional)`  
- `nome` pode ter espaços

Exemplo:

```txt
10 50
2 3 Item 1
3 4 Item 2
4 5 Item 3
...
