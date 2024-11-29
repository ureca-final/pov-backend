-- 1. 독립적인 테이블 먼저 생성
CREATE TABLE common_code_group (
                                   common_code_group_description varchar(255) null,
                                   common_code_group_name varchar(255) null,
                                   is_active bit not null,
                                   group_code varchar(255) not null primary key
);

CREATE TABLE movie (
                       has_award bit not null,
                       id bigint auto_increment primary key,
                       released date null,
                       country varchar(255) null,
                       director varchar(255) null,
                       tmdb_id int null,
                       plot varchar(255) null,
                       poster varchar(255) null,
                       title varchar(255) null,
                       writer varchar(255) null,
                       is_adult bit not null,
                       backdrop varchar(255) null
);

CREATE TABLE club (
                      is_public bit not null,
                      max_participants int null,
                      created_at datetime(6) null,
                      id binary(16) not null primary key,
                      description varchar(255) null,
                      name varchar(255) null
);

CREATE TABLE member (
                        birth date null,
                        is_notice_active bit   not null,
                        created_at datetime(6) null,
                        deleted_at datetime(6) null,
                        id binary(16) not null primary key,
                        email varchar(255) null,
                        nickname varchar(255) null,
                        profile_image varchar(255) null,
                        role varchar(255) null,
                        social_type varchar(255) null
);

CREATE TABLE premiere (
                          is_payment_required bit not null,
                          id bigint auto_increment primary key,
                          start_at datetime(6) null,
                          content text null,
                          event_image varchar(255) null,
                          title varchar(255) null
);

-- 2. 외래키를 가진 테이블들 생성
CREATE TABLE common_code (
                             code varchar(255) not null,
                             common_code_description varchar(255) null,
                             common_code_name varchar(255) null,
                             group_code varchar(255) not null,
                             is_active bit not null ,
                             PRIMARY KEY (group_code, code),
                             CONSTRAINT FKhkusbmskjw1jk5pjh8sui9cnp FOREIGN KEY (group_code) REFERENCES common_code_group (group_code)
);

