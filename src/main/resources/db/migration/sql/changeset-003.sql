CREATE TABLE IF NOT EXISTS transfers (
                                     id BIGSERIAL PRIMARY KEY,
                                     transfer_date DATE NOT NULL,
                                     amount NUMERIC(10,2) NOT NULL,
                                     card_sender_id BIGINT NOT NULL,
                                     FOREIGN KEY (card_sender_id) REFERENCES cards(id)
                                     ON UPDATE CASCADE,
                                     card_recipient_id BIGINT NOT NULL,
                                     FOREIGN KEY (card_recipient_id) REFERENCES cards(id)
                                     ON UPDATE CASCADE,
                                     user_id BIGINT NOT NULL,
                                     FOREIGN KEY (user_id) REFERENCES users(id)
                                     ON UPDATE CASCADE,
                                     transfer_status VARCHAR(255) NOT NULL
                                     );