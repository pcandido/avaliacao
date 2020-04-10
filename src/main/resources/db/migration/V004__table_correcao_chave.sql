create table correcao_chave
(
    correcao_id bigint not null references correcao (id) on update cascade on delete cascade,
    chave_id    bigint not null references chave (id) on update cascade on delete no action,
    primary key (correcao_id, chave_id)
);

insert into correcao_chave(correcao_id, chave_id)
values (9859662, 186),
       (9859663, 186),
       (9859663, 187),
       (9859664, 186),
       (9859664, 188),
       (9859665, 187),
       (9859665, 188),
       (9859666, 188);