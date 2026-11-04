import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import { unauthorizedMiddleware, type Schemas, type TankClient } from './api/client';

export type CurrentUser = Schemas['CurrentUser'];
export type AuthConfig = Schemas['AuthConfig'];

interface Session {
  client: TankClient;
  config: AuthConfig | undefined;
  /** undefined while loading, null when signed out */
  user: CurrentUser | null | undefined;
  /** Whether the user was signed out because their session expired, rather than by signing out */
  expired: boolean;
  signIn: (username: string, password: string) => Promise<string | undefined>;
  signOut: () => Promise<void>;
}

const SessionContext = createContext<Session | undefined>(undefined);

export function useSession(): Session {
  const session = useContext(SessionContext);
  if (!session) {
    throw new Error('useSession must be used inside <SessionProvider>');
  }
  return session;
}

/** Loads the login page configuration and the signed-in user, and signs the user out on any 401. */
export function SessionProvider({ client, children }: { client: TankClient; children: ReactNode }) {
  const [config, setConfig] = useState<AuthConfig>();
  const [user, setUser] = useState<CurrentUser | null>();
  const [expired, setExpired] = useState(false);
  const userRef = useRef(user);
  userRef.current = user;

  useEffect(() => {
    const middleware = unauthorizedMiddleware(() => {
      // a 401 while signed in means the session ended on the server
      if (userRef.current) {
        setExpired(true);
      }
      setUser(null);
    });
    client.use(middleware);
    return () => client.eject(middleware);
  }, [client]);

  useEffect(() => {
    let active = true;
    void client.GET('/v2/auth/config').then(({ data }) => active && setConfig(data));
    void client.GET('/v2/me').then(({ data }) => active && setUser(data ?? null));
    return () => {
      active = false;
    };
  }, [client]);

  /** @returns an error message, or undefined on success */
  const signIn = useCallback(
    async (username: string, password: string) => {
      const { data, response } = await client.POST('/v2/auth/login', { body: { username, password } });
      if (data) {
        setExpired(false);
        setUser(data);
        return undefined;
      }
      return response.status === 401 ? 'Invalid username or password' : `Sign-in failed (${response.status})`;
    },
    [client],
  );

  const signOut = useCallback(async () => {
    await client.POST('/v2/auth/logout');
    setExpired(false);
    setUser(null);
  }, [client]);

  const session = useMemo(
    () => ({ client, config, user, expired, signIn, signOut }),
    [client, config, user, expired, signIn, signOut],
  );
  return <SessionContext.Provider value={session}>{children}</SessionContext.Provider>;
}
