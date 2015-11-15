CREATE TABLE result (
  id      INTEGER PRIMARY KEY AUTOINCREMENT,
  name    TEXT    NOT NULL,
  score   INTEGER NOT NULL,
  solved  char(1) NOT NULL,
  created TEXT    NOT NULL,
  enabled char(1) NOT NULL
);