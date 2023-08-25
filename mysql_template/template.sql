create table read_out (
    ID int auto_increment not null,
    date datetime not null,
    temperature float not null,
    humidity float not null,
    primary key (ID)
);