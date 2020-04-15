# CAEd

Projeto desenvolvido como parte dos requisitos do processo seletivo
para a vaga de Desenvolvimento de Sistemas Web da CAEd/UFJF.

## Descrição do problema
Após a aplicação de uma determinada avaliação (ex. ENEM) os cadernos de
prova respondidos pelos estudantes entram no processo de correção.
Neste processo, os itens objetivos são corrigidos automaticamente
enquanto que os discursivos precisam ser distribuídos para uma equipe
de corretores corrigir item por item.

A fim de facilitar este processo, o time de desenvolvimento de software
backend foi chamado para criar a POC de uma API que tratasse algumas
regras de correção dos itens discursivos. Para tanto, temos a seguintes
requisitos:

1. Esta POC está considerando a situação de apenas UM usuário acessando
a aplicação, portanto não é preciso se preocupar com as questões de
multi-usuários (ou multicorretores).
2. O usuário/corretor solicita os itens de correção sempre 1 de cada
vez. Existe uma propriedade ordem que precisa ser seguida no retorno de
cada item.
3. Não é permitido corrigir um item fora da ordem definida. Ex. Se o
corretor corrigiu apenas o item de ordem 1, ele não poderá salvar um
item de ordem 3 antes de salvar o de ordem 2.
4. Todo item foi distribuído com as suas opções de correção (que
chamamos de chave de correção).
5. Não é permitido corrigir um item com um valor fora da chave de
correção informada.
6. Caso o item apresentado contenha algum problema que impossibilite
sua correção, o corretor pode marcar a correção específica como COM
DEFEITO.
7. É possível reservar (pular) um item específico para correção
posterior. O exemplo da regra 3 também vale para esta regra. Se o
último item corrigido for o item de ordem 3 o corretor poderá reservar
somente o item de ordem 4.
8. É possível buscar todos os itens reservados e corrigi-los na ordem
que o corretor achar necessário.
9. Quando não houver mais itens disponíveis para correção, a API deverá
retornar os itens reservados até que eles se esgotem. Se não houver
itens DISPONÍVEIS nem RESERVADOS, a API deverá alertar o corretor.
10. As situações de um item de correção pode ser: DISPONIVEL ->
RESERVADA -> CORRIGIDA | COM_DEFEITO

## Como executar

## Tomadas de decisão

Nem todas as regras de negócio ficaram claras no documento, então tomei
a liberdade de tomar algumas decisões, baseadas na minha 
própria compreensão do problema. Vou descrever aqui quais foram elas.

 * Um item pode ter mais de uma chave de correção (mais de uma questão
 a ser corrigida), mas a especificação da API parece exigir que apenas
 uma chave seja recebida por vêz. O que deveria acontecer com um item
 parcialmente corrigido?
   * Na minha compreensão, o item deveria ser marcado automaticamente
   como reservado, para que o corretor possa voltar ao item quando
   desejasse, e pensando em um cenário multi-corretores, para que ele
   a partir desse momento se responsabilize pela correção do restante
   daquele item.
   
 * Na regra #7 do documento, é descrito que um item pode ser reservado
  para correção futura, seguindo as validações de ordenação. Contudo,
  no exemplo do endpoint "Reservar correção", é esperado um payload 
  com uma chave (e o valor da correção)
    * Vou considerar como sendo um erro do documento (possivelmente
    uma cópia do endpoint anterior), uma vez que não faria sentido
    receber uma chave, se todo o item será reservado. Se apenas a
    chave devesse ser reservada, entraria em desacordo com a regra #7.
    Neste caso, vou considerar que o payload do endpoint "Reservar 
    correção" deve ser vazio.

## Comentários e explicações 

### Java vs NodeJS 

### Arquitetura

### Banco de dados

