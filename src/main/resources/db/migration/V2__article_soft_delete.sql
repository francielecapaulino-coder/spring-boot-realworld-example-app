-- Soft delete for articles (US-88).
-- Adds an is_deleted flag so DELETE /articles/{slug} can hide an article from
-- reads while keeping the row for audit/recovery purposes.
--
-- Decisions:
-- * NOT NULL with default FALSE so existing rows are automatically marked as
--   not deleted without requiring a data backfill.
-- * The existing UNIQUE constraint on slug is intentionally preserved as a
--   plain (full-table) unique index. Slugs from soft-deleted articles are NOT
--   reusable in this iteration; a partial unique index that would allow reuse
--   is out of scope and tracked separately.
ALTER TABLE articles
  ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
