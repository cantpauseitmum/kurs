package com.example.demo;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
}
