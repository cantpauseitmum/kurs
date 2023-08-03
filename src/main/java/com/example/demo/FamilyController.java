package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping(value = "api/v1/family")
public class FamilyController {
    List<Family> familyList = new ArrayList<>();
    List<Member> members = new ArrayList<>();

    @PostConstruct
    public void loadFamily() {
        members.add(new Member("Adam", 12, "M"));
        members.add(new Member("Ania", 24, "K"));
        members.add(new Member("Wojtek", 11, "M"));
        familyList.add(new Family(UUID.randomUUID().toString(), "Kowalski", members));
        familyList.add(new Family(UUID.randomUUID().toString(), "Nowakowie", members));
    }

    @RequestMapping(value = "/getall", method = RequestMethod.GET)
    public List<Family> getAll() {
        return familyList;
    }

    @RequestMapping(value = "/getByName", method = RequestMethod.GET)
    public Family getByName(@RequestParam String familyName) {
        return familyList.stream().filter(family -> family.getName().equals(familyName)).findFirst().orElseThrow();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void createFamily(@RequestBody Family family, HttpServletResponse response) throws IOException {
        if (family.getName() != null && !family.getMembers().isEmpty()) {
            familyList.add(family);
            response.sendError(HttpServletResponse.SC_OK, "Dodano do listy");
            return;
        }
        response.sendError(HttpServletResponse.SC_CONFLICT, "Nazwa rodziny nie może być pusta oraz lista członków nie może być mniejsza niż 1");
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.PATCH, consumes = "application/json")
    public void editFamily(@RequestBody Map<Object, Object> fields, @PathVariable String id, HttpServletResponse response) throws IOException {
        Optional<Family> family = familyList.stream().filter(value -> value.getUid().equals(id)).findFirst();
        try {
            if (family.isPresent()) {
                fields.forEach((k, v) -> {
                    Field field = ReflectionUtils.findField(Family.class, (String) k);
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, family.get(), v);
                });
                response.sendError(HttpServletResponse.SC_OK, "Updated family information");
                return;
            }
        } catch (NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fields aren't correct");
            return;
        }
        response.sendError(HttpServletResponse.SC_NO_CONTENT, "Family doesn't exist");
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public void updateFamily(@PathVariable String id, @RequestBody Family family, HttpServletResponse response) throws IOException {
        for (int x = 0; x < familyList.size(); x++) {
            if (familyList.get(x).getUid().equals(id)) {
                familyList.set(x, family);
                response.sendError(HttpServletResponse.SC_OK, "Value updated");
                break;
            }
            if (familyList.size() - 1 == x) {
                familyList.add(family);
                response.sendError(HttpServletResponse.SC_OK, "Value has been created");
            }
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String id, HttpServletResponse response) throws IOException {
        Optional<Family> familyOptional = familyList.stream().filter(value -> value.getUid().equals(id)).findFirst();
        if (familyOptional.isPresent()) {
            familyList.remove(familyOptional.get());
            response.sendError(HttpServletResponse.SC_OK);
            return;
        }
        response.sendError(HttpServletResponse.SC_CONFLICT, "Family doesn't exist");
    }

    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public ResponseEntity<Void> getGoogle() {
        URI location = URI.create("https://google.com");
        return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
    }

    @RequestMapping(value = "/getALLRD", method = RequestMethod.GET)
    public ResponseEntity<Void> getALLRD() {
        URI location = URI.create("/api/v1/family/getall");
        return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
    }

    @RequestMapping(value = "/getHeader", method = RequestMethod.GET)
    public void getHeader(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println(headerName + ": " + headerValue);
        }

        // Pobieranie wszystkich ciasteczek z żądania
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + ": " + cookie.getValue());
            }
        }
    }
}
