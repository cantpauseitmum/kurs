package com.example.demo;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
    public List<Family> getAll(HttpServletResponse response) {
        response.setHeader("Length", String.valueOf(familyList.size()));
        Cookie cookie = new Cookie("Length", String.valueOf(familyList.size()));
        cookie.setMaxAge(10);
        response.addCookie(cookie);
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
        if (family.getName() == null) {
            throw new FamilyLengthException("Pusta nazwa", new NullPointerException());
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

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() throws IOException {
//        Ładujemy plik z dysku lub innej lokalizacji
        File file = new File("src/main/resources/static/test.png");
        Resource resource = new FileSystemResource(file);

//        Tworzy ResponseEntity z zasobem i nagłówkami
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @GetMapping("/video")
    public StreamingResponseBody streamVideo(HttpServletResponse response) throws IOException {
        response.setContentType("video/mp4");
        InputStream videoFileSystem = new FileInputStream(new File("src/main/resources/static/video.mkv"));
        return outputStream -> {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = videoFileSystem.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, nRead);
            }
            videoFileSystem.close();
        };
    }
}
