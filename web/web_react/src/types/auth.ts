export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthUser {
  token: string;
  name: string;
  role: string;
}
