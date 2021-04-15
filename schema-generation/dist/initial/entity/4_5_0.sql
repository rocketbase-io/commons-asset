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
    asset_id         varchar(36) not null,
    field_key        varchar(50) not null,
    field_value      longtext    not null,
    field_value_hash varchar(64) not null,
    lastUpdate       datetime(6),
    primary key (asset_id, field_key)
) engine = InnoDB;

create index idx_asset_reference_hash on co_asset (reference_hash);
create index idx_asset_context on co_asset (context);

alter table co_asset
    add constraint uk_asset_system_ref_if unique (system_ref_id);

create index idx_asset_keyvalue_asset on co_asset_keyvalue (asset_id);
create index idx_asset_keyvalue_key on co_asset_keyvalue (field_key);
create index idx_asset_keyvalue_keyhash on co_asset_keyvalue (field_key, field_value_hash);

alter table co_asset_keyvalue
    add constraint fk_asset_keyvalue__asset
        foreign key (asset_id)
            references co_asset (id);
