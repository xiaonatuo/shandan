-- 20210813
alter table "BIANMU"."B_METADATA_BASIC" modify "SECRET_LEVEL" INTEGER;
alter table "BIANMU"."B_METADATA_BASIC" alter column "SECRET_LEVEL" set default (0);
comment on column "BIANMU"."B_METADATA_BASIC"."SECRET_LEVEL" is '数据密级(0:公开, 1:内部, 2:秘密, 3:机密, 4绝密)';