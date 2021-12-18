-- 20210813
alter table "BIANMU"."B_METADATA_BASIC" modify "SECRET_LEVEL" INTEGER;
alter table "BIANMU"."B_METADATA_BASIC" alter column "SECRET_LEVEL" set default (0);
comment on column "BIANMU"."B_METADATA_BASIC"."SECRET_LEVEL" is '数据密级(0:公开, 1:内部, 2:秘密, 3:机密, 4绝密)';
-- 20210815
CREATE TABLE "BIANMU"."SYS_OPERATE_LOG"
(
    "ID" VARCHAR2(50) NOT NULL,
    "LOGIN_NAME" VARCHAR2(50),
    "IP_ADDR" VARCHAR2(50),
    "URL" VARCHAR2(100),
    "OPERATE" VARCHAR2(50),
    "OPERATE_TIME" TIMESTAMP(6),
    "PARAMS" VARCHAR2(1000),
    CLUSTER PRIMARY KEY("ID")) STORAGE(ON "MAIN", CLUSTERBTR) ;

COMMENT ON TABLE "BIANMU"."SYS_OPERATE_LOG" IS '系统操作日志记录表';
COMMENT ON COLUMN "BIANMU"."SYS_OPERATE_LOG"."ID" IS '主键';
COMMENT ON COLUMN "BIANMU"."SYS_OPERATE_LOG"."LOGIN_NAME" IS '用户登录名';
COMMENT ON COLUMN "BIANMU"."SYS_OPERATE_LOG"."IP_ADDR" IS 'IP地址';
COMMENT ON COLUMN "BIANMU"."SYS_OPERATE_LOG"."URL" IS '请求路径';
COMMENT ON COLUMN "BIANMU"."SYS_OPERATE_LOG"."OPERATE" IS '操作行为';
COMMENT ON COLUMN "BIANMU"."SYS_OPERATE_LOG"."OPERATE_TIME" IS '操作时间';
COMMENT ON COLUMN "BIANMU"."SYS_OPERATE_LOG"."PARAMS" IS '请求参数';

-- 20210824
alter table "BIANMU"."SYS_MENU" add column("SYSTEM" VARCHAR2(50));
comment on column "BIANMU"."SYS_MENU"."SYSTEM" is '所属系统，BIANMU:分类编目，BROWSER:综合浏览，CONTROL:数据管控，DESKTOP:应用桌面';
alter table "BIANMU"."SYS_SETTING" add column("SYS_ADDRESS" VARCHAR2(150));
comment on column "BIANMU"."SYS_SETTING"."SYS_ADDRESS" is '系统访问地址';