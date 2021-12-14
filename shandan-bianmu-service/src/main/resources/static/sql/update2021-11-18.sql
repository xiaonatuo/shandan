CREATE TABLE "BIANMU"."SYS_DICT"
(
    "ID" VARCHAR2(50) NOT NULL,
    "DICT_CODE" VARCHAR2(100) NOT NULL,
    "TYPE_ID" VARCHAR2(50) NOT NULL,
    "DICT_NAME" VARCHAR2(100) NOT NULL,
    "DICT_VALUE" VARCHAR2(50),
    "DICT_DESC" VARCHAR2(150),
    "DICT_ORDER" INT,
    "DICT_STATE" BIT DEFAULT 1,
    "CREATE_USER" VARCHAR2(50),
    "CREATE_TIME" TIMESTAMP(6),
    "MODIFY_USER" VARCHAR2(50),
    "MODIFY_TIME" TIMESTAMP(6),
    CLUSTER PRIMARY KEY("ID")) STORAGE(ON "MAIN", CLUSTERBTR) ;

COMMENT ON TABLE "BIANMU"."SYS_DICT" IS '系统数据字典表';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."CREATE_TIME" IS '创建时间';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."CREATE_USER" IS '创建用户';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."DICT_CODE" IS '字典编码';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."DICT_DESC" IS '字典描述';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."DICT_NAME" IS '字典名称';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."DICT_ORDER" IS '排序';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."DICT_STATE" IS '状态，0：停用，1：启用';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."DICT_VALUE" IS '字典值';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."ID" IS '主键';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."MODIFY_TIME" IS '修改时间';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."MODIFY_USER" IS '修改用户';
COMMENT ON COLUMN "BIANMU"."SYS_DICT"."TYPE_ID" IS '字典类型ID';



CREATE TABLE "BIANMU"."SYS_DICT_TYPE"
(
    "ID" VARCHAR2(50) NOT NULL,
    "NAME" VARCHAR2(100),
    "CREATE_USER" VARCHAR2(50),
    "CREATE_TIME" TIMESTAMP(6),
    "MODIFY_USER" VARCHAR2(50),
    "MODIFY_TIME" TIMESTAMP(6),
    CLUSTER PRIMARY KEY("ID")) STORAGE(ON "MAIN", CLUSTERBTR) ;

COMMENT ON TABLE "BIANMU"."SYS_DICT_TYPE" IS '系统数据字典类型表';
COMMENT ON COLUMN "BIANMU"."SYS_DICT_TYPE"."CREATE_TIME" IS '创建时间';
COMMENT ON COLUMN "BIANMU"."SYS_DICT_TYPE"."CREATE_USER" IS '创建用户';
COMMENT ON COLUMN "BIANMU"."SYS_DICT_TYPE"."ID" IS '主键,类型标识';
COMMENT ON COLUMN "BIANMU"."SYS_DICT_TYPE"."MODIFY_TIME" IS '修改时间';
COMMENT ON COLUMN "BIANMU"."SYS_DICT_TYPE"."MODIFY_USER" IS '修改用户';
COMMENT ON COLUMN "BIANMU"."SYS_DICT_TYPE"."NAME" IS '字典类型名称';



CREATE TABLE "BIANMU"."OAUTH_CLIENT_DETAILS"
(
    "CLIENT_ID" VARCHAR2(50) NOT NULL,
    "CLIENT_SECRET" VARCHAR2(255),
    "RESOURCE_IDS" VARCHAR2(255),
    "SCOPE" VARCHAR2(50),
    "AUTHORIZED_GRANT_TYPES" VARCHAR2(50),
    "WEB_SERVER_REDIRECT_URI" VARCHAR2(255),
    "AUTHORITIES" VARCHAR2(255),
    "ACCESS_TOKEN_VALIDITY" BIGINT,
    "REFRESH_TOKEN_VALIDITY" BIGINT,
    "ADDITIONAL_INFORMATION" VARCHAR2(255),
    "AUTOAPPROVE" VARCHAR2(50),
    CLUSTER PRIMARY KEY("CLIENT_ID")) STORAGE(ON "MAIN", CLUSTERBTR) ;

