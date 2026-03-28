import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { usersApi } from '../api/users';
import type { UserRequest } from '../types/user';

export const userKeys = {
  all: ['users'] as const,
  byId: (id: number) => ['users', id] as const,
  groups: ['users', 'groups'] as const,
};

export function useUsers() {
  return useQuery({
    queryKey: userKeys.all,
    queryFn: () => usersApi.getAll().then((r) => r.data ?? []),
  });
}

export function useUser(id: number) {
  return useQuery({
    queryKey: userKeys.byId(id),
    queryFn: () => usersApi.getById(id).then((r) => r.data),
    enabled: !!id,
  });
}

export function useAllGroups() {
  return useQuery({
    queryKey: userKeys.groups,
    queryFn: () => usersApi.getGroups().then((r) => r.data ?? []),
  });
}

export function useCreateUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (request: UserRequest) => usersApi.create(request),
    onSuccess: () => qc.invalidateQueries({ queryKey: userKeys.all }),
  });
}

export function useUpdateUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: UserRequest }) =>
      usersApi.update(id, request),
    onSuccess: (_data, { id }) => {
      qc.invalidateQueries({ queryKey: userKeys.all });
      qc.invalidateQueries({ queryKey: userKeys.byId(id) });
    },
  });
}

export function useDeleteUser() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => usersApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: userKeys.all }),
  });
}

export function useGenerateToken() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => usersApi.generateToken(id),
    onSuccess: (_data, id) => {
      qc.invalidateQueries({ queryKey: userKeys.all });
      qc.invalidateQueries({ queryKey: userKeys.byId(id) });
    },
  });
}

export function useDeleteToken() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => usersApi.deleteToken(id),
    onSuccess: (_data, id) => {
      qc.invalidateQueries({ queryKey: userKeys.all });
      qc.invalidateQueries({ queryKey: userKeys.byId(id) });
    },
  });
}
