package net.turtlecoding.damgo.account.dto;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.turtlecoding.damgo.account.entity.Account;
import net.turtlecoding.damgo.account.enums.Gender;

import static net.turtlecoding.damgo.common.utils.TimeFormatter.formatToString;

@Value
@RequiredArgsConstructor
public class AccountInfoResponseDto {

    String name;
    String email;
    String contact;

    public AccountInfoResponseDto(Account account) {
        this.name = account.getName();
        this.email = account.getEmail();
        this.contact = account.getContact();
    }

}
