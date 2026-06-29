import { expect, test } from '@playwright/test';
import { authHeader, createArticle, registerUser, uniqueId } from './helpers';

test('creates, reads, updates and soft-deletes an article', async ({ request }) => {
  const author = await registerUser(request, uniqueId('author'));
  const title = uniqueId('Playwright Article');
  const article = await createArticle(request, author.token, title);

  const read = await request.get(`/articles/${article.slug}`);
  expect(read.status()).toBe(200);
  expect((await read.json()).article.slug).toBe(article.slug);

  const update = await request.put(`/articles/${article.slug}`, {
    headers: authHeader(author.token),
    data: {
      article: {
        title: `${title} Updated`,
      },
    },
  });
  expect(update.status()).toBe(200);
  const updated = await update.json();
  expect(updated.article.title).toBe(`${title} Updated`);

  const deleted = await request.delete(`/articles/${updated.article.slug}`, {
    headers: authHeader(author.token),
  });
  expect(deleted.status()).toBe(204);

  const afterDelete = await request.get(`/articles/${updated.article.slug}`);
  expect(afterDelete.status()).toBe(404);
});

test('rejects unauthenticated article creation', async ({ request }) => {
  const response = await request.post('/articles', {
    data: {
      article: {
        title: uniqueId('No Auth Article'),
        description: 'description',
        body: 'body',
      },
    },
  });

  expect(response.status()).toBe(401);
});

test('forbids article updates by a non-author', async ({ request }) => {
  const author = await registerUser(request, uniqueId('author'));
  const intruder = await registerUser(request, uniqueId('intruder'));
  const article = await createArticle(request, author.token, uniqueId('Protected Article'));

  const response = await request.put(`/articles/${article.slug}`, {
    headers: authHeader(intruder.token),
    data: {
      article: {
        title: 'Hijacked',
      },
    },
  });

  expect(response.status()).toBe(403);
});
