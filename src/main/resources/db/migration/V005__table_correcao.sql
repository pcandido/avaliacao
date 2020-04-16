create table correcao
(
    id        bigint    not null auto_increment primary key,
    data_hora timestamp not null,
    item_id   bigint    not null references item (id) on update cascade on delete cascade,
    chave_id  bigint    not null references chave (id) on update cascade on delete no action,
    opcao_id  bigint    not null references opcao (id) on update cascade on delete no action
);