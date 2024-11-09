import {
  Actor,
  ActorSubclass,
  AnonymousIdentity,
  HttpAgent,
} from "@dfinity/agent";

import React from "react";
import { BitcoinCanister } from "@dfinity/ckbtc";
import { Principal } from "@dfinity/principal";

export function useCkBtcLedgerAnon() {
  const [ckBtcLedger, setCkBtcLedger] = React.useState<
    ActorSubclass<_SERVICE> | undefined
  >();

  const createActor = (): ActorSubclass<_SERVICE> => {
    const agent = new HttpAgent({
      identity: new AnonymousIdentity(),
      host:
        process.env.DFX_NETWORK === "ic"
          ? "https://icp0.io"
          : "http://localhost:4943",
    });

    if (process.env.DFX_NETWORK !== "ic") {
      agent.fetchRootKey().catch((err) => {
        console.warn(
          "Unable to fetch root key. Check to ensure that your local replica is running"
        );
        console.error(err);
      });
    }
    // Creates an actor with using the candid interface and the HttpAgent
    return BitcoinCanister.create({
      agent,
      canisterId: Principal.fromText(process.env.CANISTER_ID_ICRC1_LEDGER!),
    });
  };

  React.useEffect(() => {
    const { actor } = createActor();
    setCkBtcLedger(actor);
  }, []);

  return { ckBtcLedger };
}
