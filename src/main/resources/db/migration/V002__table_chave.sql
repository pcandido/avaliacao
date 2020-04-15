create table chave
(
    id     bigint  not null primary key,
    titulo varchar not null
);

insert into chave(id, titulo)
values (186, 'Titulo 1'),
       (187, 'Titulo 2'),
       (188, 'Titulo 3');