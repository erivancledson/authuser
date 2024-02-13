package com.ead.authuser.controllers;

import com.ead.authuser.configs.security.JwtProvider;
import com.ead.authuser.configs.security.WebSecurityConfig;
import com.ead.authuser.enums.RoleType;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.RoleService;
import com.ead.authuser.services.UserService;
import com.ead.dtos.JwtDto;
import com.ead.dtos.LoginDto;
import com.ead.dtos.UserDto;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2 //do lombok
@RestController
@CrossOrigin(origins = "*", maxAge = 3600) //permite acesso de todas origens.
@RequestMapping("/auth")
public class AuthenticationController {


    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody @Validated(UserDto.UserView.RegistrationPost.class)
                                               @JsonView(UserDto.UserView.RegistrationPost.class) UserDto userDto){

        log.debug("POST registerUser userDto {} ", userDto.toString()); //exibe o objeto que vem da requisição

        if(userService.existsByUsername(userDto.getUsername())){ //verifica se existe o usuario no banco de dados
            log.warn("Username is Already Taken {} ", userDto.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username is Already Taken!");
        }

        if(userService.existsByEmail(userDto.getEmail())){ //verifica se existe o email no banco de dados
            log.warn("Email is Already Taken {} ", userDto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Email is Already Taken!");
        }
        //verifica se a role student existe
        RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error is Not Found."));

        //criptografa a senha que é passada pelo usuario
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel); //convert o user dto em user model
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.STUDENT);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.getRoles().add(roleModel); //pega a role model student
        userService.saveUser(userModel);
        log.debug("POST registerUser userId {} ", userModel.getUserId());
        log.info("User saved successfully userId {} ", userModel.getUserId()); //para ser exibido em prod
        return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate( //autentication manager faz a autenticacao por login e senha
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication); //seta a geracao
        String jwt = jwtProvider.generateJwt(authentication); //gera o token
        return ResponseEntity.ok(new JwtDto(jwt));
    }

    @GetMapping("/")
    public String index(){
        log.trace("TRACE"); //quando queremos rastrear uma parte do codigo, ele trás tudo de forma detalhado
        log.debug("DEBUG"); //trazer informações relevantes para os desenvolvedores. Quando precisa ver os valores de uma determinada variavel
        log.info("INFO"); //usado para produção, sobre fluxos que ocorreram na aplicação
        log.warn("WARN"); //de alerta: se teve algum processo que aconteceu mais de uma vez, se perdeu algo
        log.error("ERROR");
        return "Logging Spring Boot...";
    }

}
