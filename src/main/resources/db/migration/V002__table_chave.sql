create table chave
(
    id     bigint  not null primary key,
    titulo varchar not null
);

insert into chave(id, titulo)
values (186, 'Chave de correção 1'),
       (187, 'Chave de correção 2'),
       (188, 'Chave de correção 3');