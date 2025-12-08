package org.axlie.projectl.launcher_project;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/docs")
public class DocController {
    private final DocRepository docRepository;
    private final DocStore docStore;

    public DocController(DocRepository docRepository, DocStore docStore) {
        this.docRepository = docRepository;
        this.docStore = docStore;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDoc( @RequestParam("file") MultipartFile file) throws IOException {
        //create new entity document
        Document doc = new Document();
        doc.setMimeType(file.getContentType());
        doc.setName(file.getOriginalFilename());
        docStore.setContent(doc, file.getInputStream());
        docRepository.save(doc);
        return ResponseEntity.ok("uploaded");
    }

    @GetMapping("/download/{id}")
    //response entity pozvoljaet return content disposition intputstreamresource is stream of data (our file)
    public ResponseEntity<InputStreamResource> downloadDoc(@PathVariable Long id) throws IOException {
        //from docrepository we take name and other data from docstore we take sam file
        Document doc = docRepository.findById(id).orElseThrow();
        Resource resource = docStore.getResource(doc);
        //returning data of file and file
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getMimeType()))
                .header("Content-Disposition", "attachment; filename=\"" +
                        doc.getName())
                .contentLength(resource.contentLength())
                .body(new InputStreamResource(resource.getInputStream()));

    }

}
