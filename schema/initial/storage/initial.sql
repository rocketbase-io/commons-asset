create table co_asset_file
(
    id          varchar(36) not null,
    file_binary longblob    not null,
    primary key (id)
) engine = InnoDB;
