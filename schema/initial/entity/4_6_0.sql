create table co_asset
(
    id                varchar(36) not null,
    color_others      varchar(50),
    color_primary     varchar(7),
    context           varchar(100),
    created           datetime(6) not null,
    eol               datetime(6),
    file_size         bigint      not null,
    lqip              longtext,
    modified          datetime(6) not null,
    modified_by       varchar(36),
    original_filename varchar(255),
    reference_hash    varchar(64),
    reference_url     varchar(2000),
    resolution_height integer,
    resolution_width  integer,
    system_ref_id     varchar(100),
    type              varchar(10) not null,
    url_path          varchar(500),
    primary key (id)
) engine = InnoDB;

create table co_asset_keyvalue
(
    asset_id    varchar(36)  not null,
    field_value varchar(255) not null,
    field_key   varchar(50)  not null,
    primary key (asset_id, field_key)
) engine = InnoDB;

create index idx_asset_reference_hash on co_asset (reference_hash);
create index idx_asset_context on co_asset (context);
create index idx_asset_systemrefid on co_asset (system_ref_id);
create index idx_asset_keyvalue_asset on co_asset_keyvalue (asset_id);
create index idx_asset_keyvalue_value on co_asset_keyvalue (field_key, field_value);

alter table co_asset_keyvalue
    add constraint fk_asset_keyvalue__asset
        foreign key (asset_id)
            references co_asset (id);
