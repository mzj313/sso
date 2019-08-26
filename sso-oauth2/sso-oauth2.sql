/*==============================================================*/
/* Table: t_authz_approvals                                           */
/*==============================================================*/
create table t_authz_approvals
(
   open_id              varchar(128) not null comment '主键id',
   client_id            varchar(64) comment '',
   user_id              varchar(64) comment '',
   as_code              varchar(100) comment '',
   as_token             varchar(3000) comment '',
   scope                varchar(255) comment '',
   expiresat            int comment '',
   status               varchar(64) comment '',
   lastmodifiedat       int comment '',
   is_used_as_code      int comment '',
   refresh_token        varchar(255) comment '',
   primary key (open_id)
);

alter table t_authz_approvals comment '授权表';


/*==============================================================*/
/* Table: t_user_pass                                           */
/*==============================================================*/
create table t_user_pass
(
   id                   varchar(64) not null comment '主键id',
   user_id              varchar(64) comment '',
   password             varchar(100) comment '',
   account              varchar(100) comment '',
   createtime           datetime comment '',
   salt                 varchar(64) comment '',
   primary key (id)
);

alter table t_user_pass comment '用户账号表';


/*==============================================================*/
/* Table: t_user                                           */
/*==============================================================*/
create table t_user
(
   id                    varchar(64) not null comment '主键id',
   username              varchar(100) comment '',
   sex                   varchar(1) comment '',
   org_id                varchar(64) comment '',
   idcard                varchar(18) comment '',
   telphone              varchar(11) comment '',
   email                 varchar(100) comment '',
   comment               varchar(500) comment '',
   createtime            datetime comment '',
   primary key (id)
);

alter table t_user comment '用户表';

/*==============================================================*/
/* Table: t_app                                           */
/*==============================================================*/
create table t_app
(
   id                     varchar(64) not null comment '主键id',
   code                   varchar(32) comment '',
   name                   varchar(100) comment '',
   url                    varchar(100) comment '',
   client_id              varchar(64) comment '',
   client_secret          varchar(64) comment '',
   secret_key_private     varchar(1000) comment '',
   secret_key_public      varchar(1000) comment '',
   access_token_validity  int comment '',
   refresh_token_validity int comment '',
   comment                varchar(500) comment '',
   display_order          int comment '',
   createtime             datetime comment '',
   access_group_id        varchar(64) comment '',
   scope                  varchar(255) comment '',
   primary key (id)
);

alter table t_app comment '应用表';


INSERT INTO `t_user` (`id`, `username`, `sex`, `telphone`, `email`) VALUES ('10001', '测试', '1', '18812121212', '123@qq.com');
INSERT INTO `t_user_pass` (`id`, `user_id`, `password`, `account`, `salt`) VALUES ('10001', '10001', '889255f1c9c8f12a353be255f78a848b', 'test', 'test');
INSERT INTO `t_app` (`id`, `code`, `name`, `url`, `client_id`, `client_secret`, `secret_key_private`, `secret_key_public`, `access_token_validity`, `refresh_token_validity`, `comment`, `createtime`) VALUES ('1', 'app1', '应用1', NULL, '3cde850fd11f49b393dc02d9b993de32', '80d30c3bdb8c43689a75c3f766d883ff', NULL, NULL, '1', '30', NULL, NULL);
INSERT INTO `t_app` (`id`, `code`, `name`, `url`, `client_id`, `client_secret`, `secret_key_private`, `secret_key_public`, `access_token_validity`, `refresh_token_validity`, `comment`, `createtime`) VALUES ('2', 'app2', '应用2', NULL, '3cde850fd11f49b393dc02d9b993de33', '80d30c3bdb8c43689a75c3f766d883fg', NULL, NULL, '1', '30', NULL, NULL);
INSERT INTO `t_authz_approvals` (`open_id`, `client_id`, `user_id`, `as_code`, `as_token`, `scope`, `expiresat`, `status`, `lastmodifiedat`, `is_used_as_code`, `refresh_token`) VALUES ('1', '3cde850fd11f49b393dc02d9b993de32', '10001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `t_authz_approvals` (`open_id`, `client_id`, `user_id`, `as_code`, `as_token`, `scope`, `expiresat`, `status`, `lastmodifiedat`, `is_used_as_code`, `refresh_token`) VALUES ('2', '3cde850fd11f49b393dc02d9b993de33', '10001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

