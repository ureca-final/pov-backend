create table fcm_result
(
    id             bigint auto_increment primary key,
    token          varchar(255) not null,
    is_success     bit          not null,
    error_code     enum('INVALID_REGISTRATION', 'NOT_REGISTERED', 'INVALID_ARGUMENT',
                       'AUTHENTICATION_ERROR', 'SERVER_ERROR', 'QUOTA_EXCEEDED',
                       'UNAVAILABLE', 'INTERNAL_ERROR', 'UNKNOWN'),
    notice_send_id bigint,
    created_at     datetime(6),
    updated_at     datetime(6),
    constraint FK_fcm_result_notice_send foreign key (notice_send_id) references notice_send (id)
);