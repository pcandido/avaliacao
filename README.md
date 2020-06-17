# Avaliações

Os requisitos desse projeto foram extraídos de um edital de seleção.

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

O projeto está preparado para ser nos três principais sistemas operacionais:
Linux, OSX e Windows.

Existem 3 formas de se executar o projeto, mas cada uma tem suas 
dependências:
 1. A partir do código fonte _(pré-requisito: java)_: o projeto está
 sendo versionado com dois scripts executáveis (para disferentes 
 sistemas operacionais): `mvnw` e `mvnw.cmd`. Esses scripts possuem
 uma versão portável do maven, que pode ser utilizado para buildar o
 projeto. Basta executar na raiz do projeto: `./[mvnw|mvnw.cmd] package`.
 Será gerado o diretório target, e dentro dele será possível encontrar o jar.
 Para executar o jar, basta usar: `java -jar ./target/avaliacao-<version>.jar`.
 O comando `package` automaticamente executa os testes antes do build,
 mas é possível executar os testes de forma separada usando: 
 `./[mvnw|mvnw.cmd] test`.
 
 2. Construir uma imagem docker a partir do `Dockerfile` _(pré-requisito:
 docker)_: o projeto está sendo versionado com o Dockerfile. A partir dele é
 possível construir uma nova imagem docker para então executar o projeto.
 Para construir uma imagem, utilizar: `docker build -t nome_imagem .`.
 Para executar a imagem recem construída, basta utilizar: `docker run
 -p 8080:8080 nome_imagem`.
 
 3. Executar a partir de uma imagem já construída _(pré-requisito:
 docker)_: O processo de CI/CD (explicado abaixo) automatiza o build
 da imagem docker e o seu deploy (push) para o registro de containers
 DockerHub. Então é possível executar o projeto sem precisar do código
 fonte com: `docker run -p 8080:8080 pcandido/avaliacao`
 
Em todos os casos, a API estará disponível para consumo na porta `8080`.

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

## Arquitetura

A aplicação foi dividida em camadas lógicas que dividem as 
responsabilidades e minimiza o acoplamento, o que resulta em um maior
índice de manutenabilidade. As camadas são:
 * Model - responsável pode definir o modelo de dados do domínio,
 quais atributos cada propriedade terá e os relacionamentos entre
 diferentes objetos. Uma vez utilizando JPA (Java Persistence API) e
 um ORM vinculado (Hibernate), o modelo (e suas anotações) também é
 responsável pelo mapeamento objeto-relacional. Além das anotações
 relativas ao ORM, anotações para validação automatizada também foram
 adicionadas.
 * Repository - responsável pela persistência e recuperação de dados.
 Boa parte do trabalho dessa camada foi transferida para o ORM. Veja
 que através do JPA, é possível fazer consultas personalizadas apenas
 definindo o nome de um método abstrato.
 * Controller - responsável por receber e responder as requisições 
 REST. Qualquer trabalho relativo a interpretar ou formatar dados de
 requisições deve ser feito pelo Controller, mas não deve processar
 regras de negócio, para isso deve usar o Service. 
 * Service - responsável por executar toda a regra de negócio. O uso
 mais comum é receber uma ação do controller, processar a regra de
 negócio, chamar o repository quando necessário, e responder de volta
 ao controller. Veja que apesar de receber dados do controller, não
 deve lidar com dados de requisição, deve receber do controller dados
 já parseados para o domínio da aplicação (model). 
 
Além das camadas apresentadas, existe um pacote destinado a declarar e
manipular exceções que são disparadas (muitas vezes propositalmente)
pela aplicação. As exceções que não forem tratadas até o fim da 
execução do controller são enviadas para este handler que irá formatar
uma resposta REST baseda na exceção. Lidar corretamente com exceções
permite ao projeto gerar um fluxo próprio de erros e tratamento dos 
mesmos. 

## Testes

Foram implementados testes unitários para a camada de serviço e testes
de aceitação/contrato. Normalmente eu implementaria testes unitários ou de 
integração para a camada de controllers, mas não o fiz pois a camada
contoller deste projeto ficou muito simples, apenas redirecionando o
fluxo para os serviços. A camada repository também ficou muito simples,
mas em alguns casos, é interessante aplicar testes de integração 
(repository -> database), principalmente quando tempos HQLs ou raw
queries. Não tenho o costume de criar testes para a camada de modelo.

## CI/CD

Para integração, build e deploy, foi usado o CI/CD do bitbucket. A
configuração é feita pelo arquivo `bitbucket-pipelines.yml` e é
executado automaticamente a cada push no repositório.
O pipeline configurado realizará os seguintes passos:
 * `mvn -B test package`
   * Ao executar o maven, ele irá baixar todas as dependências,
   executar os testes unitários/de aceitação, e empacotar o aplicativo
   em um arquivo `.jar`
 * `docker login -u $DOCKER_HUB_USER -p $DOCKER_HUB_PASSWORD`
   * Esse passo autentica o serviço DockerHub, para permitir push
   da imagem docker. O nome de usuário e senha são armazenados fora
   do script em um local seguro.
 * `docker build -t pcandido/avaliacao .`
   * A imagem é construída a partir do `Dockerfile`
 * `docker push pcandido/avaliacao`
   * A imagem recem construída é enviada para o DockerHub, 
   concretizando o passo de deploy

## Comentários

### Complexidade

Teria sido possível criar esse pequeno projeto de forma muito mais
simples, entretanto, estou o utilizando para mostrar meus conhecimentos
que podem ser aplicados em um projeto real e robusto. A complexidade 
inerente a minha decisão é bem maior do que o esperado para um projeto
desse porte, mas é o básico para um projeto que se pretende escalar.

### Banco de dados

Para simplificar a distribuição, estou utilizando um banco de dados em
memória (H2), para evitar qualquer dependência de um SGDB externo
(mesmo utilizando docker).