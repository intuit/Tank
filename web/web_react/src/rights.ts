import type { CurrentUser } from './session';

/** AccessRight names, as keys of CurrentUser.rights */
export type AccessRight =
  | 'CREATE_PROJECT'
  | 'EDIT_PROJECT'
  | 'DELETE_PROJECT'
  | 'CREATE_SCRIPT'
  | 'EDIT_SCRIPT'
  | 'DELETE_SCRIPT'
  | 'CREATE_DATAFILE'
  | 'EDIT_DATAFILE'
  | 'DELETE_DATAFILE'
  | 'CREATE_FILTER'
  | 'EDIT_FILTER'
  | 'DELETE_FILTER'
  | 'CONTROL_JOB';

export function hasRight(user: CurrentUser | null | undefined, right: AccessRight): boolean {
  return user?.rights?.[right] === true;
}

/** The server's rule (RestAuthorization.requireRightOrOwner): the right, or owning the entity. */
export function hasRightOrOwns(user: CurrentUser | null | undefined, right: AccessRight, owner: string | undefined): boolean {
  return hasRight(user, right) || (!!user?.name && user.name === owner);
}
