drop table if exists comments;
drop table if exists books;


create table if not exists books (
                                     id identity primary key,
                                     title varchar(255) not null,
    author varchar(255) not null,
    pub_year int not null
    );

create table if not exists comments (
                                        id identity primary key,
                                        book_id bigint not null,
                                        author varchar(64) not null,
    text varchar(1000) not null,
    created_at timestamp not null default current_timestamp,
    constraint fk_book foreign key (book_id) references books(id) on delete cascade
    );

INSERT INTO books (title, author, pub_year) VALUES ('Кобзар', 'Тарас Шевченко', 1840);
INSERT INTO books (title, author, pub_year) VALUES ('1984', 'Джордж Оруелл', 1949);
INSERT INTO books (title, author, pub_year) VALUES ('Володар перснів', 'Дж. Р. Р. Толкін', 1954);
