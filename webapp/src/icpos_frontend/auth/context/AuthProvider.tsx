import { HttpAgent, Identity } from "@dfinity/agent";
import { ReactNode, createContext, useEffect, useState } from "react";

import { AuthClient } from "@dfinity/auth-client";
import { AuthContextType } from "../types/auth-context.type";
import { createAgent } from "@dfinity/utils";

// Mode
const development = process.env.DFX_NETWORK !== "ic";
// Identity provider URL
const IDENTITY_PROVIDER = development
  ? `http://${process.env.CANISTER_ID_INTERNET_IDENTITY}.localhost:4943`
  : "https://identity.ic0.app";

  console.log("MAGIC URL", IDENTITY_PROVIDER)

// Create a context for authentication
export const AuthContext = createContext<Partial<AuthContextType>>({});

// AuthProvider component that provides authentication functionality to its children
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  // State variables
  const [authClient, setAuthClient] = useState<AuthClient | undefined>();
  const [identity, setIdentity] = useState<Identity | undefined>(undefined);
  const [agent, setAgent] = useState<HttpAgent | undefined>(undefined);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | undefined>(
    undefined
  );
  const [hasLoggedIn, setHasLoggedIn] = useState<boolean>(false);

  useEffect(() => {
    const checkAuth = async () => {
      const client = await AuthClient.create({
        idleOptions: {
          disableDefaultIdleCallback: true,
          disableIdle: true,
        },
      });

      const isAuthenticated = await client.isAuthenticated();

      if (isAuthenticated) {
        const identity = client.getIdentity();
        const agent = await createAgent({
          identity,
          host: development ? "http://localhost:4943" : "https:icp0.io",
        });

        if (development) {
          await agent.fetchRootKey();
        }

        setAuthClient(client);
        setIdentity(identity);
        setAgent(agent);
        setIsAuthenticated(true);
        setHasLoggedIn(true);
      } else {
        setAuthClient(client);
        setIsAuthenticated(false);
      }
    };

    checkAuth();
  }, []);

  // Function to handle login
  const login = () => {
    if (!authClient) return;
    authClient.login({
      identityProvider: IDENTITY_PROVIDER,
      onSuccess: async () => {
        // Save the identity
        const identity = authClient.getIdentity();
        setIdentity(identity);

        // Create an agent
        const agent = await createAgent({
          identity,
          host: development ? "http://localhost:4943" : "https:icp0.io",
        });
        if (development) {
          await agent.fetchRootKey();
        }
        setAgent(agent);

        setIsAuthenticated(true);
        setHasLoggedIn(true);

        // Store login state
        localStorage.setItem("isLoggedIn", "true");
      },
    });
  };

  // Function to handle logout
  const logout = () => {
    authClient?.logout();
    setIdentity(undefined);
    setIsAuthenticated(false);
    setHasLoggedIn(false);
    localStorage.removeItem("isLoggedIn");
  };
  /*

  // Provide the auth context to children
  console.log("LOGIN DATA")
  let toSave = {authClient,
    identity,
    agent,
    isAuthenticated,
    hasLoggedIn,}
  
  if(hasLoggedIn)
    localStorage.setItem("loginData", JSON.stringify(toSave))
  else 
  */

  return (
    <AuthContext.Provider
      value={{
        authClient,
        identity,
        agent,
        isAuthenticated,
        hasLoggedIn,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