COMMENT ON TABLE "BIANMU"."OAUTH_CLIENT_DETAILS" IS 'Oauth2认证服务客户端信息配置表';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."ACCESS_TOKEN_VALIDITY" IS '令牌失效时间';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."ADDITIONAL_INFORMATION" IS '条件信息';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."AUTHORITIES" IS '权限';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."AUTHORIZED_GRANT_TYPES" IS '授权类型';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."AUTOAPPROVE" IS '自动授权';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."CLIENT_ID" IS '客户端ID';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."CLIENT_SECRET" IS '客户端秘钥';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."REFRESH_TOKEN_VALIDITY" IS '令牌刷新时间';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."RESOURCE_IDS" IS '资源ID';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."SCOPE" IS '范围';
COMMENT ON COLUMN "BIANMU"."OAUTH_CLIENT_DETAILS"."WEB_SERVER_REDIRECT_URI" IS '重定向URI';


INSERT INTO BIANMU.SYS_DICT_TYPE (ID, NAME, CREATE_USER, CREATE_TIME, MODIFY_USER, MODIFY_TIME) VALUES ('1', '密级', 'sa', '2021-11-18 00:12:15.000000', 'sa', '2021-11-18 00:12:19.000000');
INSERT INTO BIANMU.SYS_DICT_TYPE (ID, NAME, CREATE_USER, CREATE_TIME, MODIFY_USER, MODIFY_TIME) VALUES ('2', '数据类型', 'sa', '2021-11-18 00:12:15.000000', 'sa', '2021-11-18 00:12:19.000000');

INSERT INTO BIANMU.SYS_DICT (ID, DICT_CODE, TYPE_ID, DICT_NAME, DICT_VALUE, DICT_DESC, DICT_ORDER, DICT_STATE, CREATE_USER, CREATE_TIME, MODIFY_USER, MODIFY_TIME) VALUES ('1', '0', '1', '公开', '0', '', 1, 1, 'sa', '2021-11-18 00:21:44.000000', 'sa', '2021-11-18 03:18:20.000000');
INSERT INTO BIANMU.SYS_DICT (ID, DICT_CODE, TYPE_ID, DICT_NAME, DICT_VALUE, DICT_DESC, DICT_ORDER, DICT_STATE, CREATE_USER, CREATE_TIME, MODIFY_USER, MODIFY_TIME) VALUES ('2', '1', '1', '内部', '1', '', 2, 1, 'sa', '2021-11-18 00:21:46.000000', 'sa', '2021-11-18 03:18:36.000000');
INSERT INTO BIANMU.SYS_DICT (ID, DICT_CODE, TYPE_ID, DICT_NAME, DICT_VALUE, DICT_DESC, DICT_ORDER, DICT_STATE, CREATE_USER, CREATE_TIME, MODIFY_USER, MODIFY_TIME) VALUES ('3', '2', '1', '秘密', '2', null, 3, 1, 'sa', '2021-11-18 00:21:46.000000', 'sa', '2021-11-18 00:21:59.000000');
INSERT INTO BIANMU.SYS_DICT (ID, DICT_CODE, TYPE_ID, DICT_NAME, DICT_VALUE, DICT_DESC, DICT_ORDER, DICT_STATE, CREATE_USER, CREATE_TIME, MODIFY_USER, MODIFY_TIME) VALUES ('4', '3', '1', '机密', '3', null, 4, 1, 'sa', '2021-11-18 00:21:47.000000', 'sa', '2021-11-18 00:22:00.000000');
INSERT INTO BIANMU.SYS_DICT (ID, DICT_CODE, TYPE_ID, DICT_NAME, DICT_VALUE, DICT_DESC, DICT_ORDER, DICT_STATE, CREATE_USER, CREATE_TIME, MODIFY_USER, MODIFY_TIME) VALUES ('5', '4', '1', '绝密', '4', null, 5, 1, 'sa', '2021-11-18 00:21:48.000000', 'sa', '2021-11-18 00:22:00.000000');

