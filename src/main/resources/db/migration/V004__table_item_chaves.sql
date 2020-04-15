create table item_chaves
(
    item_id  bigint not null references item (id) on update cascade on delete cascade,
    chave_id bigint not null references chave (id) on update cascade on delete no action,
    primary key (item_id, chave_id)
);

insert into item_chaves(item_id, chave_id)
values (9859662, 186),
       (9859663, 186),
       (9859663, 187),
       (9859664, 186),
       (9859664, 188),
       (9859665, 187),
       (9859665, 188),
       (9859666, 188);