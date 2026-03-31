import { createContext, useContext, useState, type ReactNode } from 'react'
import { login as apiLogin, logout as apiLogout, inRole as apiInRole, type LoginRequest, type UserInfo } from '../api/auth'

interface AuthState {
  user: UserInfo | null
  loading: boolean
  isAdmin: boolean
  login: (req: LoginRequest) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthState | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserInfo | null>(null)
  const [loading, setLoading] = useState(true)
  const [isAdmin, setIsAdmin] = useState(false)

  async function login(req: LoginRequest) {
    const info = await apiLogin(req)
    setIsAdmin(await apiInRole('admin'))
    setLoading(false)
    setUser(info)
  }

  async function logout() {
    await apiLogout()
    setUser(null)
  }

  return (
      <AuthContext.Provider value={{ user, loading, login, logout, isAdmin }}>
        {children}
      </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
