package com.nunclear.escritores.service.support;

import com.nunclear.escritores.enums.AccessLevel;
import com.nunclear.escritores.enums.AccountState;
import com.nunclear.escritores.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EnumMapperService {

    public AccessLevel parseAccessLevel(String accessLevel) {
        try {
            return AccessLevel.valueOf(accessLevel.trim().toLowerCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new BadRequestException("accessLevel inválido");
        }
    }

    public AccountState parseAccountState(String accountState) {
        try {
            return AccountState.valueOf(accountState.trim().toLowerCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new BadRequestException("accountState inválido");
        }
    }
}