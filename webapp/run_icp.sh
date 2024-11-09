dfx start --clean --background;
dfx deploy --network local internet_identity;
export OWNER=$(dfx identity get-principal);
dfx deploy --network local --specified-id mxzaz-hqaaa-aaaar-qaada-cai icrc1_ledger --argument '
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
dfx deploy --network local icrc1_index --argument '
  record {
   ledger_id = (principal "mxzaz-hqaaa-aaaar-qaada-cai");
  }
';
dfx deploy --network local icpos --argument '(0)';
dfx canister --network local call icpos setCourierApiKey "pk_prod_...";