export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  requiresVerification?: boolean;
  message?: string;
}

export interface VerifyCodeRequest {
  email: string;
  code: string;
}

export interface VerifyCodeResponse {
  token: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role: string;
}

export interface RegisterResponse {
  message: string;
}

export interface Role {
  value: string;
  label: string;
}
