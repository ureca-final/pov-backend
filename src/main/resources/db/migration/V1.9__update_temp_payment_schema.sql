ALTER TABLE temp_payment
    DROP FOREIGN KEY FK_temp_payment_member;

ALTER TABLE temp_payment
    DROP INDEX UK_temp_payment_member;

ALTER TABLE temp_payment
    ADD CONSTRAINT FK_temp_payment_member
        FOREIGN KEY (member_id) REFERENCES member (id);