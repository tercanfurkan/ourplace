CREATE TABLE IF NOT EXISTS messages
(
    id                     VARCHAR(60) DEFAULT RANDOM_UUID() PRIMARY KEY,
    content                VARCHAR      NOT NULL,
    content_type           VARCHAR(128) NOT NULL,
    sent_at                TIMESTAMP    NOT NULL,
    username               VARCHAR(60)  NOT NULL,
    user_avatar_link       VARCHAR(256) NOT NULL
);