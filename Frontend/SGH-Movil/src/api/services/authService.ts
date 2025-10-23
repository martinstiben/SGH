import { API_URL } from '../constant/api';
import { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse, Role, VerifyCodeRequest, VerifyCodeResponse } from '../types/auth';

export async function loginService(credentials: LoginRequest): Promise<{ requiresVerification: boolean; message?: string }> {
  const response = await fetch(`${API_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  });

  const data = await response.json() as { message: string } | { error: string };

  if (!response.ok) {
    throw new Error((data as any).error || 'Login failed');
  }

  // Backend returns { message: "Código de verificación enviado al correo electrónico" }
  return { requiresVerification: true, message: (data as { message: string }).message };
}

export async function verifyCodeService(request: VerifyCodeRequest): Promise<VerifyCodeResponse> {
  const response = await fetch(`${API_URL}/auth/verify-code`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  const data = (await response.json()) as VerifyCodeResponse | { error?: string };

  if (!response.ok) {
    throw new Error((data as any).error || 'Code verification failed');
  }

  return data as VerifyCodeResponse;
}

export async function registerService(request: RegisterRequest): Promise<RegisterResponse> {
  const response = await fetch(`${API_URL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  const data = (await response.json()) as RegisterResponse | { error?: string };

  if (!response.ok) {
    throw new Error((data as any).error || 'Registration failed');
  }

  return data as RegisterResponse;
}

export async function getRolesService(): Promise<{ roles: Role[] }> {
  const response = await fetch(`${API_URL}/auth/roles`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  const data = (await response.json()) as { roles: Role[] } | { error?: string };

  if (!response.ok) {
    throw new Error((data as any).error || 'Failed to fetch roles');
  }

  return data as { roles: Role[] };
}
