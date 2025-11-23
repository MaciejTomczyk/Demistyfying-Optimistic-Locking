CREATE TABLE cats (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      version INT NOT NULL
);


INSERT INTO cats (name, version) VALUES
                                     ('Whiskers', 1),
                                     ('Mittens', 1);