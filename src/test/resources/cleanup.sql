-- Cleanup script para testes @SpringBootTest que nao usam rollback transacional
-- Executado APOS cada metodo de teste para garantir isolamento entre classes de teste
TRUNCATE TABLE article_favorites, article_tags, comments, follows, articles, tags, users CASCADE;
