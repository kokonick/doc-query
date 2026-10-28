CREATE TABLE  IF NOT EXISTS document (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT,
    file_name TEXT,
    content TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);