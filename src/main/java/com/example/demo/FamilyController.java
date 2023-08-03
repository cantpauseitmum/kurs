package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/family")
public class FamilyController {

    @RequestMapping(value = "/getall", method = RequestMethod.GET)
    public List<Family> getAll() {
        List<Family> familyList = new ArrayList<>();
        List<Member> members = new ArrayList<>();
        members.add(new Member("Adam", 12, "M"));
        members.add(new Member("Ania", 24, "K"));
        members.add(new Member("Wojtek", 11, "M"));
        familyList.add(new Family(UUID.randomUUID().toString(), "Kowalski", members));
        familyList.add(new Family(UUID.randomUUID().toString(), "Nowakowie", members));
        return familyList;
    }

    @RequestMapping(value = "/getByName", method = RequestMethod.GET)
    public Family getByName(@RequestParam String familyName) {
        List<Family> familyList = new ArrayList<>();
        List<Member> members = new ArrayList<>();
        members.add(new Member("Adam", 12, "M"));
        members.add(new Member("Ania", 24, "K"));
        members.add(new Member("Wojtek", 11, "M"));
        familyList.add(new Family(UUID.randomUUID().toString(), "Kowalski", members));
        familyList.add(new Family(UUID.randomUUID().toString(), "Nowakowie", members));
        return familyList.stream().filter(family -> family.getName().equals(familyName)).findFirst().orElseThrow();
    }
}
