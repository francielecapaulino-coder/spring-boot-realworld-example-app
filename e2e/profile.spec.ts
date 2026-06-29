import { expect, test } from '@playwright/test';
import { authHeader, registerUser, uniqueId } from './helpers';

test('follows and unfollows a profile', async ({ request }) => {
  const target = await registerUser(request, uniqueId('target'));
  const follower = await registerUser(request, uniqueId('follower'));

  const profile = await request.get(`/profiles/${target.username}`);
  expect(profile.status()).toBe(200);
  expect((await profile.json()).profile.following).toBe(false);

  const follow = await request.post(`/profiles/${target.username}/follow`, {
    headers: authHeader(follower.token),
  });
  expect(follow.status()).toBe(200);
  expect((await follow.json()).profile.following).toBe(true);

  const unfollow = await request.delete(`/profiles/${target.username}/follow`, {
    headers: authHeader(follower.token),
  });
  expect(unfollow.status()).toBe(200);
  expect((await unfollow.json()).profile.following).toBe(false);
});