INSERT INTO BIANMU.OAUTH_CLIENT_DETAILS (CLIENT_ID, CLIENT_SECRET, RESOURCE_IDS, SCOPE, AUTHORIZED_GRANT_TYPES, WEB_SERVER_REDIRECT_URI, AUTHORITIES, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY, ADDITIONAL_INFORMATION, AUTOAPPROVE) VALUES ('bianmu', '0CE1331B040C1572F5A95F0EE54DBE53', null, 'all', 'authorization_code,refresh_token', 'http://localhost:8080/bianmu/login', null, 7200, 7200, null, 'true');
INSERT INTO BIANMU.OAUTH_CLIENT_DETAILS (CLIENT_ID, CLIENT_SECRET, RESOURCE_IDS, SCOPE, AUTHORIZED_GRANT_TYPES, WEB_SERVER_REDIRECT_URI, AUTHORITIES, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY, ADDITIONAL_INFORMATION, AUTOAPPROVE) VALUES ('browser', '052B915E573CFA9C260431F473202235', null, 'all', 'authorization_code,refresh_token', 'http://localhost:8090/browser/login', null, 7200, 7200, null, 'true');
INSERT INTO BIANMU.OAUTH_CLIENT_DETAILS (CLIENT_ID, CLIENT_SECRET, RESOURCE_IDS, SCOPE, AUTHORIZED_GRANT_TYPES, WEB_SERVER_REDIRECT_URI, AUTHORITIES, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY, ADDITIONAL_INFORMATION, AUTOAPPROVE) VALUES ('control', '5E08B374444263144692F2A60DF97D03', null, 'all', 'authorization_code,refresh_token', 'http://localhost:8100/control/login', null, 7200, 7200, null, 'true');

INSERT INTO BIANMU.SYS_MENU (MENU_ID, MENU_NAME, MENU_PATH, MENU_PARENT_ID, SORT_WEIGHT, CREATE_TIME, MODIFY_TIME, CREATE_USER, MODIFY_USER, IS_DELETE, SYSTEM) VALUES ('1461008460238610433', '数据字典', '/sys/dict/', '35cb950cebb04bb18bb1d8b742a02xxx', 6, '2021-11-18 00:28:56', '2021-11-18 00:28:56', 'sa', 'sa', '0', 'CONTROL');
INSERT INTO BIANMU.SYS_ROLE_MENU (RM_ID, ROLE_ID, MENU_ID, CREATE_TIME, CREATE_USER, MODIFY_TIME, MODIFY_USER) VALUES ('1461008530321235987', '3fb1c570496d4c09ab99b8d31b06zzz', '1461008460238610433', '2021-11-18 00:29:12.522000', 'sa', '2021-11-18 00:29:12.522000', 'sa');

CREATE TABLE "BIANMU"."SYS_USER_CLIENT"
(
    "USER_CLIENT_ID" VARCHAR(50) NOT NULL,
    "USER_ID" VARCHAR2(50) NOT NULL,
    "CLIENT_ID" VARCHAR2(50) NOT NULL,
    "CREATE_TIME" TIMESTAMP(0),
    "MODIFY_TIME" TIMESTAMP(0),
    "CREATE_USER" VARCHAR2(50),
    "MODIFY_USER" VARCHAR2(50),
    NOT CLUSTER PRIMARY KEY("USER_CLIENT_ID")) STORAGE(ON "MAIN", CLUSTERBTR) ;

COMMENT ON TABLE "BIANMU"."SYS_USER_CLIENT" IS '系统用户与Oauth2客户端关系表';
COMMENT ON COLUMN "BIANMU"."SYS_USER_CLIENT"."CLIENT_ID" IS '客户端ID';
COMMENT ON COLUMN "BIANMU"."SYS_USER_CLIENT"."CREATE_TIME" IS '创建时间';
COMMENT ON COLUMN "BIANMU"."SYS_USER_CLIENT"."CREATE_USER" IS '创建用户';
COMMENT ON COLUMN "BIANMU"."SYS_USER_CLIENT"."MODIFY_TIME" IS '修改时间';
COMMENT ON COLUMN "BIANMU"."SYS_USER_CLIENT"."MODIFY_USER" IS '修改用户';
COMMENT ON COLUMN "BIANMU"."SYS_USER_CLIENT"."USER_CLIENT_ID" IS '关系主键';
COMMENT ON COLUMN "BIANMU"."SYS_USER_CLIENT"."USER_ID" IS '用户ID';
