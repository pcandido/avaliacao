create table opcao
(
    id        bigint  not null primary key,
    valor     varchar not null,
    descricao varchar not null,
    chave_id  bigint  not null references chave (id) on update cascade on delete cascade
);

insert into opcao(id, valor, descricao, chave_id)
values (1, '0', 'Certo', 186),
       (2, '1', 'Errado', 186),
       (3, '0', 'Lido', 187),
       (4, '1', 'Não lido', 187),
       (5, '0', '0', 188),
       (6, '1', '1', 188),
       (7, '2', '2', 188),
       (8, '3', '3', 188),
       (9, '4', '4', 188),
       (10, '5', '5', 188);