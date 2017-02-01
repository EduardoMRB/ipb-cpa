CREATE TABLE videos (
id SERIAL PRIMARY KEY,
date DATE,
title VARCHAR(255),
excerpt TEXT,
embedded TEXT,
active BOOLEAN
);
