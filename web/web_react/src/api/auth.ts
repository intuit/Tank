export interface LoginRequest {
  username: string
  password: string
}

export interface UserInfo {
  username: string
  roles: string[]
}

export interface SsoRedirectResponse {
  redirectUrl: string
}

const BASE = '/v2/auth'

export async function login(req: LoginRequest): Promise<UserInfo> {
  const res = await fetch(`${BASE}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(req),
    credentials: 'include',
  })
  if (!res.ok) throw new Error('Invalid username or password')
  return res.json() as Promise<UserInfo>
}

export async function logout(): Promise<void> {
  await fetch(`${BASE}/logout`, { method: 'POST', credentials: 'include' })
}

export async function inRole(role: string): Promise<boolean> {
  const res = await fetch(`${BASE}/inRole/${role}`, { credentials: 'include' })
  if (res.status === 401) return false
  if (!res.ok) throw new Error('Failed to fetch user info')
  return true
}

export async function getSsoRedirectUrl(): Promise<string> {
  const res = await fetch(`${BASE}/sso-redirect`, { credentials: 'include' })
  if (!res.ok) throw new Error('SSO not configured')
  const data = await res.json() as SsoRedirectResponse
  return data.redirectUrl
}
