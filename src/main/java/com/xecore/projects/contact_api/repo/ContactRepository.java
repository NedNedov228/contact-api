package com.xecore.projects.contact_api.repo;

import com.xecore.projects.contact_api.models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact,String> {
    Optional<Contact> findById(String id);
}
