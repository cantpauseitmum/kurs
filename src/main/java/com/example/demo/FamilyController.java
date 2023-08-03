package com.example.demo;

import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequestMapping(value = "api/v1/family")
public class FamilyController {
    List<Family> familyList = new ArrayList<>();
    List<Member> members = new ArrayList<>();

    @RequestMapping(value = "/getall", method = RequestMethod.GET)
    public List<Family> getAll() {
        members.add(new Member("Adam", 12, "M"));
        members.add(new Member("Ania", 24, "K"));
        members.add(new Member("Wojtek", 11, "M"));
        familyList.add(new Family(UUID.randomUUID().toString(), "Kowalski", members));
        familyList.add(new Family(UUID.randomUUID().toString(), "Nowakowie", members));
        return familyList;
    }

    @RequestMapping(value = "/getByName", method = RequestMethod.GET)
    public Family getByName(@RequestParam String familyName) {
        members.add(new Member("Adam", 12, "M"));
        members.add(new Member("Ania", 24, "K"));
        members.add(new Member("Wojtek", 11, "M"));
        familyList.add(new Family(UUID.randomUUID().toString(), "Kowalski", members));
        familyList.add(new Family(UUID.randomUUID().toString(), "Nowakowie", members));
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
}
