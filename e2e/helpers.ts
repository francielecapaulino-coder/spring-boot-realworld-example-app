import { APIRequestContext, expect } from '@playwright/test';

export function uniqueId(prefix: string): string {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
}

export async function registerUser(
  request: APIRequestContext,
  username: string,
  password = 'pass1234',
): Promise<{ email: string; token: string; username: string }> {
  const email = `${username}@example.com`;
  const response = await request.post('/users', {
    data: {
      user: {
        email,
        username,
        password,
      },
    },
  });

  expect(response.status()).toBe(201);
  const body = await response.json();
  return {
    email,
    token: body.user.token,
    username: body.user.username,
  };
}

export async function createArticle(
  request: APIRequestContext,
  token: string,
  title: string,
): Promise<{ slug: string }> {
  const response = await request.post('/articles', {
    headers: authHeader(token),
    data: {
      article: {
        title,
        description: 'Playwright E2E description',
        body: 'Playwright E2E body',
        tagList: ['playwright'],
      },
    },
  });

  expect(response.status()).toBe(200);
  const body = await response.json();
  return {
    slug: body.article.slug,
  };
}

export function authHeader(token: string): Record<string, string> {
  return {
    Authorization: `Token ${token}`,
  };
}
