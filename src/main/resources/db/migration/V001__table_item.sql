create table item
(
    id          bigint      not null primary key,
    item        varchar     not null,
    referencia  varchar     not null,
    sequencial  varchar     not null,
    solicitacao varchar     not null,
    situacao    varchar(15) not null,
    ordem       bigint      not null
);

create index idx_item_ordem on item (ordem);

insert into item(id, item, referencia, sequencial, solicitacao, situacao, ordem)
values (9859662, 'D020006H6', 'upload/correcao_9859662.png', '68300003130128', '2000000885', 'DISPONIVEL', 1),
       (9859663, 'D320006C8', 'upload/correcao_9859663.png', '68300003130127', '2000000886', 'DISPONIVEL', 2),
       (9859664, 'D6530006C7', 'upload/correcao_9859664.png', '68300003130128', '2000000886', 'DISPONIVEL', 3),
       (9859665, 'DA4E2FRMO', 'upload/correcao_9859665.png', '68300003130127', '2000000887', 'DISPONIVEL', 4),
       (9859666, 'D5N92DB2G', 'upload/correcao_9859666.png', '68300003130128', '2000000887', 'DISPONIVEL', 5);
