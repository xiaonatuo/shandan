alter table "BIANMU"."SYS_FILE" add column("MD5" VARCHAR2(150));

alter table "BIANMU"."SYS_FILE" add column("IS_CHUNK" BIT default (0));

alter table "BIANMU"."SYS_FILE" add column("IS_MERGE" BIT default (0));

alter table "BIANMU"."SYS_FILE" add column("CURRENT_CHUNK_INDEX" BIGINT);

comment on column "BIANMU"."SYS_FILE"."MD5" is '文件MD5值';

comment on column "BIANMU"."SYS_FILE"."IS_CHUNK" is '是否分片上传';

comment on column "BIANMU"."SYS_FILE"."IS_MERGE" is '是否合并';

comment on column "BIANMU"."SYS_FILE"."CURRENT_CHUNK_INDEX" is '当前分片索引';

create table "BIANMU"."SYS_FILE_CHUNK"
(
    "ID" INTEGER identity(1, 1) not null ,
    "CHUNK_NAME" VARCHAR2(150),
    "CHUNK_PATH" VARCHAR2(500),
    "CHUNK_MD5" VARCHAR2(150),
    "CHUNK_INDEX" INTEGER,
    "START_OFFSET" INTEGER,
    "END_OFFSET" INTEGER,
    "CHUNK_SUFFIX" VARCHAR2(50),
    "CHUNK_TOTAL" INTEGER,
    "FILE_ID" VARCHAR2(50),
    "FILE_MD5" VARCHAR2(150),
    "FILE_SIZE" BIGINT,
    primary key("ID")
)
    storage(initial 1, next 1, minextents 1, fillfactor 0)
;

comment on table "BIANMU"."SYS_FILE_CHUNK" is '系统文件分片表';

comment on column "BIANMU"."SYS_FILE_CHUNK"."ID" is '分片ID';

comment on column "BIANMU"."SYS_FILE_CHUNK"."CHUNK_NAME" is '分片名称';

comment on column "BIANMU"."SYS_FILE_CHUNK"."CHUNK_PATH" is '分片存储路径';

comment on column "BIANMU"."SYS_FILE_CHUNK"."CHUNK_MD5" is '分片MD5';

comment on column "BIANMU"."SYS_FILE_CHUNK"."CHUNK_INDEX" is '当前分片索引';

comment on column "BIANMU"."SYS_FILE_CHUNK"."START_OFFSET" is '当前分片起始偏移量';

comment on column "BIANMU"."SYS_FILE_CHUNK"."END_OFFSET" is '当前分片结束偏移量';

comment on column "BIANMU"."SYS_FILE_CHUNK"."CHUNK_SUFFIX" is '分片后缀';

comment on column "BIANMU"."SYS_FILE_CHUNK"."CHUNK_TOTAL" is '分片总数';

comment on column "BIANMU"."SYS_FILE_CHUNK"."FILE_ID" is '文件ID';

comment on column "BIANMU"."SYS_FILE_CHUNK"."FILE_MD5" is '文件MD5';

comment on column "BIANMU"."SYS_FILE_CHUNK"."FILE_SIZE" is '文件大小';

alter table "BIANMU"."SYS_FILE_CHUNK" add column("CREATE_USER" VARCHAR2(50));

alter table "BIANMU"."SYS_FILE_CHUNK" add column("CREATE_TIME" TIMESTAMP(6));

alter table "BIANMU"."SYS_FILE_CHUNK" add column("MODIFY_USER" VARCHAR2(50));

alter table "BIANMU"."SYS_FILE_CHUNK" add column("MODIFY_TIME" TIMESTAMP(6));