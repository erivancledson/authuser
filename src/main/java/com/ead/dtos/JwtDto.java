package com.ead.dtos;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor //para serem criados construtores apenas com os campos obrigatorios
public class JwtDto {

    @NonNull
    private String token;
    private String type = "Bearer";

}
