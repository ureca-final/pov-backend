ALTER TABLE payment
DROP
FOREIGN KEY FK_payment_member;

ALTER TABLE payment
DROP
COLUMN member_id;