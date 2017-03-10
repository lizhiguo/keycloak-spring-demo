package org.keycloak.example.spring.api;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelos on 10/3/2017.
 */
@RestController("/realms")
public class KeycloakAdminApiController {

    private final Keycloak keycloak;

    @Autowired
    public KeycloakAdminApiController(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<List<RealmRepresentation>> createRealm(@RequestBody String name) {
        RealmRepresentation templateRealm = keycloak.realms().realm("spring-demo").toRepresentation();
        templateRealm.setUsers(new ArrayList<>());
        templateRealm.setClients(new ArrayList<>());
        templateRealm.setId(name);
        templateRealm.setRealm(name);
        templateRealm.setDisplayName(name);
        templateRealm.setEnabled(true);
        templateRealm.setSslRequired("external");
        templateRealm.setRegistrationAllowed(true);
        keycloak.realms().create(templateRealm);
        return new ResponseEntity<>(keycloak.realms().findAll(), HttpStatus.CREATED);
    }

}
