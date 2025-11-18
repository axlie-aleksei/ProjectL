package org.axlie.projectl.launcher_project;

import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.fs.store.FilesystemStore;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface DocStore extends ContentStore<Document, UUID>{

}