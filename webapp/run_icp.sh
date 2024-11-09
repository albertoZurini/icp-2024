# dfx start --clean --background;
# dfx deploy --network local internet_identity;
export OWNER=$(dfx identity get-principal --network ic);
dfx deploy --network ic icrc1_ledger --argument '
  (variant {
    Init = record {
      token_name = "Local ckBTC";
      token_symbol = "LCKBTC";
      minting_account = record {
        owner = principal "'${OWNER}'";
      };
      initial_balances = vec {
        record {
          record {
            owner = principal "'${OWNER}'";
          };
          100_000_000_000;
        };
      };
      metadata = vec {};
      transfer_fee = 10;
      archive_options = record {
        trigger_threshold = 2000;
        num_blocks_to_archive = 1000;
        controller_id = principal "'${OWNER}'";
      }
    }
  })
';
export LEDGER=$(dfx canister id icrc1_ledger --network ic);
dfx deploy --network ic icrc1_index --argument '
  record {
   ledger_id = (principal "'${LEDGER}'");
  }
';
dfx deploy --network ic icpos --argument '(4027618000)';