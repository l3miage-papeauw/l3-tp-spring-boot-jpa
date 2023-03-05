package fr.uga.l3miage.library.authors;

import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.library.books.BookDTO;
import fr.uga.l3miage.library.books.BooksMapper;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.DeleteAuthorException;
import fr.uga.l3miage.library.service.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class AuthorsController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;
    private final BooksMapper booksMapper;

    @Autowired
    public AuthorsController(AuthorService authorService, AuthorMapper authorMapper, BooksMapper booksMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
        this.booksMapper = booksMapper;
    }

    @GetMapping("/authors")
    public Collection<AuthorDTO> authors(@RequestParam(value = "q", required = false) String query) {
        Collection<Author> authors;
        if (query == null) {
            authors = authorService.list();
        } else {
            authors = authorService.searchByName(query);
        }
        return authors.stream()
                .map(authorMapper::entityToDTO)
                .toList();
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/authors/{id}")
    public AuthorDTO author(@PathVariable("id")  Long id)  {
        Author auteur = null;
        try {
            auteur = this.authorService.get(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }


        return authorMapper.entityToDTO(auteur);
    }

    @PostMapping("/authors")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDTO newAuthor(@RequestBody @Valid AuthorDTO author) {

        Author auteur = authorMapper.dtoToEntity(author);
        auteur = this.authorService.save(auteur);
        return authorMapper.entityToDTO(auteur);

    }

    @PutMapping("/authors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDTO updateAuthor(@RequestBody @Valid AuthorDTO  author, @PathVariable("id") Long id) throws EntityNotFoundException {
        if(id!= author.id()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        // Ce return pourrait être fait en plusieurs étapes, mais ici, on fait en une seule ligne les 2 "conversions" et l'update
        return authorMapper.entityToDTO(authorService.update(authorMapper.dtoToEntity(author)));
    }

    @DeleteMapping("/authors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable("id") Long id) throws EntityNotFoundException, DeleteAuthorException {
        var auteur = this.authorService.get(id);
        if(auteur==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        this.authorService.delete(id);
    }

    public Collection<BookDTO> books(Long authorId) {
        return Collections.emptyList();
    }

}
