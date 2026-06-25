package io.spring.application.user;

import io.spring.core.user.User;

/**
 * Command object passed to {@code UserService.updateUser} carrying the target user
 * together with the incoming update payload.
 *
 * <p>Pure carrier of data — converted to {@code record} under US-06.03 / KR1.5
 * (mandate J5). The class-level {@code @UpdateUserConstraint} (which cross-validates
 * email/username uniqueness against the target user) is preserved on the record
 * declaration; the {@code UpdateUserValidator} reads {@code targetUser()} and
 * {@code param()} via the canonical record accessors.
 */
@UpdateUserConstraint
public record UpdateUserCommand(User targetUser, UpdateUserParam param) {}
