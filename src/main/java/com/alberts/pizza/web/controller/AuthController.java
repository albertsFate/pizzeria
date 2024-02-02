package com.alberts.pizza.web.controller;

import com.alberts.pizza.service.dto.LoginDTO;
import com.alberts.pizza.web.config.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }


    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginDTO loginDTO){
        //Se crea un UsernamePasswordAuthenticationToken a partir del usuario y contraseña que regresa en el requestbody
        UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        //Se crea un objeto de authentication que basicamente recupera el usuario mandado y compara la contraseña en bd contra la mandada en el dto
        Authentication authentication = this.authenticationManager.authenticate(login);

        //si se llega a esta linea el usuario se autentico correctamente
        System.out.println(authentication.isAuthenticated());
        //se imprime el usuario en la seguridad de spring
        System.out.println(authentication.getPrincipal());

        //se crea un JasonWebToken
        String jwt = this.jwtUtils.create(loginDTO.getUsername());

        //se asigna el JWT en la respuesta de la solicitud
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwt).build();
    }
}