CREATE TABLE actor (
                       id bigint auto_increment primary key,
                       movie_id bigint null,
                       name varchar(255) null,
                       CONSTRAINT FKj1v8ubnn1t4547r4q725paa8h FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE award (
                       id bigint auto_increment primary key,
                       movie_id bigint null,
                       name varchar(255) null,
                       CONSTRAINT FK3jo1qobhxrpnricjwo30hemsi FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE member_club (
                             is_leader bit not null,
                             created_at datetime(6) null,
                             id bigint not null primary key,
                             club_id binary(16) null,
                             member_id binary(16) null,
                             CONSTRAINT FK73tthgfalulir41hmwmuwieyc FOREIGN KEY (club_id) REFERENCES club (id),
                             CONSTRAINT FKbwpq58hyksntm0wcrunw2hcqe FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE club_favor_genre (
                                  genre_code varchar(2) null,
                                  id bigint auto_increment primary key,
                                  club_id binary(16) null,
                                  CONSTRAINT FK7oxhernp72286u3dwppqcwk1b FOREIGN KEY (club_id) REFERENCES club (id)
);

CREATE TABLE club_movie (
                            created_at datetime(6) null,
                            id bigint not null primary key,
                            movie_id bigint null,
                            club_id binary(16) null,
                            CONSTRAINT FK8kry8ai0frhjchbeb7v9yoogv FOREIGN KEY (movie_id) REFERENCES movie (id),
                            CONSTRAINT FKqil3i0k4is2mxynctcuon9oyu FOREIGN KEY (club_id) REFERENCES club (id)
);

CREATE TABLE curation (
                          created_at datetime(6) null,
                          id bigint auto_increment primary key,
                          member_id binary(16) null,
                          description varchar(255) null,
                          theme varchar(255) null,
                          title varchar(255) null,
                          category enum ('ACTOR', 'AWARD', 'COUNTRY', 'DIRECTOR', 'GENRE', 'OTHER', 'RELEASE') null,
                          start_time datetime(6) null,
                          CONSTRAINT FKdlnps7qqivebgquql0rxw9qri FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE entry (
                       amount int null,
                       quantity    int         null,
                       created_at datetime(6) null,
                       id bigint auto_increment primary key,
                       premiere_id bigint null,
                       member_id binary(16) null,
                       CONSTRAINT FKant1v9nuotv4eueo2lbpga8vj FOREIGN KEY (member_id) REFERENCES member (id),
                       CONSTRAINT FKbqtcl89r826gm2jf92jjci0x9 FOREIGN KEY (premiere_id) REFERENCES premiere (id)
);

CREATE TABLE member_favor_genre (
                                    genre_code varchar(2) null,
                                    created_at datetime(6) null,
                                    id bigint auto_increment primary key,
                                    member_id binary(16) null,
                                    CONSTRAINT FKiaxiyt0n6b6l4v6lg57xmvfv4 FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE movie_content (
                               id bigint auto_increment primary key,
                               movie_id bigint null,
                               content varchar(255) null,
                               content_type enum ('IMAGE', 'YOUTUBE') null,
                               CONSTRAINT FKsxi6kjwd6w964wk4urng1ptsf FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE movie_genre (
                             genre_code varchar(2) null,
                             id bigint auto_increment primary key,
                             movie_id bigint null,
                             CONSTRAINT FKp6vjabv2e2435at1hnuxg64yv FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE movie_like (
                            created_at datetime(6) null,
                            id bigint auto_increment primary key,
                            movie_id bigint null,
                            member_id binary(16) null,
                            is_liked   boolean default false,
                            CONSTRAINT FK221ilwmr0s5y3m371cfbjhmgs FOREIGN KEY (member_id) REFERENCES member (id),
                            CONSTRAINT FKdglb6nfnx6ge9ogjlb6dqb63m FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE movie_like_count (
                                  like_count bigint null,
                                  movie_id bigint not null primary key,
                                  CONSTRAINT FKo0i0dph7pki64cfui19lt12xc FOREIGN KEY (movie_id) REFERENCES movie (id)
);

create table payment
(
    amount       int          null,
    approved_at datetime(6)  null,
    created_at  datetime(6)  null,
    id          bigint auto_increment
        primary key,
    member_id   binary(16)   null,
    payment_key varchar(255) null,
    vendor      varchar(255) null,
    constraint FK4pswry4r5sx6j57cdeulh1hx8
        foreign key (member_id) references member (id)
);

create table payment_transaction
(
    amount           int                                  null,
    created_at      datetime(6)                           null,
    id              bigint auto_increment
        primary key,
    payment_id      bigint                                null,
    transaction_key varchar(255)                          null,
    status          enum ('FAILED', 'PENDING', 'SUCCESS') null,
    type            enum ('CANCEL', 'PAY', 'REFUND')      null,
    constraint UK8qrrs01smxp31txlwy36q7mam
        unique (payment_id),
    constraint FKhs69kx826yrnvhanj0m0dcegn
        foreign key (payment_id) references payment (id)
);

create table temp_payment
(
    amount     int             null,
    created_at datetime(6)     null,
    id         bigint auto_increment
        primary key,
    member_id  binary(16)      null,
    order_id   varchar(255)    null,
    type       enum ('NORMAL') null,
    constraint UKjnp2rmi61sdvkti7ido11w140
        unique (member_id),
    constraint FKqupcsmm2a74886you7gwa4fqp
        foreign key (member_id) references member (id)
);

CREATE TABLE recommended_movie (
                                   created_at datetime(6) null,
                                   id bigint auto_increment primary key,
                                   movie_id bigint null,
                                   member_id binary(16) null,
                                   CONSTRAINT FKcsfw03338yfc99j83utsivmxg FOREIGN KEY (movie_id) REFERENCES movie (id),
                                   CONSTRAINT FKtkysi9psfmiakp04m8suvn1ac FOREIGN KEY (member_id) REFERENCES member (id)
);

create table review
(
    disabled    bit          not null,
    is_spoiler  bit          not null,
    created_at  datetime(6)  null,
    deleted_at  datetime(6)  null,
    id          bigint auto_increment
        primary key,
    modified_at datetime(6)  null,
    member_id   binary(16)   null,
    contents    text         null,
    title       varchar(255) null,
    preference  varchar(255) null,
    thumbnail   varchar(255) null,
    movie_id    bigint      not null,
    CONSTRAINT FKk0ccx5i4ci2wd70vegug074w1 FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT FK_review_movie FOREIGN KEY (movie_id) REFERENCES movie (id)
);

CREATE TABLE review_keyword_link (
                                     review_keyword_code varchar(2) null,
                                     id bigint auto_increment primary key,
                                     review_id bigint null,
                                     CONSTRAINT FK8vdr4cgq8fwp3b4ve89e7oiqr FOREIGN KEY (review_id) REFERENCES review (id)
);

CREATE TABLE review_like (
                             created_at datetime(6) null,
                             id bigint auto_increment primary key,
                             review_id bigint null,
                             member_id binary(16) null,
                             is_liked   boolean default false,
                             CONSTRAINT FK68am9vk1s1e8n1v873meqkk0k FOREIGN KEY (review_id) REFERENCES review (id),
                             CONSTRAINT FKf19ep4u0vm5vietilw2kp9jo2 FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE review_like_count (
                                   review_id bigint not null primary key,
                                   review_like_count bigint null,
                                   CONSTRAINT FKa4lkj3hwstfenynlujdqpx2sx FOREIGN KEY (review_id) REFERENCES review (id)
);

create table notice
(
    is_active      bit          not null,
    notice_type enum ('REVIEW', 'CLUB') null,
    created_at     datetime(6)  null,
    id             bigint auto_increment
        primary key,
    member_id      binary(16)   null,
    description    varchar(255) null,
    notice_content text         null,
    notice_title   varchar(255) null
);

create table notice_receive
(
    is_read        bit          not null,
    notice_type enum ('REVIEW', 'CLUB') null,
    created_at     datetime(6)  null,
    id             bigint auto_increment
        primary key,
    notice_send_id bigint       null,
    member_id   binary(16)   null,
    notice_content text         null,
    notice_title   varchar(255) null,
    constraint FKtqfpe9qomg6roy9o0y3458wcb
        foreign key (member_id) references member (id)
);

create table notice_send
(
    is_succeed            bit         not null,
    created_at            datetime(6) null,
    id                    bigint auto_increment
        primary key,
    notice_id             bigint      null,
    notice_content_detail text        null,
    constraint FK6pksr0fsrrngct93esdrccn1f
        foreign key (notice_id) references notice (id)
);

