package net.turtlecoding.damgo.account.service;

import lombok.RequiredArgsConstructor;
import net.turtlecoding.damgo.account.entity.Account;
import net.turtlecoding.damgo.account.repository.AccountRepository;
import net.turtlecoding.damgo.common.exception.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static net.turtlecoding.damgo.common.exception.enums.ExceptionStatus.NOT_FOUND_ACCOUNT;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ACCOUNT));
        return new AccountDetails(account);
    }
}
