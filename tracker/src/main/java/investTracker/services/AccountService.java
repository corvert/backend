package investTracker.services;

import investTracker.dtos.AccountResponse;
import investTracker.dtos.CreateAccountRequest;

import java.util.List;

public interface AccountService {
    AccountResponse create(CreateAccountRequest request);

    List<AccountResponse> accountList();

    AccountResponse update(Long accountId, CreateAccountRequest request);
}
