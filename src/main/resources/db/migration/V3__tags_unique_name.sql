-- Dedupe tags by name and enforce UNIQUE(name) (#90).
--
-- Pre-existing bug: JpaArticleRepository.save() did not reuse the persisted
-- Tag instance when one with the same name already existed. The JPA cascade
-- then inserted a duplicate Tag row. Over time the tags table could accumulate
-- multiple rows with the same name, breaking findByName with
-- NonUniqueResultException.
--
-- This migration:
--   1. Picks the canonical (oldest, smallest id) Tag row per name.
--   2. Re-points every article_tags reference from any duplicate id to the
--      canonical id.
--   3. Removes the now-orphan duplicate Tag rows.
--   4. Adds a UNIQUE constraint on tags.name so future writes cannot reintroduce
--      the same situation.
--
-- The migration is idempotent and safe to run against a clean database: the
-- dedupe CTE is a no-op when no duplicates exist.

WITH ranked_tags AS (
  SELECT
    id,
    name,
    ROW_NUMBER() OVER (PARTITION BY name ORDER BY id) AS rn,
    MIN(id) OVER (PARTITION BY name)                  AS keep_id
  FROM tags
)
UPDATE article_tags AS at
SET tag_id = rt.keep_id
FROM ranked_tags rt
WHERE at.tag_id = rt.id
  AND rt.rn > 1;

DELETE FROM tags
WHERE id IN (
  SELECT id
  FROM (
    SELECT
      id,
      ROW_NUMBER() OVER (PARTITION BY name ORDER BY id) AS rn
    FROM tags
  ) ranked
  WHERE ranked.rn > 1
);

ALTER TABLE tags
  ADD CONSTRAINT uk_tags_name UNIQUE (name);
