package com.xecore.projects.contact_api.controllers;

import com.xecore.projects.contact_api.models.Contact;
import com.xecore.projects.contact_api.services.ContactsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.xecore.projects.contact_api.constant.Constant.PHOTO_DIRECTORY;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactsController {

    private final ContactsService contactsService;

    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        return ResponseEntity.created(URI.create("/api/contacts/user_ID"))
                .body(contactsService.createContact(contact));
    }

    @GetMapping
    public ResponseEntity<Page<Contact>> getContacts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size" ,defaultValue = "10") int size){
        return ResponseEntity.ok().body(contactsService.getAllContacts(page , size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContacts(@PathVariable(value = "id") String id)
                                                   {
        return ResponseEntity.ok().body(contactsService.getContact(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") String id,
                                              @RequestParam("file")MultipartFile file){

        return ResponseEntity.ok().body(contactsService.uploadPhoto(id, file));
    }

    @GetMapping(path = "/image/{filename}", produces = {IMAGE_PNG_VALUE,IMAGE_JPEG_VALUE})
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException{
        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable(value = "id") String id) {
        contactsService.deleteContact(contactsService.getContact(id));
        return ResponseEntity.noContent().build();
    }
}
