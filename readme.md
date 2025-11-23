## Demystifying Optimistic Locking in Databases

In web applications, it’s common to encounter situations where multiple users try to modify the same data simultaneously. To prevent overwriting changes made by others, a mechanism called optimistic locking is often used.

If you’re using an ORM like Hibernate or JPA, you probably already have built-in support for this. Typically, it involves adding a special column (e.g., version) to your database table to track the record’s version. But have you ever wondered how it works under the hood? In this article, I’ll explain using a simple SQL-based example—no frameworks required.

### How Optimistic Locking Works

Let’s assume we have a table cats with columns id, name, and version:
```sql
CREATE TABLE cats (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    version INT NOT NULL
);
```

When a user wants to update a record, they first fetch it from the database, including its version value. Later, when the user makes changes and attempts to save them back, the application executes an SQL update with a condition that checks whether the record’s version in the database matches the version the user originally retrieved:
```sql
UPDATE cats
SET name = 'New Name', version = version + 1
WHERE id = ? AND version = ?;
```

If the number of updated rows is 0, it means someone else modified the record in the meantime, and the application can notify the user of a conflict.

### Practical Example

Suppose we have a cat record with id = 1, name = 'Whiskers', and version = 1.

User A fetches the record:
```sql
SELECT id, name, version FROM cats WHERE id = 1;
-- Result: (1, 'Whiskers', 1)
```

User B also fetches the same record:
```sql
SELECT id, name, version FROM cats WHERE id = 1;
-- Result: (1, 'Whiskers', 1)
```

User A changes the cat’s name to 'Fluffy' and updates the record:
```sql
UPDATE cats
SET name = 'Fluffy', version = version + 1
WHERE id = 1 AND version = 1;
-- Rows updated: 1
```

Meanwhile, User B changes the cat’s name to 'Mittens' and tries to save it:
```sql
UPDATE cats
SET name = 'Mittens', version = version + 1
WHERE id = 1 AND version = 1;
-- Rows updated: 0 (conflict)
```

The key is that the WHERE clause checks the object version. Since the version no longer matches for User B, the update fails. User B must re-fetch the latest record, retrieve the current version, and attempt the update again.

### Summary

Optimistic locking is an effective way to manage concurrent access to data in web applications. By maintaining a simple version counter, you can prevent conflicts and ensure data integrity without complex locking mechanisms.

Even if your ORM handles this automatically, understanding how it works at the database level helps you diagnose issues and optimize performance.

I’ve also shared a small project that demonstrates optimistic locking in action. Check it out here:

(Insert link to your repository)