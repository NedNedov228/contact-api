package com.xecore.projects.contact_api.services;

import com.xecore.projects.contact_api.models.Contact;
import com.xecore.projects.contact_api.repo.ContactRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.xecore.projects.contact_api.constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ContactsService {

    private final ContactRepository contactRepository;

    public Page<Contact> getAllContacts(int page,int size){
        return contactRepository.findAll(PageRequest.of(page,size, Sort.by("name")));
    }

    public Contact getContact(String id){
        return contactRepository.findById(id).orElseThrow(() -> new RuntimeException(("Contact not found")));
    }

    public Contact createContact(Contact c){
        return contactRepository.save(c);
    }

    public void deleteContact(Contact c){
        contactRepository.delete(c);
    }

    public String uploadPhoto(String id, MultipartFile file){
        log.info("Saving photo for user: "+id);
        Contact contact = getContact(id);
        String photoUrl = photoFunction.apply(id,file);

        contact.setPhotoUrl(photoUrl);

        contactRepository.save(contact);

        return photoUrl;
    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name-> "."+name.substring(filename.lastIndexOf(".")+1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id,image) ->{
      try {
          Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();

          if(!Files.exists(fileStorageLocation))
              Files.createDirectories(fileStorageLocation);

          String filename = id + fileExtension.apply(image.getOriginalFilename());
          Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename),REPLACE_EXISTING);

          return ServletUriComponentsBuilder.fromCurrentContextPath()
                  .path("/api/contacts/image/" + filename).toUriString();
      }catch (Exception e){
          throw new RuntimeException("Unable to save image");
      }
    };
}
