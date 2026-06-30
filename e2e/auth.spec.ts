import { expect, test } from '@playwright/test';
import { registerUser, uniqueId } from './helpers';

test('registers and logs in a user through the REST API', async ({ request }) => {
  const username = uniqueId('auth-user');
  const password = 'pass1234';
  const registered = await registerUser(request, username, password);

  expect(registered.username).toBe(username);
  expect(registered.token).toBeTruthy();

  const login = await request.post('/users/login', {
    data: {
      user: {
        email: registered.email,
        password,
      },
    },
  });

  expect(login.status()).toBe(200);
  const body = await login.json();
  expect(body.user.email).toBe(registered.email);
  expect(body.user.token).toBeTruthy();
});

test('rejects invalid login credentials', async ({ request }) => {
  const username = uniqueId('bad-login');
  const registered = await registerUser(request, username);

  const response = await request.post('/users/login', {
    data: {
      user: {
        email: registered.email,
        password: 'wrong-password',
      },
    },
  });

  expect(response.status()).toBe(422);
});
