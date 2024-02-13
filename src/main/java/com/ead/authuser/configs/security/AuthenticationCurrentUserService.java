package com.ead.authuser.configs.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationCurrentUserService {
    //extrai informações do usuario que solicita informações pela api
    public UserDetailsImpl getCurrentUser(){ //retorna os dados do usuario UserDetailsImpl corrente
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //SecurityContextHolder = injeta a validação quando é feita do token
    }

    public Authentication getAuthentication(){
        //recebe o nome do usuario autenticado
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
