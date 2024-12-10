create table member_fcm_token
(
    is_active  bit          not null,
    created_at datetime(6)  null,
    id         bigint auto_increment
        primary key,
    member_id  binary(16)   not null,
    fcm_token  varchar(255) not null,
    constraint UKn6c3rws6atlk3w4ff67mhgqm0
        unique (member_id),
    constraint FK1t32tfi2x0x2xl8te76jm3q2u
        foreign key (member_id) references member (id)
);
